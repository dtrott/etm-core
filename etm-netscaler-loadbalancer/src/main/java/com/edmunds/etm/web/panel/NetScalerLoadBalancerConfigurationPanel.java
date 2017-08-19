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
package com.edmunds.etm.web.panel;

import com.edmunds.etm.netscaler.NetScalerConfig;
import org.apache.click.control.Panel;

public class NetScalerLoadBalancerConfigurationPanel extends Panel implements LoadBalancerConfigurationPanel {
    public NetScalerLoadBalancerConfigurationPanel(String name, NetScalerConfig config) {
        super(name);
        setTemplate("/panel/netscaler-lb-configuration-panel.htm");

        addModel("netScalerHost", config.getNetScalerHost());
        addModel("defaultVipPort", config.getDefaultVipPort());
        addModel("ipPoolStart", config.getIpPoolStart());
        addModel("ipPoolEnd", config.getIpPoolEnd());
    }
}
