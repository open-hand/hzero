package org.hzero.platform.api.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.CodeRule;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * 编码规则接收参数dto
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/22 14:28
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CodeRuleDTO extends CodeRule {

    private String ruleCode;
    private String ruleName;
    private String description;
    private Long objectVersionNumber;
    private String tenantName;
    private String meaning;
    private Long tenantId;
    private List<CodeRuleDistDTO> codeRuleDistDTOList;
    private String ruleLevel;
    private String levelCode;
    private String levelValue;
    private Map<String, String> variableMap;

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    @Override
    public String getRuleCode() {
        return ruleCode;
    }

    @Override
    public CodeRuleDTO setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
        return this;
    }

    @Override
    public String getRuleName() {
        return ruleName;
    }

    @Override
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public CodeRuleDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }


    public List<CodeRuleDistDTO> getCodeRuleDistDTOList() {
        return codeRuleDistDTOList;
    }

    public void setCodeRuleDistDTOList(List<CodeRuleDistDTO> codeRuleDistDTOList) {
        this.codeRuleDistDTOList = codeRuleDistDTOList;
    }

    @Override
    public String getRuleLevel() {
        return ruleLevel;
    }

    @Override
    public void setRuleLevel(String ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue;
    }

    public Map<String, String> getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(Map<String, String> variableMap) {
        this.variableMap = variableMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeRuleDTO)) return false;
        if (!super.equals(o)) return false;
        CodeRuleDTO that = (CodeRuleDTO) o;
        return Objects.equals(ruleCode, that.ruleCode) &&
                Objects.equals(ruleName, that.ruleName) &&
                Objects.equals(description, that.description) &&
                Objects.equals(objectVersionNumber, that.objectVersionNumber) &&
                Objects.equals(tenantName, that.tenantName) &&
                Objects.equals(meaning, that.meaning) &&
                Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(codeRuleDistDTOList, that.codeRuleDistDTOList) &&
                Objects.equals(ruleLevel, that.ruleLevel) &&
                Objects.equals(levelCode, that.levelCode) &&
                Objects.equals(levelValue, that.levelValue) &&
                Objects.equals(variableMap, that.variableMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ruleCode, ruleName, description, objectVersionNumber, tenantName, meaning, tenantId, codeRuleDistDTOList, ruleLevel, levelCode, levelValue, variableMap);
    }
}
