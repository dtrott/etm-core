package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.resource.config.lb.lbmonitor;
import com.citrix.netscaler.nitro.service.nitro_service;

import static com.edmunds.etm.nitro.NitroException.wrap;
import static com.edmunds.etm.nitro.Suppress.suppress;

public class NsMonitor {
    private nitro_service nitroService;
    private final lbmonitor internal;

    public NsMonitor() {
        this.internal = new lbmonitor();
    }

    public NsMonitor(nitro_service nitroService, lbmonitor internal) {
        this.nitroService = nitroService;
        this.internal = internal;
    }

    public lbmonitor getInternal(nitro_service nitroService) {
        this.nitroService = nitroService;
        return internal;
    }

    public void delete() throws NitroException {
        wrap("Delete Monitor Failed", () ->
                lbmonitor.delete(nitroService, internal));
    }

    // Primary Parameters

    public String getMonitorName() {
        return suppress(internal::get_monitorname);
    }

    public void setMonitorName(String monitorname) {
        suppress(() -> internal.set_monitorname(monitorname));
    }

    public NsMonitorType getType() {
        return NsMonitorType.remoteValueOf(suppress(internal::get_type));
    }

    public void setType(NsMonitorType type) {
        suppress(() -> internal.set_type(type.getRemoteName()));
    }

    public Integer getInterval() {
        return suppress(internal::get_interval);
    }

    public void setInterval(Integer interval) {
        suppress(() -> internal.set_interval(interval));
    }

    public void setInterval(int interval) {
        suppress(() -> internal.set_interval(interval));
    }

    public String getIntervalUnits() {
        return suppress(internal::get_units3);
    }

    public void setIntervalUnits(String intervalUnits) {
        suppress(() -> internal.set_units3(intervalUnits));
    }

    public String getDestinationIp() {
        return suppress(internal::get_destip);
    }

    public void setDestinationIp(String destip) {
        suppress(() -> internal.set_destip(destip));
    }

    public Integer getResponseTimeout() {
        return suppress(internal::get_resptimeout);
    }

    public void setResponseTimeout(Integer responseTimeout) {
        suppress(() -> internal.set_resptimeout(responseTimeout));
    }

    public void setResponseTimeout(int responseTimeout) {
        suppress(() -> internal.set_resptimeout(responseTimeout));
    }

    public String getResponseTimeoutUnits() {
        return suppress(internal::get_units4);
    }

    public void setResponseTimeoutUnits(String responseTimeoutUnits) {
        suppress(() -> internal.set_units4(responseTimeoutUnits));
    }

    public Integer getDestinationPort() {
        return suppress(internal::get_destport);
    }

    public void setDestinationPort(Integer destport) {
        suppress(() -> internal.set_destport(destport));
    }

    public void setDestinationPort(int destport) {
        suppress(() -> internal.set_destport(destport));
    }

    public String getRtsPrequest() {
        return suppress(internal::get_rtsprequest);
    }

    public void setRtsPrequest(String rtsprequest) {
        suppress(() -> internal.set_rtsprequest(rtsprequest));
    }

    public Integer getDowntime() {
        return suppress(internal::get_downtime);
    }

    public void setDowntime(Integer downtime) {
        suppress(() -> internal.set_downtime(downtime));
    }

    public void setDowntime(int downtime) {
        suppress(() -> internal.set_downtime(downtime));
    }

    public String getDowntimeUnits() {
        return suppress(internal::get_units2);
    }

    public void setDowntimeUnits(String downtimeUnits) {
        suppress(() -> internal.set_units2(downtimeUnits));
    }

    public Long getDeviation() {
        return suppress(internal::get_deviation);
    }

    public void setDeviation(Long deviation) {
        suppress(() -> internal.set_deviation(deviation));
    }

    public void setDeviation(long deviation) {
        suppress(() -> internal.set_deviation(deviation));
    }

    public String getDeviationUnits() {
        return suppress(internal::get_units1);
    }

