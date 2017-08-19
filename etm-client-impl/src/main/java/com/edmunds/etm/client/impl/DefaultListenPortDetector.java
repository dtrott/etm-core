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

/**
 * Default detector that returns port 8080 which is the normal port jetty and tomcat run on.
 */
public class DefaultListenPortDetector implements ListenPortDetector {
    private static final int DEFAULT_PORT_NUMBER = 8080;

    @Override
    public int getListenPort() {
        try {
            final String propListenPort = System.getProperty("listenPort");

            if (propListenPort != null && !propListenPort.isEmpty()) {
                final int port = Integer.parseInt(propListenPort);

                if (port > 0) {
                    return port;
                }
            }
        } catch (NumberFormatException e) {
            return DEFAULT_PORT_NUMBER;
        }
        return DEFAULT_PORT_NUMBER;
    }
}
