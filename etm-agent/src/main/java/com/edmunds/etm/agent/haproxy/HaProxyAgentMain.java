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
package com.edmunds.etm.agent.haproxy;

import com.edmunds.etm.agent.impl.Agent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The HA Proxy agent application.
 * <p/>
 * This class provides a main method to start an agent.
 *
 * @author David Trott
 */
public final class HaProxyAgentMain {

    public static void main(String[] args) {

        // Create the Spring application context
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("ha-proxy-etm-agent-context.xml");
        ctx.registerShutdownHook();

        // Run the agent
        Agent agent;
        agent = (Agent) ctx.getBean("agent", Agent.class);
        agent.run();
    }

    private HaProxyAgentMain() {
        // This class should never be instantiated.
    }
}
