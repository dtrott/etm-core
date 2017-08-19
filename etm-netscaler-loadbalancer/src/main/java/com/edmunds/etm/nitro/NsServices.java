package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.basic.service;
import com.citrix.netscaler.nitro.service.nitro_service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static java.util.stream.Collectors.toList;

public class NsServices {

    private final nitro_service nitroService;

    NsServices(nitro_service nitroService) {
        this.nitroService = nitroService;
    }

    public List<NsService> list() throws NitroException {
        final service[] serviceArray = wrap(() -> service.get(nitroService));

        if (serviceArray == null) {
            return Collections.emptyList();
        }

        return Stream.of(serviceArray)
                .map(x -> new NsService(nitroService, x))
                .collect(toList());
    }

    public void create(NsService newService) throws NitroException {
        wrap("Create Service Failed", () -> service.add(nitroService, newService.getInternal(nitroService)));
    }
}
