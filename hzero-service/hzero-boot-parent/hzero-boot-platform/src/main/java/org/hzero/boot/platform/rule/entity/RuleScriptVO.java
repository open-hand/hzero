package org.hzero.boot.platform.rule.entity;

/**
 * 规则引擎  规则定义DTO
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/28 17:48
 */
public class RuleScriptVO {

    private Long ruleScriptId;
    private String serverName;
    private String scriptCode;
    private String scriptTypeCode;
    private String scriptContent;
    private String scriptDescription;
    private Long tenantId;
    private Integer enabledFlag;

    public Long getRuleScriptId() {
        return ruleScriptId;
    }

    public RuleScriptVO setRuleScriptId(Long ruleScriptId) {
        this.ruleScriptId = ruleScriptId;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public RuleScriptVO setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getScriptCode() {
        return scriptCode;
    }

    public RuleScriptVO setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
        return this;
    }

    public String getScriptTypeCode() {
        return scriptTypeCode;
    }

    public RuleScriptVO setScriptTypeCode(String scriptTypeCode) {
        this.scriptTypeCode = scriptTypeCode;
        return this;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public RuleScriptVO setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
        return this;
    }

    public String getScriptDescription() {
        return scriptDescription;
    }

    public RuleScriptVO setScriptDescription(String scriptDescription) {
        this.scriptDescription = scriptDescription;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public RuleScriptVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public RuleScriptVO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    @Override
    public String toString() {
        return "RuleScriptVO{" +
                "ruleScriptId=" + ruleScriptId +
                ", serverName='" + serverName + '\'' +
                ", scriptCode='" + scriptCode + '\'' +
                ", scriptTypeCode='" + scriptTypeCode + '\'' +
                ", scriptContent='" + scriptContent + '\'' +
                ", scriptDescription='" + scriptDescription + '\'' +
                ", tenantId=" + tenantId +
                ", enabledFlag=" + enabledFlag +
                '}';
    }
}
