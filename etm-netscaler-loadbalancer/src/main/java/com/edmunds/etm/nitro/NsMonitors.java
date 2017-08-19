package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.lb.lbmonitor;
import com.citrix.netscaler.nitro.service.nitro_service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.edmunds.etm.nitro.NitroException.wrap;

public class NsMonitors {

    private final nitro_service nitroService;

    public NsMonitors(nitro_service nitroService) {
        this.nitroService = nitroService;
    }

    public List<NsMonitor> list() throws NitroException {
        final lbmonitor[] array = wrap(() -> lbmonitor.get(nitroService));

        if (array == null) {
            return Collections.emptyList();
        }

        return Stream.of(array)
                .map(x -> new NsMonitor(nitroService, x))
                .collect(Collectors.toList());
    }

    public NsMonitor get(String name) throws NitroException {
        return new NsMonitor(
                nitroService,
                wrap(() -> lbmonitor.get(nitroService, name)));
    }

    public void create(NsMonitor monitor) throws NitroException {
        wrap("Create Monitor Failed", () -> lbmonitor.add(nitroService, monitor.getInternal(nitroService)));
    }
}

