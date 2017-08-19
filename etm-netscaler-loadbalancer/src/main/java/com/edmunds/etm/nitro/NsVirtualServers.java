package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.lb.lbvserver;
import com.citrix.netscaler.nitro.service.nitro_service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.edmunds.etm.nitro.NitroException.wrap;

public class NsVirtualServers {

    private final nitro_service nitroService;

    public NsVirtualServers(nitro_service nitroService) {
        this.nitroService = nitroService;
    }

    public List<NsVirtualServer> list() throws NitroException {
        final lbvserver[] vServerArray = wrap(() -> lbvserver.get(nitroService));

        if (vServerArray == null) {
            return Collections.emptyList();
        }

        return Stream.of(vServerArray)
                .map(x -> new NsVirtualServer(nitroService, x))
                .collect(Collectors.toList());
    }

    public NsVirtualServer get(String name) throws NitroException {
        return new NsVirtualServer(
                nitroService,
                wrap(() -> lbvserver.get(nitroService, name)));
    }

    public List<NsVirtualServer> get(String[] name) throws NitroException {
        final lbvserver[] vServerArray = wrap(() -> lbvserver.get(nitroService, name));

        if (vServerArray == null) {
            return Collections.emptyList();
        }

        return Stream.of(vServerArray)
                .map(x -> new NsVirtualServer(nitroService, x))
                .collect(Collectors.toList());
    }

    public void create(NsVirtualServer vServer) throws NitroException {
        wrap("Create Virtual Server Failed", () -> lbvserver.add(nitroService, vServer.getInternal(nitroService)));
    }
}

