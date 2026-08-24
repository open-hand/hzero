package org.hzero.platform.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * <p>
 * 编码规则调用参数DTO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/23 14:03
 */
@ApiModel("编码规则调用接收参数")
public class CodeRuleParamDTO {

    @ApiModelProperty("规则CODE")
    private String ruleCode;
    @ApiModelProperty("（租户级编码规则才需要考虑是否传）编码规则应用层级，租户下公司层级传COM，租户下全局传GLOBAL")
    private String levelCode;
    @ApiModelProperty("（租户级编码规则才需要考虑是否传）编码规则应用层级值，当层级为公司时传公司CODE,如HAND，租户传租户CODE,全局传GLOBAL")
    private String levelValue;
    @ApiModelProperty("编码规则详情中段类型有为变量的值时才考虑传值，比如设置了一个段类型为变量，值为var的规则，则传入一个 {\"var\" : \"你想传入的值\"}")
    private Map<String, String> variableMap;

    @Override
    public String toString() {
        return "CodeRuleParamDTO{" + "ruleCode='" + ruleCode + '\'' + ", levelCode='" + levelCode + '\''
                + ", levelValue='" + levelValue + '\'' + ", variableMap=" + variableMap + '}';
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
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
}
