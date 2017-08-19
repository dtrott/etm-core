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
package com.edmunds.etm.netscaler;

import com.edmunds.etm.loadbalancer.api.AvailabilityStatus;
import com.edmunds.etm.loadbalancer.api.LoadBalancerConnection;
import com.edmunds.etm.loadbalancer.api.PoolMember;
import com.edmunds.etm.loadbalancer.api.PoolMemberExistsException;
import com.edmunds.etm.loadbalancer.api.PoolMemberNotFoundException;
import com.edmunds.etm.loadbalancer.api.VirtualServer;
import com.edmunds.etm.loadbalancer.api.VirtualServerConfig;
import com.edmunds.etm.loadbalancer.api.VirtualServerExistsException;
import com.edmunds.etm.loadbalancer.api.VirtualServerNotFoundException;
import com.edmunds.etm.loadbalancer.impl.Inet4AddressPool;
import com.edmunds.etm.loadbalancer.impl.OrderedInet4Address;
import com.edmunds.etm.management.api.HostAddress;
import com.edmunds.etm.management.api.HttpMonitor;
import com.edmunds.etm.nitro.LbMethod;
import com.edmunds.etm.nitro.NitroException;
import com.edmunds.etm.nitro.NitroExceptionNoSuchResource;
import com.edmunds.etm.nitro.NitroExceptionResourceExists;
import com.edmunds.etm.nitro.NitroService;
import com.edmunds.etm.nitro.NsMonitor;
import com.edmunds.etm.nitro.NsMonitorType;
import com.edmunds.etm.nitro.NsServer;
import com.edmunds.etm.nitro.NsService;
import com.edmunds.etm.nitro.NsServiceType;
import com.edmunds.etm.nitro.NsVirtualServer;
import com.edmunds.etm.nitro.NsVirtualServerBinding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * NetScaler Load Balancer.
 *
 * @author David Trott
 */
@Component
@Slf4j
public class NetScalerLoadBalancer implements LoadBalancerConnection {

    private NitroService nitroService;
    private final NetScalerConfig config;
    private final Inet4AddressPool addressPool;

    @Autowired
    public NetScalerLoadBalancer(NetScalerConfig config) {
        this.config = config;
        this.addressPool = new Inet4AddressPool(config);
    }