    public void setDeviationUnits(String deviationUnits) {
        suppress(() -> internal.set_units1(deviationUnits));
    }

    public Integer getRetries() {
        return suppress(internal::get_retries);
    }

    public void setRetries(Integer retries) {
        suppress(() -> internal.set_retries(retries));
    }

    public void setRetries(int retries) {
        suppress(() -> internal.set_retries(retries));
    }

    public Long getResponseTimeoutThreshold() {
        return suppress(internal::get_resptimeoutthresh);
    }

    public void setResponseTimeoutThreshold(Long responseTimeoutThreshold) {
        suppress(() -> internal.set_resptimeoutthresh(responseTimeoutThreshold));
    }

    public void setResponseTimeoutThreshold(long responseTimeoutThreshold) {
        suppress(() -> internal.set_resptimeoutthresh(responseTimeoutThreshold));
    }

    public Integer getSnmpAlertRetries() {
        return suppress(internal::get_alertretries);
    }

    public void setSnmpAlertRetries(Integer snmpAlertRetries) {
        suppress(() -> internal.set_alertretries(snmpAlertRetries));
    }

    public void setSnmpAlertRetries(int snmpAlertRetries) {
        suppress(() -> internal.set_alertretries(snmpAlertRetries));
    }

    public Integer getSuccessRetries() {
        return suppress(internal::get_successretries);
    }

    public void setSuccessRetries(Integer successRetries) {
        suppress(() -> internal.set_successretries(successRetries));
    }

    public void setSuccessRetries(int successRetries) {
        suppress(() -> internal.set_successretries(successRetries));
    }

    public Integer getFailureRetries() {
        return suppress(internal::get_failureretries);
    }

    public void setFailureRetries(Integer failureRetries) {
        suppress(() -> internal.set_failureretries(failureRetries));
    }

    public void setFailureRetries(int failureRetries) {
        suppress(() -> internal.set_failureretries(failureRetries));
    }

    public String getNetProfile() {
        return suppress(internal::get_netprofile);
    }

    public void setNetProfile(String netprofile) {
        suppress(() -> internal.set_netprofile(netprofile));
    }

    public String getTos() {
        return suppress(internal::get_tos);
    }

    public void setTos(String tos) {
        suppress(() -> internal.set_tos(tos));
    }

    public Long getTosId() {
        return suppress(internal::get_tosid);
    }

    public void setTosId(Long tosId) {
        suppress(() -> internal.set_tosid(tosId));
    }

    public void setTosId(long tosId) {
        suppress(() -> internal.set_tosid(tosId));
    }

    public String getReverse() {
        return suppress(internal::get_reverse);
    }

    public void setReverse(String reverse) {
        suppress(() -> internal.set_reverse(reverse));
    }

    public String getTransparent() {
        return suppress(internal::get_transparent);
    }

    public void setTransparent(String transparent) {
        suppress(() -> internal.set_transparent(transparent));
    }

    public String getLrtm() {
        return suppress(internal::get_lrtm);
    }

    public void setLrtm(String lrtm) {
        suppress(() -> internal.set_lrtm(lrtm));
    }

    public String getSecure() {
        return suppress(internal::get_secure);
    }

    public void setSecure(String secure) {
        suppress(() -> internal.set_secure(secure));
    }

    public String getIpTunnel() {
        return suppress(internal::get_iptunnel);
    }

    public void setIpTunnel(String ipTunnel) {
        suppress(() -> internal.set_iptunnel(ipTunnel));
    }

    //// Page 2

    public String getHttpRequest() {
        return suppress(internal::get_httprequest);
    }

    public void setHttpRequest(String httprequest) {
        suppress(() -> internal.set_httprequest(httprequest));
    }

    public String[] getResponseCodes() {
        return suppress(internal::get_respcode);
    }

    public void setResponseCodes(String[] respcode) {
        suppress(() -> internal.set_respcode(respcode));
    }

    public String getSendString() {
        return suppress(internal::get_send);
    }

    public void setSendString(String sendString) {
        suppress(() -> internal.set_send(sendString));
    }

