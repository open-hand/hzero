package org.hzero.starter.sms.entity;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:08
 */
public class SmsConfig {

    private String serverCode;
    private String serverName;
    private String serverTypeCode;
    private String endPoint;
    private String accessKey;
    private String accessKeySecret;
    private String signName;
    private Integer enabledFlag;
    private Long tenantId;

    public String getServerCode() {
        return serverCode;
    }

    public SmsConfig setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public SmsConfig setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getServerTypeCode() {
        return serverTypeCode;
    }

    public SmsConfig setServerTypeCode(String serverTypeCode) {
        this.serverTypeCode = serverTypeCode;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public SmsConfig setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public SmsConfig setAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public SmsConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getSignName() {
        return signName;
    }

    public SmsConfig setSignName(String signName) {
        this.signName = signName;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public SmsConfig setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public SmsConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
