package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.basic.service_binding;
import com.citrix.netscaler.nitro.resource.config.basic.service_lbmonitor_binding;
import com.citrix.netscaler.nitro.service.nitro_service;

import java.util.List;
import java.util.stream.Stream;

import static com.edmunds.etm.nitro.Suppress.suppress;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class NsServiceBinding {

    private final nitro_service nitroService;
    private final NsService service;
    private final service_binding internal;

    NsServiceBinding(nitro_service nitroService, NsService service, service_binding internal) {
        this.nitroService = nitroService;
        this.service = service;
        this.internal = internal;
    }

    public NsService getService() {
        return service;
    }

    public String getServiceName() throws Exception {
        return internal.get_name();
    }

    public void setServiceName(String name) throws Exception {
        internal.set_name(name);
    }

    public List<NsServiceMonitorBinding> getMonitorBindings() {
        final service_lbmonitor_binding[] bindings = suppress(internal::get_service_lbmonitor_bindings);

        if (bindings == null) {
            return emptyList();
        }

        return Stream.of(bindings)
                .map(binding -> new NsServiceMonitorBinding(nitroService, service, binding))
                .collect(toList());
    }
}
