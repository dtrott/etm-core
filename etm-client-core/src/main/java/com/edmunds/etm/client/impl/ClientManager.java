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
package com.edmunds.etm.client.impl;

import com.edmunds.etm.common.api.ClientPaths;
import com.edmunds.etm.common.impl.ObjectSerializer;
import com.edmunds.etm.common.thrift.ClientConfigDto;
import com.edmunds.etm.common.thrift.HostAddressDto;
import com.edmunds.zookeeper.connection.ZooKeeperConnection;
import com.edmunds.zookeeper.connection.ZooKeeperConnectionListener;
import com.edmunds.zookeeper.connection.ZooKeeperConnectionState;
import com.edmunds.zookeeper.connection.ZooKeeperNodeInitializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Primary class that handles the ZooKeeper interactions from the etm client web applications.
 *
 * @author David Trott
 * @author Ryan Holmes
 */
@Component
public class ClientManager implements InitializingBean, ZooKeeperConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);

    public static String createNodeName(HostAddressDto address, String contextPath) {
        Validate.notNull(address, "Host address is null");
        Validate.notNull(contextPath, "Context path is null");

        // Format context path
        String ctxPath = contextPath.trim();

        // Remove leading slash
        if (ctxPath.startsWith("/")) {
            ctxPath = ctxPath.substring(1);
        }

        // Replace any additional slashes with dash
        ctxPath = ctxPath.replace("/", "-");

        StringBuilder sb = new StringBuilder(64);
        sb.append(address.getHost());
        sb.append(':');
        sb.append(address.getPort());
        if (StringUtils.isNotBlank(ctxPath)) {
            sb.append(':');
            sb.append(ctxPath);
        }
        sb.append("-");
        return sb.toString();
    }

    private final ZooKeeperConnection connection;
    private final ClientPaths clientPaths;
    private final ObjectSerializer objectSerializer;
    private final ClientSettings clientSettings;

    private ClientConfigDto clientConfigDto;
    private String connectedNodePath;

    /**
     * Auto-wire constructor.
     *
     * @param connection       the ZooKeeper connection
     * @param clientPaths      the client paths
     * @param objectSerializer object serializer
     * @param clientSettings   etm client configuration bean
     */
    @Autowired
    public ClientManager(ZooKeeperConnection connection,
                         ClientPaths clientPaths,
                         ObjectSerializer objectSerializer,
                         ClientSettings clientSettings) {
        this.connection = connection;
        this.clientPaths = clientPaths;
        this.objectSerializer = objectSerializer;
        this.clientSettings = clientSettings;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        connection.addInitializer(new ZooKeeperNodeInitializer(clientPaths.getStructuralPaths()));
        connection.addListener(this);
    }

    /**
     * Main call which creates the EPHEMERAL nodes representing the client.
     * <p/>
     * The nodes created by the client are deleted automatically when the client disconnects.
     *
     * @param configDto   client configuration object
     */
    public void register(ClientConfigDto configDto) {

        if (connection == null || configDto == null || !clientSettings.isEnabled()) {
            return;
        }

        clientConfigDto = configDto;
        connection.connect();
    }

    /**
     * Clean shutdown that disconnects from the etm server.
     */
    public void shutdown() {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void onConnectionStateChanged(ZooKeeperConnectionState state) {
        if (state == ZooKeeperConnectionState.INITIALIZED) {
            checkConnectedNode();
        }
    }

    /**
     * Checks whether the connected node exists and registers a watcher.
     */
    private void checkConnectedNode() {

        if (connectedNodePath == null) {
            createConnectedNode();
            return;
        }

        logger.debug("Checking if connected node exists");

        AsyncCallback.StatCallback cb = new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                onNodeExists(Code.get(rc), path);
            }
        };

        connection.exists(connectedNodePath, null, cb, null);
    }

    /**
     * Handles the node existence check.
     *
     * @param rc   result code
     * @param path node path
     */
    private void onNodeExists(Code rc, String path) {
        if (rc == Code.OK) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Connected node exists: %s", path));
            }
        } else if (rc == Code.NONODE) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Connected node does not exist: %s", path));
            }
            createConnectedNode();
        } else {
            logger.error("Unexpected ZooKeeper result: " + rc + " : " + path);
        }
    }

    /**
     * Creates the ephemeral sequential connected node.
     */
    private void createConnectedNode() {

        // CREATE: /connected/HOST:PORT:CTX
        String nodeName = createNodeName(clientConfigDto.getHostAddress(), clientConfigDto.getContextPath());
        String nodePath = clientPaths.getConnectedHost(nodeName);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Creating connected node: %s", nodePath));
        }

        byte[] nodeData;
        try {
            nodeData = objectSerializer.writeValue(clientConfigDto);
        } catch (IOException e) {
            String message = "Client config serialization failed";
            logger.error(message);
            throw new RuntimeException(message, e);
        }

        AsyncCallback.StringCallback cb = new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, String name) {
                onNodeCreated(Code.get(rc), path, name);
            }
        };
        connection.createEphemeralSequential(nodePath, nodeData, cb, null);
    }

    /**
     * Handles the node creation result.
     *
     * @param rc   result code
     * @param path node path requested for creation
     * @param name actual created node name (full path and sequential name)
     */
    private void onNodeCreated(Code rc, String path, String name) {
        if (rc == Code.OK || rc == Code.NODEEXISTS) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Created connected node: %s", name));
            }
            connectedNodePath = name;
        } else {
            logger.error("Unexpected ZooKeeper result: " + rc + " : " + path);
        }
    }
}
