package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.basic.service_lbmonitor_binding;
import com.citrix.netscaler.nitro.resource.config.lb.lbmonitor;
import com.citrix.netscaler.nitro.service.nitro_service;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static com.edmunds.etm.nitro.Suppress.suppress;

public class NsServiceMonitorBinding {

    private final nitro_service nitroService;
    private final NsService service;
    private final service_lbmonitor_binding internal;



    NsServiceMonitorBinding(nitro_service nitroService, NsService service, service_lbmonitor_binding internal) {
        this.nitroService = nitroService;
        this.service = service;
        this.internal = internal;
    }

    public NsService getService() {
        return service;
    }

    public NsMonitor getMonitor() throws NitroException {
        return new NsMonitor(nitroService, wrap(() ->
                lbmonitor.get(nitroService, getMonitorName())));
    }

    public String getServiceName() {
        return suppress(internal::get_name);
    }

    public void setServiceName(String name) {
        suppress(() -> internal.set_name(name));
    }

    public String getMonitorName() {
        return suppress(internal::get_monitor_name);
    }

    public void setMonitorName(String monitorName) {
        suppress(() -> internal.set_monitor_name(monitorName));
    }

    public String getMonitorState() {
        return suppress(internal::get_monstate);
    }

    public void setMonitorState(String state) {
        suppress(() -> internal.set_monstate(state));
    }

    ////

    public Long getWeight() {
        return suppress(internal::get_weight);
    }

    public void setWeight(long weight) {
        suppress(() -> internal.set_weight(weight));
    }

    public void setWeight(Long weight) {
        suppress(() -> internal.set_weight(weight));
    }

    public Boolean getPassive() {
        return suppress(internal::get_passive);
    }

    public void setPassive(boolean passive) {
        suppress(() -> internal.set_passive(passive));
    }

    public void setPassive(Boolean passive) {
        suppress(() -> internal.set_passive(passive));
    }

    public Integer getMonstatparam2() {
        return suppress(internal::get_monstatparam2);
    }

    public Integer getMonstatcode() {
        return suppress(internal::get_monstatcode);
    }

    public String getLastresponse() {
        return suppress(internal::get_lastresponse);
    }

    public Long getFailedprobes() {
        return suppress(internal::get_failedprobes);
    }

    public Integer getMonstatparam3() {
        return suppress(internal::get_monstatparam3);
    }

    public Long getTotalprobes() {
        return suppress(internal::get_totalprobes);
    }

    public String getMonitor_state() {
        return suppress(internal::get_monitor_state);
    }

    public String getDup_state() {
        return suppress(internal::get_dup_state);
    }

    public Long getMonitortotalprobes() {
        return suppress(internal::get_monitortotalprobes);
    }

    public Long getDup_weight() {
        return suppress(internal::get_dup_weight);
    }

    public Integer getMonstatparam1() {
        return suppress(internal::get_monstatparam1);
    }

    public Long getResponsetime() {
        return suppress(internal::get_responsetime);
    }

    public Long getMonitortotalfailedprobes() {
        return suppress(internal::get_monitortotalfailedprobes);
    }

    public Long getMonitorcurrentfailedprobes() {
        return suppress(internal::get_monitorcurrentfailedprobes);
    }

    public Long getTotalfailedprobes() {
        return suppress(internal::get_totalfailedprobes);
    }
}
