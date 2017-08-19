package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.basic.server;
import com.citrix.netscaler.nitro.service.nitro_service;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static com.edmunds.etm.nitro.Suppress.suppress;

public class NsServer {
    private nitro_service nitroService;
    private final server internal;

    public NsServer() {
        this.internal = new server();
    }

    NsServer(nitro_service nitroService, server internal) {
        this.nitroService = nitroService;
        this.internal = internal;
    }

    server getInternal(nitro_service nitroService) {
        this.nitroService = nitroService;
        return internal;
    }

    public String getName() {
        return suppress(internal::get_name);
    }

    public void setName(String name) {
        suppress(() -> internal.set_name(name));
    }

    public String getIpAddress() {
        return suppress(internal::get_ipaddress);
    }

    public void setIpAddress(String ipAddress) {
        suppress(() -> internal.set_ipaddress(ipAddress));
    }

    public void delete() throws NitroException {
        wrap("Delete Server Failed", () -> server.delete(nitroService, internal));
    }
}
