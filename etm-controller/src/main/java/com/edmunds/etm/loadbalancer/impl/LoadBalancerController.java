/*
 * Copyright 2011 Edmunds.com, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.edmunds.etm.loadbalancer.impl;

import com.edmunds.common.configuration.api.EnvironmentConfiguration;
import com.edmunds.etm.loadbalancer.api.LoadBalancerConfig;
import com.edmunds.etm.loadbalancer.api.LoadBalancerConnection;
import com.edmunds.etm.loadbalancer.api.PoolMember;
import com.edmunds.etm.loadbalancer.api.PoolMemberExistsException;
import com.edmunds.etm.loadbalancer.api.PoolMemberNotFoundException;
import com.edmunds.etm.loadbalancer.api.VirtualServer;
import com.edmunds.etm.loadbalancer.api.VirtualServerConfig;
import com.edmunds.etm.loadbalancer.api.VirtualServerExistsException;
import com.edmunds.etm.loadbalancer.api.VirtualServerNotFoundException;
import com.edmunds.etm.management.api.HostAddress;
import com.edmunds.etm.management.api.HttpMonitor;
import com.edmunds.etm.management.api.ManagementPoolMember;
import com.edmunds.etm.management.api.ManagementVip;
import com.edmunds.etm.management.api.ManagementVips;
import com.edmunds.etm.management.api.MavenModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import static com.edmunds.etm.management.api.ManagementLoadBalancerState.ACTIVE;
import static com.edmunds.etm.management.api.ManagementVipType.COMPLETE;

/**
 * Controls a load balancer via a {@link com.edmunds.etm.loadbalancer.api.LoadBalancerConnection}.
 *
 * @author David Trott
 * @author Ryan Holmes
 */
@Component
public class LoadBalancerController {