    public String getReceiveString() {
        return suppress(internal::get_recv);
    }

    public void setReceiveString(String receiveString) {
        suppress(() -> internal.set_recv(receiveString));
    }

    public String getCustomHeaders() {
        return suppress(internal::get_customheaders);
    }

    public void setCustomHeaders(String customheaders) {
        suppress(() -> internal.set_customheaders(customheaders));
    }

    // Other Parameters

    public String getSnmpoid() {
        return suppress(internal::get_Snmpoid);
    }

    public void setSnmpoid(String Snmpoid) {
        suppress(() -> internal.set_Snmpoid(Snmpoid));
    }

    public Long[] getAcctapplicationid() {
        return suppress(internal::get_acctapplicationid);
    }

    public void setAcctapplicationid(Long[] acctapplicationid) {
        suppress(() -> internal.set_acctapplicationid(acctapplicationid));
    }

    public String getAction() {
        return suppress(internal::get_action);
    }

    public void setAction(String action) {
        suppress(() -> internal.set_action(action));
    }

    public String getApplication() {
        return suppress(internal::get_application);
    }

    public void setApplication(String application) {
        suppress(() -> internal.set_application(application));
    }

    public String getAttribute() {
        return suppress(internal::get_attribute);
    }

    public void setAttribute(String attribute) {
        suppress(() -> internal.set_attribute(attribute));
    }

    public Long[] getAuthapplicationid() {
        return suppress(internal::get_authapplicationid);
    }

    public void setAuthapplicationid(Long[] authapplicationid) {
        suppress(() -> internal.set_authapplicationid(authapplicationid));
    }

    public String getBasedn() {
        return suppress(internal::get_basedn);
    }

    public void setBasedn(String basedn) {
        suppress(() -> internal.set_basedn(basedn));
    }

    public String getBinddn() {
        return suppress(internal::get_binddn);
    }

    public void setBinddn(String binddn) {
        suppress(() -> internal.set_binddn(binddn));
    }

    public String getDatabase() {
        return suppress(internal::get_database);
    }

    public void setDatabase(String database) {
        suppress(() -> internal.set_database(database));
    }

    public String getDispatcherip() {
        return suppress(internal::get_dispatcherip);
    }

    public void setDispatcherip(String dispatcherip) {
        suppress(() -> internal.set_dispatcherip(dispatcherip));
    }

    public Integer getDispatcherport() {
        return suppress(internal::get_dispatcherport);
    }

    public void setDispatcherport(Integer dispatcherport) {
        suppress(() -> internal.set_dispatcherport(dispatcherport));
    }

    public void setDispatcherport(int dispatcherport) {
        suppress(() -> internal.set_dispatcherport(dispatcherport));
    }

    public String getDomain() {
        return suppress(internal::get_domain);
    }

    public void setDomain(String domain) {
        suppress(() -> internal.set_domain(domain));
    }

    public String getEvalrule() {
        return suppress(internal::get_evalrule);
    }

    public void setEvalrule(String evalrule) {
        suppress(() -> internal.set_evalrule(evalrule));
    }

    public String getFilename() {
        return suppress(internal::get_filename);
    }

    public void setFilename(String filename) {
        suppress(() -> internal.set_filename(filename));
    }

    public String getFilter() {
        return suppress(internal::get_filter);
    }

    public void setFilter(String filter) {
        suppress(() -> internal.set_filter(filter));
    }

    public Long getFirmwarerevision() {
        return suppress(internal::get_firmwarerevision);
    }

    public void setFirmwarerevision(Long firmwarerevision) {
        suppress(() -> internal.set_firmwarerevision(firmwarerevision));
    }

    public void setFirmwarerevision(long firmwarerevision) {
        suppress(() -> internal.set_firmwarerevision(firmwarerevision));
    }

    public String getGroup() {
        return suppress(internal::get_group);
    }

    public void setGroup(String group) {
        suppress(() -> internal.set_group(group));
    }

    public String getHostipaddress() {
        return suppress(internal::get_hostipaddress);
    }

