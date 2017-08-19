package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.basic.service;
import com.citrix.netscaler.nitro.resource.config.basic.service_binding;
import com.citrix.netscaler.nitro.resource.config.basic.service_lbmonitor_binding;
import com.citrix.netscaler.nitro.service.nitro_service;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static com.edmunds.etm.nitro.Suppress.suppress;

public class NsService {
    private nitro_service nitroService;
    private final service internal;

    public NsService() {
        this.internal = new service();
    }

    NsService(nitro_service nitroService, service internal) {
        this.nitroService = nitroService;
        this.internal = internal;
    }

    service getInternal(nitro_service nitroService) {
        this.nitroService = nitroService;
        return internal;
    }

    public NsServiceBinding getBindings() throws NitroException {
        final service_binding binding = wrap(() ->
                service_binding.get(nitroService, getName()));

        return binding != null ?
                new NsServiceBinding(nitroService, this, binding) :
                null;
    }

    public void bindMonitor(NsMonitor monitor) throws NitroException {
        bindMonitor(monitor.getMonitorName());
    }

    public void bindMonitor(String monitorName) throws NitroException {

        final service_lbmonitor_binding binding = new service_lbmonitor_binding();

        suppress(() -> {
            binding.set_name(getName());
            binding.set_monitor_name(monitorName);
        });

        wrap("Add Service Monitor Binding Failed", () -> service_lbmonitor_binding.add(nitroService, binding));
    }

    public String getName() {
        return suppress(internal::get_name);
    }

    public void setName(String name) {
        suppress(() -> internal.set_name(name));
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

    public void setServer(NsServer server) {
        suppress(() -> internal.set_servername(server.getName()));
    }

    public String getServerName() {
        return suppress(internal::get_servername);
    }

    public void setServerName(String serverName) {
        suppress(() -> internal.set_servername(serverName));
    }

    public NsServiceType getServiceType() {
        return NsServiceType.valueOf(suppress(internal::get_servicetype));
    }

    public void setServiceType(NsServiceType serviceType) {
        suppress(() -> internal.set_servicetype(serviceType.name()));
    }
}