    public static final String VIRTUAL_SERVER_NAME_PREFIX = "etm";

    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerController.class);

    private final LoadBalancerConnection connection;
    private final LoadBalancerConfig loadBalancerConfig;
    private EnvironmentConfiguration environment;

    private Map<String, VirtualServer> virtualServersByName;

    @Autowired
    public LoadBalancerController(LoadBalancerConnection connection,
                                  LoadBalancerConfig loadBalancerConfig,
                                  EnvironmentConfiguration environment) {
        this.connection = connection;
        this.loadBalancerConfig = loadBalancerConfig;
        this.environment = environment;
    }

    /**
     * Updates the load balancer with the specified set of vips.
     *
     * @param deltaVips delta between the last set of active vips and the desired vips
     * @param validate  true to validate that existing vips are configured correctly, false to skip validation
     * @param delete    true to process vip deletions, false to ignore
     * @return the current set of active vips or null if the operation could not be completed
     */
    public ManagementVips updateLoadBalancerConfiguration(ManagementVips deltaVips, boolean validate, boolean delete) {

        Validate.notNull(deltaVips, "deltaVips is null");

        logger.info("Updating load balancer configuration");

        // Connect to an active load balancer
        if (!connection.connect()) {
            logger.error("Cannot connect to an active load balancer");
            return null;
        }

        // Check that the load balancer is active
        if (!connection.isActive()) {
            logger.error("Load balancer not active before updating configuration");
            return null;
        }

        // Initialize internal state
        if (!initializeFromLoadBalancer()) {
            logger.error("Cannot initialize data from load balancer");
            return null;
        }

        // Clean up virtual servers
        deleteUnknownVirtualServers(deltaVips);

        // Process vips
        Set<ManagementVip> updatedVips = processVips(deltaVips, validate, delete);

        // Check that the load balancer is still active
        if (!connection.isActive()) {
            logger.error("Load balancer not active after updating configuration");
            return null;
        }

        // Save and synchronize the new configuration
        connection.saveConfiguration();

        // Return the active vips
        return new ManagementVips(COMPLETE, updatedVips);
    }

    private boolean initializeFromLoadBalancer() {
        // Initialize the address pool
        Set<VirtualServer> allVirtualServers;
        try {
            allVirtualServers = connection.getAllVirtualServers();
        } catch (RemoteException e) {
            logger.error("Failed to get all virtual servers", e);
            return false;
        }

        // Initialize the map of ETM virtual servers
        virtualServersByName = createEtmVirtualServerMap(allVirtualServers);
        return true;
    }

    private Set<ManagementVip> processVips(ManagementVips deltaVips,
                                           boolean validateExisting,
                                           boolean deleteVips) {
        Set<ManagementVip> updatedVips = Sets.newHashSet();
        for (ManagementVip vip : deltaVips.getVips()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Processing vip: %s", vip));
            }

            ManagementVip updatedVip;
            switch (vip.getLoadBalancerState()) {
                case CREATE_REQUEST:
                    updatedVip = createVirtualServer(vip);
                    break;
                case DELETE_REQUEST:
                    if (deleteVips) {
                        updatedVip = deleteVirtualServer(vip);
                    } else {
                        // preserve the original vip
                        updatedVip = vip;
                    }
                    break;
                case ACTIVE:
                    updatedVip = updatePoolMembers(vip, validateExisting);
                    break;
                default:
                    throw new IllegalStateException("Unexpected State: " + vip.getLoadBalancerState());
            }

            if (updatedVip != null) {
                updatedVips.add(updatedVip);
            }
        }
        return updatedVips;
    }

    private ManagementVip updatePoolMembers(ManagementVip vip, boolean verify) {

        // Check that the virtual server exists
        String serverName = createServerName(vip.getMavenModule());
        VirtualServer vs = virtualServersByName.get(serverName);
        if (vs == null) {
            return createVirtualServer(vip);
        }

        if (verify) {
            validateVirtualServer(vip);
        }

        Collection<ManagementPoolMember> updatedMembers = Lists.newArrayList();

        for (ManagementPoolMember poolMember : vip.getPoolMembers().values()) {

            ManagementPoolMember updatedPoolMember;
            switch (poolMember.getLoadBalancerState()) {
                case CREATE_REQUEST:
                    updatedPoolMember = addPoolMember(vip, poolMember);
                    break;
                case DELETE_REQUEST:
                    updatedPoolMember = removePoolMember(vip, poolMember);
                    // Don't add deletes
                    break;
                case ACTIVE:
                    updatedPoolMember = poolMember;
                    break;
                default:
                    throw new IllegalStateException("Unexpected State: " + poolMember.getLoadBalancerState());
            }
            if (updatedPoolMember != null) {
                updatedMembers.add(updatedPoolMember);
            }
        }
        return new ManagementVip(ACTIVE, vip.getMavenModule(), vs.getHostAddress(),
                updatedMembers, vip.getRootContext(), vip.getRules(), vip.getHttpMonitor());
    }

    private ManagementVip createVirtualServer(ManagementVip vip) {
        final String serverName = createServerName(vip.getMavenModule());
        final SortedSet<PoolMember> poolMembers = vipToPoolMembers(vip);
        final HttpMonitor httpMonitor = vip.getHttpMonitor();
        logger.info(String.format("Creating virtual server: %s", serverName));

        if (!removeExistingVirtualServer(serverName)) {
            return null;
        }

        final VirtualServer vs = createVirtualServerInternal(serverName, poolMembers, httpMonitor);
        if (vs == null) {
            return null;
        }

        virtualServersByName.put(serverName, vs);

        final Collection<ManagementPoolMember> updatedMembers = Lists.newArrayList();
        for (PoolMember member : vs.getPoolMembers()) {
            updatedMembers.add(new ManagementPoolMember(ACTIVE, member.getHostAddress()));
        }

        return new ManagementVip(ACTIVE, vip.getMavenModule(), vs.getHostAddress(),
                updatedMembers, vip.getRootContext(), vip.getRules(), vip.getHttpMonitor());
    }

    private boolean removeExistingVirtualServer(String serverName) {
        try {
            // Check for existing virtual server
            final VirtualServer vs = virtualServersByName.get(serverName);
            if (vs != null) {
                logger.warn(String.format("Replacing existing virtual server: %s", serverName));
                deleteVirtualServer(vs);
            }
            return true;
        } catch (RemoteException e) {
            logger.error(String.format("Failed to delete virtual server: %s", serverName), e);
        }
        return false;
    }

    private VirtualServer createVirtualServerInternal(
            String serverName, SortedSet<PoolMember> poolMembers, HttpMonitor httpMonitor) {
        try {
            // Create new virtual server (Note: HostAddress is null)
            final VirtualServer vsTemplate = new VirtualServer(serverName, null, poolMembers);
            final VirtualServerConfig vsConfig = new VirtualServerConfig(loadBalancerConfig.getDefaultVipPort());

            final HostAddress hostAddress = connection.createVirtualServer(vsTemplate, vsConfig, httpMonitor);

            return new VirtualServer(serverName, hostAddress, poolMembers);
        } catch (VirtualServerExistsException e) {
            logger.warn(String.format("Attempted to create duplicate virtual server: %s", serverName), e);
        } catch (RemoteException e) {
            logger.error(String.format("Failed to create virtual server: %s", serverName), e);
        }
        return null;
    }

    private void validateVirtualServer(ManagementVip vip) {
        String serverName = createServerName(vip.getMavenModule());
        VirtualServer vs = virtualServersByName.get(serverName);

        connection.verifyVirtualServer(vs, vip.getHttpMonitor());
    }

    private void deleteUnknownVirtualServers(ManagementVips vips) {
        Map<String, ManagementVip> vipsByServerName = createVipServerNameMap(vips);
        Set<VirtualServer> virtualServers = Sets.newHashSet(virtualServersByName.values());

        for (VirtualServer vs : virtualServers) {
            if (!vipsByServerName.containsKey(vs.getName())) {
                try {
                    deleteVirtualServer(vs);
                } catch (RemoteException e) {
                    logger.error(String.format("Failed to delete virtual server: %s", vs.getName()), e);
                }
            }
        }
    }

    private ManagementVip deleteVirtualServer(ManagementVip vip) {
        String serverName = createServerName(vip.getMavenModule());
        try {
            final SortedSet<PoolMember> poolMembers = vipToPoolMembers(vip);
            deleteVirtualServer(new VirtualServer(serverName, vip.getHostAddress(), poolMembers));
        } catch (RemoteException e) {
            logger.error(String.format("Failed to delete virtual server: %s", serverName), e);
            return new ManagementVip(ACTIVE, vip.getMavenModule(), vip.getHostAddress(),
                    vip.getPoolMembers().values(), vip.getRootContext(), vip.getRules(), vip.getHttpMonitor());
        }
        return null;
    }

    private void deleteVirtualServer(VirtualServer vs) throws RemoteException {
        logger.info(String.format("Deleting virtual server: %s", vs.getName()));
        try {
            connection.deleteVirtualServer(vs);
            virtualServersByName.remove(vs.getName());
        } catch (VirtualServerNotFoundException e) {
            logger.warn(String.format("Attempted to delete nonexistent virtual server: %s", vs.getName()));
        }
    }

    private ManagementPoolMember addPoolMember(ManagementVip vip, ManagementPoolMember poolMember) {
        String serverName = createServerName(vip.getMavenModule());
        HostAddress memberAddress = poolMember.getHostAddress();
        PoolMember pm = new PoolMember(memberAddress);
        logger.info(String.format("Adding pool member %s to virtual server: %s", pm, serverName));
        try {
            connection.addPoolMember(serverName, pm);
        } catch (PoolMemberExistsException e) {
            logger.warn(String.format(
                    "Attempted to add duplicate pool member %s to virtual server: %s", pm, serverName));
        } catch (RemoteException e) {
            logger.error(String.format(
                    "Failed to add pool member %s to virtual server: %s", pm, serverName), e);
            return null;
        }
        return new ManagementPoolMember(ACTIVE, memberAddress);
    }

    private ManagementPoolMember removePoolMember(ManagementVip vip, ManagementPoolMember poolMember) {
        String serverName = createServerName(vip.getMavenModule());
        HostAddress memberAddress = poolMember.getHostAddress();
        PoolMember pm = new PoolMember(memberAddress);
        logger.info(String.format("Removing pool member %s from virtual server: %s", pm, serverName));
        try {
            connection.removePoolMember(serverName, pm);
        } catch (PoolMemberNotFoundException e) {
            logger.warn(String.format(
                    "Attempted to remove nonexistent pool member %s from virtual server: %s", pm, serverName));
        } catch (RemoteException e) {
            logger.error(String.format(
                    "Failed to delete pool member %s from virtual server: %s", pm, serverName), e);
            return poolMember;
        }
        return null;
    }

    private Map<String, VirtualServer> createEtmVirtualServerMap(Set<VirtualServer> virtualServers) {
        Map<String, VirtualServer> map = Maps.newHashMapWithExpectedSize(virtualServers.size());

        for (VirtualServer vs : virtualServers) {
            String name = vs.getName();
            if (name.startsWith(createServerEnvironmentPrefix())) {
                map.put(vs.getName(), vs);
            }
        }
        return map;
    }

    private Map<String, ManagementVip> createVipServerNameMap(ManagementVips vips) {
        Map<String, ManagementVip> map = Maps.newHashMapWithExpectedSize(vips.getVips().size());
        for (ManagementVip vip : vips.getVips()) {
            String serverName = createServerName(vip.getMavenModule());
            map.put(serverName, vip);
        }
        return map;
    }

    private SortedSet<PoolMember> vipToPoolMembers(ManagementVip vip) {
        Collection<ManagementPoolMember> managementPoolMembers = vip.getPoolMembers().values();
        SortedSet<PoolMember> poolMembers = Sets.newTreeSet();
        for (ManagementPoolMember mpm : managementPoolMembers) {
            poolMembers.add(new PoolMember(mpm.getHostAddress()));
        }
        return poolMembers;
    }

    private String createServerEnvironmentPrefix() {
        return VirtualServer.createServerEnvironmentPrefix(VIRTUAL_SERVER_NAME_PREFIX, environment);
    }

    private String createServerName(MavenModule mavenModule) {
        return VirtualServer.createServerName(VIRTUAL_SERVER_NAME_PREFIX, mavenModule, environment);
    }
}