    public void setHostipaddress(String hostipaddress) {
        suppress(() -> internal.set_hostipaddress(hostipaddress));
    }

    public String getHostname() {
        return suppress(internal::get_hostname);
    }

    public void setHostname(String hostname) {
        suppress(() -> internal.set_hostname(hostname));
    }

    public String getInbandsecurityid() {
        return suppress(internal::get_inbandsecurityid);
    }

    public void setInbandsecurityid(String inbandsecurityid) {
        suppress(() -> internal.set_inbandsecurityid(inbandsecurityid));
    }

    public String[] getIpaddress() {
        return suppress(internal::get_ipaddress);
    }

    public void setIpaddress(String[] ipaddress) {
        suppress(() -> internal.set_ipaddress(ipaddress));
    }

    public String getKcdaccount() {
        return suppress(internal::get_kcdaccount);
    }

    public void setKcdaccount(String kcdaccount) {
        suppress(() -> internal.set_kcdaccount(kcdaccount));
    }

    public String getLasversion() {
        return suppress(internal::get_lasversion);
    }

    public void setLasversion(String lasversion) {
        suppress(() -> internal.set_lasversion(lasversion));
    }

    public String getLogonpointname() {
        return suppress(internal::get_logonpointname);
    }

    public void setLogonpointname(String logonpointname) {
        suppress(() -> internal.set_logonpointname(logonpointname));
    }

    public Long getMaxforwards() {
        return suppress(internal::get_maxforwards);
    }

    public void setMaxforwards(Long maxforwards) {
        suppress(() -> internal.set_maxforwards(maxforwards));
    }

    public void setMaxforwards(long maxforwards) {
        suppress(() -> internal.set_maxforwards(maxforwards));
    }

    public String getMetric() {
        return suppress(internal::get_metric);
    }

    public void setMetric(String metric) {
        suppress(() -> internal.set_metric(metric));
    }

    public String getMetrictable() {
        return suppress(internal::get_metrictable);
    }

    public void setMetrictable(String metrictable) {
        suppress(() -> internal.set_metrictable(metrictable));
    }

    public void setMetricthreshold(Long metricthreshold) {
        suppress(() -> internal.set_metricthreshold(metricthreshold));
    }

    public Long getMetricthreshold() {
        return suppress(internal::get_metricthreshold);
    }

    public void setMetricthreshold(long metricthreshold) {
        suppress(() -> internal.set_metricthreshold(metricthreshold));
    }

    public Long getMetricweight() {
        return suppress(internal::get_metricweight);
    }

    public void setMetricweight(Long metricweight) {
        suppress(() -> internal.set_metricweight(metricweight));
    }

    public void setMetricweight(long metricweight) {
        suppress(() -> internal.set_metricweight(metricweight));
    }

    public String getMssqlprotocolversion() {
        return suppress(internal::get_mssqlprotocolversion);
    }

    public void setMssqlprotocolversion(String mssqlprotocolversion) {
        suppress(() -> internal.set_mssqlprotocolversion(mssqlprotocolversion));
    }

    public String getOriginhost() {
        return suppress(internal::get_originhost);
    }

    public void setOriginhost(String originhost) {
        suppress(() -> internal.set_originhost(originhost));
    }

    public String getOriginrealm() {
        return suppress(internal::get_originrealm);
    }

    public void setOriginrealm(String originrealm) {
        suppress(() -> internal.set_originrealm(originrealm));
    }

    public String getPassword() {
        return suppress(internal::get_password);
    }

    public void setPassword(String password) {
        suppress(() -> internal.set_password(password));
    }

    public String getProductname() {
        return suppress(internal::get_productname);
    }

    public void setProductname(String productname) {
        suppress(() -> internal.set_productname(productname));
    }

    public String getQuery() {
        return suppress(internal::get_query);
    }

    public void setQuery(String query) {
        suppress(() -> internal.set_query(query));
    }

    public String getQuerytype() {
        return suppress(internal::get_querytype);
    }

