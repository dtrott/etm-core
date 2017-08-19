package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.basic.service;
import com.citrix.netscaler.nitro.resource.config.lb.lbvserver_service_binding;
import com.citrix.netscaler.nitro.service.nitro_service;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static com.edmunds.etm.nitro.Suppress.suppress;

public class NsVirtualServerBinding {

    private nitro_service nitroService;
    private NsVirtualServer virtualServer;
    private lbvserver_service_binding internal;

    public NsVirtualServerBinding(nitro_service nitroService, NsVirtualServer virtualServer, lbvserver_service_binding binding) {
        this.nitroService = nitroService;
        this.virtualServer = virtualServer;
        this.internal = binding;
    }

    public NsVirtualServer getVirtualServer() throws NitroException {
        return virtualServer;
    }

    public NsService getService() throws NitroException {
        return new NsService(nitroService, wrap(() ->
                service.get(nitroService, getServiceName())));
    }

    public String getVirtualServerName() {
        return suppress(internal::get_name);
    }

    public void setVirtualServerName(String name) {
        suppress(() -> internal.set_name(name));
    }

    public String getServiceName() {
        return suppress(internal::get_servicename);
    }

    public void setServiceName(String servicename) {
        suppress(() -> internal.set_servicename(servicename));
    }

    public String getServiceGroupName() {
        return suppress(internal::get_servicegroupname);
    }

    public void setServiceGroupName(String servicegroupname) {
        suppress(() -> internal.set_servicegroupname(servicegroupname));
    }

    public NsServiceType getServiceType() {
        return NsServiceType.valueOf(suppress(internal::get_servicetype));
    }

    public String getIpV46() {
        return suppress(internal::get_ipv46);
    }

    public Integer getPort() {
        return suppress(internal::get_port);
    }

    public String getCurrentState() {
        return suppress(internal::get_curstate);
    }

    public String getBindServiceIp() {
        return suppress(internal::get_vsvrbindsvcip);
    }

    public Integer getBindServicePort() {
        return suppress(internal::get_vsvrbindsvcport);
    }

    /////////

    public String getVirtualServerId() {
        return suppress(internal::get_vserverid);
    }

    public Long getWeight() {
        return suppress(internal::get_weight);
    }

    public void setWeight(long weight) {
        suppress(() -> internal.set_weight(weight));
    }

    public void set_weight(Long weight) {
        suppress(() -> internal.set_weight(weight));
    }

    public Long get_dynamicweight() {
        return suppress(internal::get_dynamicweight);
    }

    public String get_cookieipport() {
        return suppress(internal::get_cookieipport);
    }

    public void delete() throws NitroException {
        wrap("Delete Binding Failed", () -> lbvserver_service_binding.delete(nitroService, internal));
    }
}
