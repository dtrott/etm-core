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

import com.edmunds.etm.loadbalancer.api.LoadBalancerConfig;
import lombok.Data;

/**
 * Identity Configuration for load balancer.
 */
@Data
public class NetScalerConfig implements LoadBalancerConfig {

    /**
     * NetScaler admin host/ip.
     */
    private String netScalerHost;

    /**
     * Username to log into the NetScaler.
     */

    private String netScalerUsername = "nsroot";
    /**
     * Password to log into the NetScaler.
     */
    private String netScalerPassword = "nsroot";

    /**
     * Beginning of ip pool.
     */
    private String ipPoolStart;

    /**
     * End of ip pool.
     */
    private String ipPoolEnd;

    /**
     * Default port to assign to the new virtual service
     */
    private int defaultVipPort = 80;
}