    @Override
    public boolean connect() {
        try {
            this.nitroService = new NitroService(config.getNetScalerHost());
            this.nitroService.login(
                    config.getNetScalerUsername(),
                    config.getNetScalerPassword());
            return true;
        } catch (NitroException e) {
            log.error("Failed to connect to the NetScaler", e);
            return false;
        }
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public synchronized Set<VirtualServer> getAllVirtualServers() {
        try {
            return nitroService.getVirtualServers().list().stream()
                    .map(NetScalerLoadBalancer::toVirtualServer)
                    .collect(toSet());
        } catch (NitroException e) {
            return Collections.emptySet();
        }
    }

    @Override
    public synchronized VirtualServer getVirtualServer(final String serverName) {
        try {
            return toVirtualServer(nitroService.getVirtualServers().get(serverName));
        } catch (NitroException e) {
            return null;
        }
    }

    private static VirtualServer toVirtualServer(NsVirtualServer vs) {
        return new VirtualServer(
                vs.getName(),
                new HostAddress(vs.getIpV46(), vs.getPort()),
                getBindings(vs).stream()
                        .map(bind -> new PoolMember(new HostAddress(bind.getIpV46(), bind.getPort())))
                        .collect(toCollection(TreeSet::new)));
    }

    private static List<NsVirtualServerBinding> getBindings(NsVirtualServer vs) {
        try {
            final List<NsVirtualServerBinding> bindings = vs.getBindings();
            return bindings != null ? bindings : Collections.emptyList();
        } catch (NitroException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized boolean isVirtualServerDefined(String serverName) throws RemoteException {
        try {
            return nitroService.getVirtualServers().get(serverName) != null;
        } catch (NitroExceptionNoSuchResource e) {
            return false;
        } catch (NitroException e) {
            throw new RemoteException("Existence Check Failed", e);
        }
    }

    @Override
    public synchronized Map<String, AvailabilityStatus> getAvailabilityStatus(List<String> serverNames) throws
            VirtualServerNotFoundException, RemoteException {

        try {
            final String[] namesArray = serverNames.toArray(new String[serverNames.size()]);

            final Map<String, NsVirtualServer> virtualServers = nitroService.getVirtualServers().get(namesArray)
                    .stream().collect(toMap(
                            NsVirtualServer::getName,
                            x -> x));

            return serverNames.stream().collect(toMap(
                    x -> x,
                    x -> getAvailabilityStatus(virtualServers.get(x))));
        } catch (NitroExceptionNoSuchResource e) {
            // Probably won't happen.
            throw new VirtualServerNotFoundException("Get availability status failed", e);
        } catch (NitroException e) {
            throw new RemoteException("Get availability status failed", e);
        }
    }

    private static AvailabilityStatus getAvailabilityStatus(NsVirtualServer server) {
        if (server == null || server.getEffectiveState() == null) {
            return AvailabilityStatus.NONE;
        }

        switch (server.getEffectiveState()) {
            case UP:
                return AvailabilityStatus.AVAILABLE;
            case DOWN:
                return AvailabilityStatus.UNAVAILABLE;
            case DISABLED:
            case OUT_OF_SERVICE:
            case GOING_OUT_OF_SERVICE:
            case DOWN_WHEN_GOING_OUT_OF_SERVICE:
                return AvailabilityStatus.DISABLED;
            case BUSY:
            case UNKNOWN:
            case NS_EMPTY_STR:
                return AvailabilityStatus.UNKNOWN;
            default:
                return AvailabilityStatus.NONE;
        }
    }

    private static String getMonitorName(String serverName) {
        final String[] split = serverName.split("_");

        String value = "";
        if (split.length == 6 && !split[4].isEmpty()) {
            value = split[4];
        }

        if (value.isEmpty()) {
            value = "etm";
        }

        if (value.length() >= 16) {
            value = value.substring(0, 16);
        }

        return value + "-" + Integer.toString(serverName.hashCode());
    }

    @Override
    public synchronized HostAddress createVirtualServer(
            VirtualServer server, VirtualServerConfig virtualServerConfig, HttpMonitor httpMonitor) throws
            VirtualServerExistsException, RemoteException {

        if (httpMonitor != null) {
            final NsMonitor monitor = new NsMonitor();
            monitor.setMonitorName(getMonitorName(server.getName()));
            monitor.setType(NsMonitorType.HTTP_ECV);
            monitor.setSendString(httpMonitor.getHttpRequest());
            monitor.setReceiveString(httpMonitor.getHttpResponse());

            try {
                nitroService.getMonitors().create(monitor);
            } catch (NitroExceptionResourceExists e) {
                log.warn("Monitor already exists: " + server.getName(), e);
            } catch (NitroException e) {
                throw new RemoteException("Create Monitor Failed", e);
            }
        }

        final OrderedInet4Address address = addressPool.issueAddress();

        final NsVirtualServer nvs = new NsVirtualServer();
        nvs.setName(server.getName());
        nvs.setIpV46(address.toString());
        nvs.setLbMethod(LbMethod.LEASTCONNECTION);
        nvs.setPort(virtualServerConfig.getPort());
        nvs.setServiceType(NsServiceType.HTTP);

        try {
            nitroService.getVirtualServers().create(nvs);
        } catch (NitroExceptionResourceExists e) {
            throw new VirtualServerExistsException("Virtual Server Exists: " + server.getName(), e);
        } catch (NitroException e) {
            throw new RemoteException("Create Virtual Server Failed", e);
        }

        for (PoolMember poolMember : server.getPoolMembers()) {
            try {
                addPoolMember(server.getName(), poolMember);
            } catch (PoolMemberExistsException e) {
            }
        }

        return new HostAddress(address.toString(), virtualServerConfig.getPort());
    }

    @Override
    public void verifyVirtualServer(VirtualServer server, HttpMonitor httpMonitor) {
        // TODO fix this.
        // No-op
    }

    @Override
    public synchronized void deleteVirtualServer(VirtualServer server) throws
            VirtualServerNotFoundException, RemoteException {

        for (PoolMember poolMember : server.getPoolMembers()) {
            try {
                removePoolMember(server.getName(), poolMember);
            } catch (PoolMemberNotFoundException e) {
            }
        }

        try {
            nitroService.getVirtualServers().get(server.getName()).delete();
        } catch (NitroExceptionNoSuchResource e) {
            throw new VirtualServerNotFoundException(server.getName(), e);
        } catch (NitroException e) {
            throw new VirtualServerNotFoundException("Delete Virtual Server Failed", e);
        }

        addressPool.releaseAddress(server.getHostAddress().getIpAddress());

        final String monitorName = getMonitorName(server.getName());
        try {
            nitroService.getMonitors().get(monitorName).delete();
        } catch (NitroExceptionNoSuchResource e) {
            log.info("Monitor does not exist: " + monitorName);
        } catch (NitroException e) {
            throw new RemoteException("Delete Monitor Failed", e);
        }
    }

    @Override
    public synchronized void addPoolMember(String serverName, PoolMember member) throws
            PoolMemberExistsException, RemoteException {

        final NsMonitor monitor = getMonitor(serverName);

        final String ipAddress = member.getHostAddress().getIpAddress();
        final String serviceName = "ip_" + ipAddress;

        final NsServer nsServer = new NsServer();
        nsServer.setName(serviceName);
        nsServer.setIpAddress(ipAddress);

        final NsService nsService = new NsService();
        nsService.setName(serviceName);
        nsService.setServiceType(NsServiceType.HTTP);
        nsService.setPort(member.getHostAddress().getPort());
        nsService.setServer(nsServer);

        try {
            final NsVirtualServer nsVirtualServer = nitroService.getVirtualServers().get(serverName);

            try {
                nitroService.getServers().create(nsServer);
            } catch (NitroExceptionResourceExists e) {
                // Ignore since we still need to handle the service and binding
            }

            try {
                nitroService.getServices().create(nsService);
            } catch (NitroExceptionResourceExists e) {
                // Ignore since we still need to handle the service and binding
            }

            if (monitor != null) {
                nsService.bindMonitor(monitor);
            }

            nsVirtualServer.bindService(nsService);
        } catch (NitroExceptionResourceExists e) {
            throw new PoolMemberExistsException(serviceName, e);
        } catch (NitroException e) {
            throw new RemoteException("Add Pool Member Failed", e);
        }
    }

    private NsMonitor getMonitor(String serverName) throws RemoteException {
        try {
            final String monitorName = getMonitorName(serverName);
            return nitroService.getMonitors().get(monitorName);
        } catch (NitroExceptionNoSuchResource e) {
            return null;
        } catch (NitroException e) {
            throw new RemoteException("Unable to retrieve Monitor", e);
        }
    }

    @Override
    public synchronized void removePoolMember(String serverName, PoolMember member) throws
            PoolMemberNotFoundException, RemoteException {

        final String ipAddress = member.getHostAddress().getIpAddress();
        final String serviceName = "ip_" + ipAddress;

        try {
            final NsServer nsServer;
            nsServer = nitroService.getServers().get(serviceName);
            nsServer.delete();
        } catch (NitroExceptionNoSuchResource e) {
            throw new PoolMemberNotFoundException(serviceName, e);
        } catch (NitroException e) {
            throw new RemoteException("Delete Pool Member Failed", e);
        }
    }

    @Override
    public boolean saveConfiguration() {
        // No Op
        return true;
    }
}
