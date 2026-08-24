package org.hzero.iam.domain.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 屏蔽规则
 *
 * @author min.wang0101@hand-china.com 2018-08-15 11:01:44
 */
public class DataPermissionRuleVO {
    @Encrypt
    private Long ruleId;
    private String ruleCode;
    private String ruleName;
    private String description;
    private String sqlValue;
    private Long tenantId;
    private Integer enabledFlag;
    private Integer editableFlag;


    public Long getRuleId() {
        return ruleId;
    }

    public DataPermissionRuleVO setRuleId(Long ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public DataPermissionRuleVO setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
        return this;
    }

    public String getRuleName() {
        return ruleName;
    }

    public DataPermissionRuleVO setRuleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DataPermissionRuleVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public DataPermissionRuleVO setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public DataPermissionRuleVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public DataPermissionRuleVO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public DataPermissionRuleVO setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
        return this;
    }
}
