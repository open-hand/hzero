package org.hzero.iam.api.dto;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

import org.hzero.iam.domain.entity.Domain;
import org.hzero.iam.domain.vo.DomainAssignCacheVO;
import org.springframework.beans.BeanUtils;

public class DomainDTO implements Serializable{
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
    private String loginNameField;
    private List<DomainAssignCacheVO> tenants;

    public static DomainDTO from(Domain domain) {
        DomainDTO dto = new DomainDTO();
        BeanUtils.copyProperties(domain, dto);
        return dto;
    }

    public List<DomainAssignCacheVO> getTenants() {
        return tenants;
    }

    public void setTenants(List<DomainAssignCacheVO> tenants) {
        this.tenants = tenants;
    }

    /**
     * @return
     */
    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }


    /**
     * @return 域名
     */
    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    /**
     * @return CAS|CAS2|SAML|IDM|NULL
     */
    public String getSsoTypeCode() {
        return ssoTypeCode;
    }

    public void setSsoTypeCode(String ssoTypeCode) {
        this.ssoTypeCode = ssoTypeCode;
    }

    /**
     * @return 单点认证服务器地址
     */
    public String getSsoServerUrl() {
        return ssoServerUrl;
    }

    public void setSsoServerUrl(String ssoServerUrl) {
        this.ssoServerUrl = ssoServerUrl;
    }

    /**
     * @return 单点登录地址
     */
    public String getSsoLoginUrl() {
        return ssoLoginUrl;
    }

    public void setSsoLoginUrl(String ssoLoginUrl) {
        this.ssoLoginUrl = ssoLoginUrl;
    }

    /**
     * @return 客户端URL
     */
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

    public String getLoginNameField() {
        return loginNameField;
    }

    public void setLoginNameField(String loginNameField) {
        this.loginNameField = loginNameField;
    }

    public String getSsoLogoutUrl() {
        return ssoLogoutUrl;
    }

    public void setSsoLogoutUrl(String ssoLogoutUrl) {
        this.ssoLogoutUrl = ssoLogoutUrl;
    }

    public String getSamlMetaUrl() {
        return samlMetaUrl;
    }

    public void setSamlMetaUrl(String samlMetaUrl) {
        this.samlMetaUrl = samlMetaUrl;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DomainDTO.class.getSimpleName() + "[", "]")
                .add("domainId=" + domainId)
                .add("domainUrl='" + domainUrl + "'")
                .add("ssoTypeCode='" + ssoTypeCode + "'")
                .add("ssoServerUrl='" + ssoServerUrl + "'")
                .add("ssoLoginUrl='" + ssoLoginUrl + "'")
                .add("clientHostUrl='" + clientHostUrl + "'")
                .add("ssoClientId='" + ssoClientId + "'")
                .add("ssoClientPwd='" + ssoClientPwd + "'")
                .add("ssoUserInfo='" + ssoUserInfo + "'")
                .add("loginNameField='" + loginNameField + "'")
                .add("tenants='" + tenants + "'")
                .toString();
    }
}
