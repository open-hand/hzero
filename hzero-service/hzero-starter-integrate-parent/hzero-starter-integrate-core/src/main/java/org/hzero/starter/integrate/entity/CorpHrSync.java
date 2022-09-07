package org.hzero.starter.integrate.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 *description
 *
 * @author zifeng.ding@hand-china.com 2020/01/13 10:39
 */
public class CorpHrSync {

    @ApiModelProperty("id")
    private Long syncId;
    @ApiModelProperty(value = "同步类型 值集HPFM.HR_SYNC_TYPE")
    private String syncTypeCode;
    @ApiModelProperty(value = "用户凭证")
    private String appId;
    @ApiModelProperty(value = "用户密钥")
    private String appSecret;
    @ApiModelProperty(value = "租户id，hpfm_tenant.tenant_id")
    private Long tenantId;
    @ApiModelProperty(value = "启用标识")
    private Integer enabledFlag;
    @ApiModelProperty(value = "授权类型，值集：HPFM.HR_AUTH_TYPE 值:SELF|THIRD")
    private String authType;
    @ApiModelProperty(value = "授权地址")
    private String  authAddress;

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

}