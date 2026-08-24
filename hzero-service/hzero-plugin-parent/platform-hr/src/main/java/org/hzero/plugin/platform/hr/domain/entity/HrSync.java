package org.hzero.plugin.platform.hr.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * hr基础数据同步外部系统
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
@ApiModel("hr基础数据同步外部系统")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_hr_sync")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrSync extends AuditDomain {

    public static final String FIELD_SYNC_ID = "syncId";
    public static final String FIELD_SYNC_TYPE_CODE = "syncTypeCode";
    public static final String FIELD_APP_ID = "appId";
    public static final String FIELD_APP_SECRET = "appSecret";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_OBJECT_VERSION_NUMBER = "objectVersionNumber";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String FIELD_ENABLE_FLAG = "enabledFlag";
    public static final String FIELD_AUTH_TYPE = "authType";
    public static final String FIELD_AUTH_ADDRESS = "authAddress";
    public static final String ENCRYPT = "hpfm_hr_sync";
    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("")
    @Id
    @GeneratedValue
    @Encrypt
    private Long syncId;
    @ApiModelProperty(value = "同步类型 值集HPFM.HR_SYNC_TYPE")
    @LovValue(lovCode = "HPFM.HR_SYNC_TYPE")
    private String syncTypeCode;
    @ApiModelProperty(value = "用户凭证")
    @Length(max = 200)
    private String appId;
    @ApiModelProperty(value = "用户密钥")
    @DataSecurity
    @Length(max = 200)
    private String appSecret;
    @ApiModelProperty(value = "租户id，hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "启用标识")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "授权类型，值集：HPFM.HR_AUTH_TYPE 值:SELF|THIRD")
    @LovValue(lovCode = "HPFM.HR_AUTH_TYPE")
    private String authType;
    @Length(max = 240)
    @ApiModelProperty(value = "authAddress")
    private String  authAddress;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String syncTypeMeaning;
    @Transient
    private String authTypeMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 
     */
    public Long getSyncId() {
        return syncId;
    }

    public void setSyncId(Long syncId) {
        this.syncId = syncId;
    }

    /**
     * @return 同步类型 值集HPFM.HR_SYNC_TYPE
     */
    public String getSyncTypeCode() {
        return syncTypeCode;
    }

    public void setSyncTypeCode(String syncTypeCode) {
        this.syncTypeCode = syncTypeCode;
    }

    /**
     * @return appId
     */
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return appSecret
     */
    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * @return 租户id，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getSyncTypeMeaning() {
        return syncTypeMeaning;
    }

    public void setSyncTypeMeaning(String syncTypeMeaning) {
        this.syncTypeMeaning = syncTypeMeaning;
    }
    
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getAuthAddress() {
        return authAddress;
    }

    public void setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
    }
    
    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthTypeMeaning() {
        return authTypeMeaning;
    }

    public void setAuthTypeMeaning(String authTypeMeaning) {
        this.authTypeMeaning = authTypeMeaning;
    }

}
