package org.hzero.starter.call.entity;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:08
 */
public class CallConfig {

    private Long serverId;
    private String serverCode;
    private String serverName;
    private String serverTypeCode;
    private String accessKey;
    private String accessSecret;
    private String extParam;
    private Long tenantId;
    private Integer enabledFlag;

    public Long getServerId() {
        return serverId;
    }

    public CallConfig setServerId(Long serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public CallConfig setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public CallConfig setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getServerTypeCode() {
        return serverTypeCode;
    }

    public CallConfig setServerTypeCode(String serverTypeCode) {
        this.serverTypeCode = serverTypeCode;
        return this;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public CallConfig setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public CallConfig setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
        return this;
    }

    public String getExtParam() {
        return extParam;
    }

    public CallConfig setExtParam(String extParam) {
        this.extParam = extParam;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public CallConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public CallConfig setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }
}
