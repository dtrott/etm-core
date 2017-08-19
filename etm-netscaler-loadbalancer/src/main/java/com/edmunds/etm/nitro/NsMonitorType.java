package com.edmunds.etm.nitro;

public enum NsMonitorType {
    PING,

    TCP,
    HTTP,
    TCP_ECV,
    HTTP_ECV,
    UDP_ECV,
    DNS,
    FTP,
    LDNS_PING,
    LDNS_TCP,
    LDNS_DNS,
    RADIUS,
    USER,
    HTTP_INLINE,
    SIP_UDP,
    LOAD,
    FTP_EXTENDED,
    SMTP,
    SNMP,
    NNTP,
    MYSQL,
    MYSQL_ECV,
    MSSQL_ECV,
    ORACLE_ECV,
    LDAP,
    POP3,
    CITRIX_XML_SERVICE,
    CITRIX_WEB_INTERFACE,
    DNS_TCP,
    RTSP,
    ARP,
    CITRIX_AG,
    CITRIX_AAC_LOGINPAGE,
    CITRIX_AAC_LAS,
    CITRIX_XD_DDC,
    ND6,
    CITRIX_WI_EXTENDED,
    DIAMETER,
    RADIUS_ACCOUNTING,
    STOREFRONT;

    public String getRemoteName() {
        if (this == RADIUS_ACCOUNTING) {
            return name();
        }

        return name().replace('_', '-');
    }

    public static NsMonitorType remoteValueOf(String name) {
        return valueOf(name.replace('-', '_'));
    }
}
