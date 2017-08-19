package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.exception.nitro_exception;
import com.citrix.netscaler.nitro.service.nitro_service;
import lombok.Getter;

import static com.edmunds.etm.nitro.NitroException.wrap;

public class NitroService {
    private final nitro_service internal;

    @Getter
    private final NsVirtualServers virtualServers;

    @Getter
    private final NsServers servers;

    @Getter
    private final NsServices services;

    @Getter
    private final NsMonitors monitors;

    public NitroService(String ip) throws NitroException {
        try {
            this.internal = new nitro_service(ip);
            this.virtualServers = new NsVirtualServers(internal);
            this.servers = new NsServers(internal);
            this.services = new NsServices(internal);
            this.monitors = new NsMonitors(internal);
        } catch (nitro_exception e) {
            throw new NitroException(e);
        }
    }

    public void login(String username, String password) throws NitroException {
        wrap("Login Failed", () -> this.internal.login(username, password));
    }
}
