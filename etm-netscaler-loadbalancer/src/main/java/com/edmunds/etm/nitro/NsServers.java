package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.basic.server;
import com.citrix.netscaler.nitro.service.nitro_service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static java.util.stream.Collectors.toList;

public class NsServers {

    private final nitro_service nitroService;

    public NsServers(nitro_service nitroService) {
        this.nitroService = nitroService;
    }

    public NsServer get(String name) throws NitroException {
        return new NsServer(
                nitroService,
                wrap(() -> server.get(nitroService, name)));
    }

    public List<NsServer> list() throws NitroException {
        final server[] serverArray = wrap(() -> server.get(nitroService));

        if (serverArray == null) {
            return Collections.emptyList();
        }

        return Stream.of(serverArray)
                .map(x -> new NsServer(nitroService, x))
                .collect(toList());
    }

    public void create(NsServer nsServer) throws NitroException {
        wrap("Create Server Failed", () -> server.add(nitroService, nsServer.getInternal(nitroService)));
    }
}