    public void setQuerytype(String querytype) {
        suppress(() -> internal.set_querytype(querytype));
    }

    public String getRadaccountsession() {
        return suppress(internal::get_radaccountsession);
    }

    public void setRadaccountsession(String radaccountsession) {
        suppress(() -> internal.set_radaccountsession(radaccountsession));
    }

    public Long getRadaccounttype() {
        return suppress(internal::get_radaccounttype);
    }

    public void setRadaccounttype(Long radaccounttype) {
        suppress(() -> internal.set_radaccounttype(radaccounttype));
    }

    public void setRadaccounttype(long radaccounttype) {
        suppress(() -> internal.set_radaccounttype(radaccounttype));
    }

    public String getRadapn() {
        return suppress(internal::get_radapn);
    }

    public void setRadapn(String radapn) {
        suppress(() -> internal.set_radapn(radapn));
    }

    public String getRadframedip() {
        return suppress(internal::get_radframedip);
    }

    public void setRadframedip(String radframedip) {
        suppress(() -> internal.set_radframedip(radframedip));
    }

    public String getRadkey() {
        return suppress(internal::get_radkey);
    }

    public void setRadkey(String radkey) {
        suppress(() -> internal.set_radkey(radkey));
    }

    public String getRadmsisdn() {
        return suppress(internal::get_radmsisdn);
    }

    public void setRadmsisdn(String radmsisdn) {
        suppress(() -> internal.set_radmsisdn(radmsisdn));
    }

    public String getRadnasid() {
        return suppress(internal::get_radnasid);
    }

    public void setRadnasid(String radnasid) {
        suppress(() -> internal.set_radnasid(radnasid));
    }

    public String getRadnasip() {
        return suppress(internal::get_radnasip);
    }

    public void setRadnasip(String radnasip) {
        suppress(() -> internal.set_radnasip(radnasip));
    }

    public String getScriptargs() {
        return suppress(internal::get_scriptargs);
    }

    public void setScriptargs(String scriptargs) {
        suppress(() -> internal.set_scriptargs(scriptargs));
    }

    public String getScriptname() {
        return suppress(internal::get_scriptname);
    }

    public void setScriptname(String scriptname) {
        suppress(() -> internal.set_scriptname(scriptname));
    }

    public String getSecondarypassword() {
        return suppress(internal::get_secondarypassword);
    }

    public void setSecondarypassword(String secondarypassword) {
        suppress(() -> internal.set_secondarypassword(secondarypassword));
    }

    public String getServicegroupname() {
        return suppress(internal::get_servicegroupname);
    }

    public void setServicegroupname(String servicegroupname) {
        suppress(() -> internal.set_servicegroupname(servicegroupname));
    }

    public String getServicename() {
        return suppress(internal::get_servicename);
    }

    public void setServicename(String servicename) {
        suppress(() -> internal.set_servicename(servicename));
    }

    public String getSipmethod() {
        return suppress(internal::get_sipmethod);
    }

    public void setSipmethod(String sipmethod) {
        suppress(() -> internal.set_sipmethod(sipmethod));
    }

    public String getSipreguri() {
        return suppress(internal::get_sipreguri);
    }

    public void setSipreguri(String sipreguri) {
        suppress(() -> internal.set_sipreguri(sipreguri));
    }

    public String getSipuri() {
        return suppress(internal::get_sipuri);
    }

    public void setSipuri(String sipuri) {
        suppress(() -> internal.set_sipuri(sipuri));
    }

    public String getSitepath() {
        return suppress(internal::get_sitepath);
    }

    public void setSitepath(String sitepath) {
        suppress(() -> internal.set_sitepath(sitepath));
    }

    public String getSnmpcommunity() {
        return suppress(internal::get_snmpcommunity);
    }

    public void setSnmpcommunity(String snmpcommunity) {
        suppress(() -> internal.set_snmpcommunity(snmpcommunity));
    }

    public String getSnmpthreshold() {
        return suppress(internal::get_snmpthreshold);
    }

