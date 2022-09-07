package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.DomainUtils;
import org.hzero.core.util.Regexs;
import org.hzero.iam.domain.repository.DomainRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 门户分配
 *
 * @author minghui.qiu@hand-china.com 2019-06-27 20:50:16
 */
@ApiModel("门户分配")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_domain")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Domain extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_domain";

    public static final String FIELD_DOMAIN_ID = "domainId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_DOMAIN_URL = "domainUrl";
    public static final String FIELD_SSO_TYPE_CODE = "ssoTypeCode";
    public static final String FIELD_SSO_SERVER_URL = "ssoServerUrl";
    public static final String FIELD_SSO_LOGIN_URL = "ssoLoginUrl";
    public static final String FIELD_SSO_LOGOUT_URL = "ssoLogoutUrl";
    public static final String FIELD_SSO_CLIENT_ID = "ssoClientId";
    public static final String FIELD_SSO_CLIENT_PWD = "ssoClientPwd";
    public static final String FIELD_SSO_USER_INFO = "ssoUserInfo";
    public static final String FIELD_SAML_META_URL = "samlMetaUrl";
    public static final String FIELD_CLIENT_HOST_URL = "clientHostUrl";
    public static final String FIELD_LOGIN_NAME_FIELD = "loginNameField";
    public static final String FIELD_REMARK = "remark";
    

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    public void vaidateDomainUrl(DomainRepository domainRepository) {
        //校验域名是否重复
        int existFlag = domainRepository.selectCountByCondition(Condition.builder(Domain.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Domain.FIELD_DOMAIN_URL, DomainUtils.getDomain(domainUrl))
                        .andNotEqualTo(Domain.FIELD_DOMAIN_ID,domainId,true)).build());
        if (existFlag != 0) {
            throw new CommonException("domain.domain-url.exist");
        }
    }
    
    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("")
    @Id
    @GeneratedValue
    @Encrypt
    private Long domainId;
    @ApiModelProperty(value = "客户租户ID")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "客户公司ID，HPFM_COMPANY.COMPANY_ID")
    @Encrypt
    private Long companyId;
    @ApiModelProperty(value = "域名")
    @NotBlank
    private String domainUrl;
    @ApiModelProperty(value = "CAS|CAS2|SAML|IDM|OAUTH2|NULL")
    @LovValue(lovCode = "HIAM.SSO_TYPE_CODE")
    private String ssoTypeCode;
    @ApiModelProperty(value = "单点认证服务器地址")
    private String ssoServerUrl;
    @ApiModelProperty(value = "单点登录地址")
    private String ssoLoginUrl;
    @ApiModelProperty(value = "单点登出地址")
    private String ssoLogoutUrl;
    @ApiModelProperty(value = "客户端URL")
    private String clientHostUrl;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    private String ssoClientId;
    private String ssoClientPwd;
    private String ssoUserInfo;
    private String samlMetaUrl;
    @Length(max = 60)
    @Pattern(regexp = Regexs.CODE)
    private String loginNameField;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String ssoTypeMeaning;
    @Transient
    private String tenantName;
    @Transient
    private String tenantNum;
    @Transient
    private String companyName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

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
     * @return 客户租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 客户公司ID，HPFM_COMPANY.COMPANY_ID
     */
    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSsoTypeMeaning() {
        return ssoTypeMeaning;
    }

    public void setSsoTypeMeaning(String ssoTypeMeaning) {
        this.ssoTypeMeaning = ssoTypeMeaning;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getLoginNameField() {
        return loginNameField;
    }

    public void setLoginNameField(String loginNameField) {
        this.loginNameField = loginNameField;
    }
}
