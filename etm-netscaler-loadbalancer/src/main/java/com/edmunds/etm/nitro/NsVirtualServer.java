package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.lb.lbvserver;
import com.citrix.netscaler.nitro.resource.config.lb.lbvserver_service_binding;
import com.citrix.netscaler.nitro.service.nitro_service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static com.edmunds.etm.nitro.Suppress.suppress;

public class NsVirtualServer {
    private nitro_service nitroService;
    private final lbvserver internal;

    public NsVirtualServer() {
        this.internal = new lbvserver();
    }

    NsVirtualServer(nitro_service nitroService, lbvserver internal) {
        this.nitroService = nitroService;
        this.internal = internal;
    }

    lbvserver getInternal(nitro_service nitroService) {
        this.nitroService = nitroService;
        return internal;
    }

    public String getName() {
        return suppress(internal::get_name);
    }

    public void setName(String name) {
        suppress(() -> this.internal.set_name(name));
    }

    public NsServiceType getServiceType() {
        return NsServiceType.valueOf(suppress(internal::get_servicetype));
    }

    public void setServiceType(NsServiceType serviceType) {
        suppress(() -> internal.set_servicetype(serviceType.name()));
    }

    public String getIpV46() {
        return suppress(internal::get_ipv46);
    }

    public void setIpV46(String ip) {
        suppress(() -> internal.set_ipv46(ip));
    }

    public Integer getPort() {
        return suppress(internal::get_port);
    }

    public void setPort(Integer port) {
        suppress(() -> internal.set_port(port));
    }

    public void setPort(int port) {
        suppress(() -> internal.set_port(port));
    }

    public LbMethod getLbMethod() {
        return LbMethod.valueOf(suppress(internal::get_lbmethod));
    }

    public void setLbMethod(LbMethod lbMethod) {
        suppress(() -> internal.set_lbmethod(lbMethod.name()));
    }

    public NsEffectiveState getEffectiveState() {
        return NsEffectiveState.remoteValueOf(suppress(internal::get_effectivestate));
    }

    public List<NsVirtualServerBinding> getBindings() throws NitroException {
        final lbvserver_service_binding[] bindingArray = wrap(() ->
                lbvserver_service_binding.get(nitroService, getName()));

        if (bindingArray == null) {
            return Collections.emptyList();
        }

        return Stream.of(bindingArray)
                .map(x -> new NsVirtualServerBinding(nitroService, this, x))
                .collect(Collectors.toList());
    }

    public void bindService(NsService service) throws NitroException {
        bindService(service.getName());
    }

    public void bindService(String serviceName) throws NitroException {
        final lbvserver_service_binding binding = new lbvserver_service_binding();

        suppress(() -> {
            binding.set_name(getName());
            binding.set_servicename(serviceName);
        });

        wrap("Binding Failed", () -> lbvserver_service_binding.add(nitroService, binding));
    }

    public void delete() throws NitroException {
        wrap("Delete Virtual Server Failed", () -> lbvserver.delete(nitroService, internal));
    }
}
