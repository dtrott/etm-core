package com.edmunds.etm.nitro;

public enum LbMethod {
    /**
     * Distribute requests in rotation, regardless of the load. Weights can be assigned to services to enforce weighted round robin distribution.
     */
    ROUNDROBIN,

    /**
     * Select the service with the fewest connections. (default)
     */
    LEASTCONNECTION,

    /**
     * Select the service with the lowest average response time.
     */
    LEASTRESPONSETIME,

    /**
     * Select the service currently handling the least traffic.
     */
    LEASTBANDWIDTH,

    /**
     * Select the service currently serving the lowest number of packets per second.
     */
    LEASTPACKETS,

    /**
     * Base service selection on the SNMP metrics obtained by custom load monitors.
     */
    CUSTOMLOAD,

    /**
     * Select the service with the lowest response time. Response times are learned through monitoring probes. This method also takes the number of active connections into account.
     */
    LRTM,

    //Also available are a number of hashing methods, in which the appliance extracts a predetermined portion of the request, creates a hash of the portion, and then checks whether any previous requests had the same hash value. If it finds a match, it forwards the request to the service that served those previous requests. Following are the hashing methods:
    /**
     * Create a hash of the request URL (or part of the URL).
     */
    URLHASH,

    /**
     * Create a hash of the domain name in the request (or part of the domain name). The domain name is taken from either the URL or the Host header. If the domain name appears in both locations, the URL is preferred. If the request does not contain a domain name, the load balancing method defaults to LEASTCONNECTION.
     */
    DOMAINHASH,

    /**
     * Create a hash of the destination IP address in the IP header.
     */
    DESTINATIONIPHASH,

    /**
     * Create a hash of the source IP address in the IP header.
     */
    SOURCEIPHASH,

    /**
     * Extract a token from the request, create a hash of the token, and then select the service to which any previous requests with the same token hash value were sent.
     */
    TOKEN,

    /**
     * Create a hash of the string obtained by concatenating the source IP address and destination IP address in the IP header.
     */
    SRCIPDESTIPHASH,

    /**
     * Create a hash of the source IP address and source port in the IP header.
     */
    SRCIPSRCPORTHASH,

    /**
     * Create a hash of the SIP Call-ID header.
     */
    CALLIDHASH;
}
