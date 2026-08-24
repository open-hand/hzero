package org.hzero.sso.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 单点域名
 *
 * @author bojiangzhou 2020/08/15
 */
public class Domain implements Serializable {
    private static final long serialVersionUID = -6625864894934365970L;

    private Long domainId;
    private String domainUrl;
    private String ssoTypeCode;
    private String ssoServerUrl;
    private String ssoLoginUrl;
    private String ssoLogoutUrl;
    private String clientHostUrl;
    private String ssoClientId;
    private String ssoClientPwd;
    private String ssoUserInfo;
    private String samlMetaUrl;
    private String tenantNum;
    private Long companyId;
    private String loginNameField;
    private String host;

    private List<SsoTenant> tenants;

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public String getSsoTypeCode() {
        return ssoTypeCode;
    }

    public void setSsoTypeCode(String ssoTypeCode) {
        this.ssoTypeCode = ssoTypeCode;
    }

    public String getSsoServerUrl() {
        return ssoServerUrl;
    }

    public void setSsoServerUrl(String ssoServerUrl) {
        this.ssoServerUrl = ssoServerUrl;
    }

    public String getSsoLoginUrl() {
        return ssoLoginUrl;
    }

    public void setSsoLoginUrl(String ssoLoginUrl) {
        this.ssoLoginUrl = ssoLoginUrl;
    }

    public String getSsoLogoutUrl() {
        return ssoLogoutUrl;
    }

    public void setSsoLogoutUrl(String ssoLogoutUrl) {
        this.ssoLogoutUrl = ssoLogoutUrl;
    }

    public String getClientHostUrl() {
        return clientHostUrl;
    }

    public void setClientHostUrl(String clientHostUrl) {
        this.clientHostUrl = clientHostUrl;
    }

    public String getSsoClientId() {
        return ssoClientId;
    }

    public void setSsoClientId(String ssoClientId) {
        this.ssoClientId = ssoClientId;
    }

    public String getSsoClientPwd() {
        return ssoClientPwd;
    }

    public void setSsoClientPwd(String ssoClientPwd) {
        this.ssoClientPwd = ssoClientPwd;
    }

    public String getSsoUserInfo() {
        return ssoUserInfo;
    }

    public void setSsoUserInfo(String ssoUserInfo) {
        this.ssoUserInfo = ssoUserInfo;
    }

    public String getSamlMetaUrl() {
        return samlMetaUrl;
    }

    public void setSamlMetaUrl(String samlMetaUrl) {
        this.samlMetaUrl = samlMetaUrl;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getLoginNameField() {
        return loginNameField;
    }

    public void setLoginNameField(String loginNameField) {
        this.loginNameField = loginNameField;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<SsoTenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<SsoTenant> tenants) {
        this.tenants = tenants;
    }

    @Override
    public String toString() {
        return "{" +
                "domainId=" + domainId +
                ", domainUrl='" + domainUrl + '\'' +
                ", tenants=" + tenants +
                '}';
    }
}