    public void setSnmpthreshold(String snmpthreshold) {
        suppress(() -> internal.set_snmpthreshold(snmpthreshold));
    }

    public String getSnmpversion() {
        return suppress(internal::get_snmpversion);
    }

    public void setSnmpversion(String snmpversion) {
        suppress(() -> internal.set_snmpversion(snmpversion));
    }

    public String getSqlquery() {
        return suppress(internal::get_sqlquery);
    }

    public void setSqlquery(String sqlquery) {
        suppress(() -> internal.set_sqlquery(sqlquery));
    }

    public String getState() {
        return suppress(internal::get_state);
    }

    public void setState(String state) {
        suppress(() -> internal.set_state(state));
    }

    public String getStoredb() {
        return suppress(internal::get_storedb);
    }

    public void setStoredb(String storedb) {
        suppress(() -> internal.set_storedb(storedb));
    }

    public String getStorefrontacctservice() {
        return suppress(internal::get_storefrontacctservice);
    }

    public void setStorefrontacctservice(String storefrontacctservice) {
        suppress(() -> internal.set_storefrontacctservice(storefrontacctservice));
    }

    public String getStorename() {
        return suppress(internal::get_storename);
    }

    public void setStorename(String storename) {
        suppress(() -> internal.set_storename(storename));
    }

    public Long[] getSupportedvendorids() {
        return suppress(internal::get_supportedvendorids);
    }

    public void setSupportedvendorids(Long[] supportedvendorids) {
        suppress(() -> internal.set_supportedvendorids(supportedvendorids));
    }

    public String getUsername() {
        return suppress(internal::get_username);
    }

    public void setUsername(String username) {
        suppress(() -> internal.set_username(username));
    }

    public String getValidatecred() {
        return suppress(internal::get_validatecred);
    }

    public void setValidatecred(String validatecred) {
        suppress(() -> internal.set_validatecred(validatecred));
    }

    public Long getVendorid() {
        return suppress(internal::get_vendorid);
    }

    public void setVendorid(Long vendorid) {
        suppress(() -> internal.set_vendorid(vendorid));
    }

    public void setVendorid(long vendorid) {
        suppress(() -> internal.set_vendorid(vendorid));
    }

    public Long[] getVendorspecificacctapplicationids() {
        return suppress(internal::get_vendorspecificacctapplicationids);
    }

    public void setVendorspecificacctapplicationids(Long[] vendorspecificacctapplicationids) {
        suppress(() -> internal.set_vendorspecificacctapplicationids(vendorspecificacctapplicationids));
    }

    public Long[] getVendorspecificauthapplicationids() {
        return suppress(internal::get_vendorspecificauthapplicationids);
    }

    public void setVendorspecificauthapplicationids(Long[] vendorspecificauthapplicationids) {
        suppress(() -> internal.set_vendorspecificauthapplicationids(vendorspecificauthapplicationids));
    }

    public Long getVendorspecificvendorid() {
        return suppress(internal::get_vendorspecificvendorid);
    }

    public void setVendorspecificvendorid(Long vendorspecificvendorid) {
        suppress(() -> internal.set_vendorspecificvendorid(vendorspecificvendorid));
    }

    public void setVendorspecificvendorid(long vendorspecificvendorid) {
        suppress(() -> internal.set_vendorspecificvendorid(vendorspecificvendorid));
    }

    // Read Only Properties.

    public String getDupState() {
        return suppress(internal::get_dup_state);
    }

    public Long getDupWeight() {
        return suppress(internal::get_dup_weight);
    }

    public Integer getDynamicInterval() {
        return suppress(internal::get_dynamicinterval);
    }

    public Integer getDynamicResponseTimeout() {
        return suppress(internal::get_dynamicresponsetimeout);
    }

    public Integer getLrtmConf() {
        return suppress(internal::get_lrtmconf);
    }

    public String[] getMultiMetricTable() {
        return suppress(internal::get_multimetrictable);
    }

    public Long getWeight() {
        return suppress(internal::get_weight);
    }
}
