package org.hzero.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.EventRule;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 事件规则DTO
 *
 * @author jiagnzhou.bo@hand-china.com 2018年7月20日下午4:41:52
 */
@ApiModel("事件规则")
@ExcelSheet(title = "事件规则")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventRuleDTO extends AuditDomain {

    @ApiModelProperty("事件规则ID")
    @Encrypt
    private Long eventRuleId;
    @ApiModelProperty("事件ID")
    @Encrypt
    private Long eventId;
    @ApiModelProperty("是否同步")
    private Integer syncFlag;
    @ApiModelProperty("调用类型")
    private String callType;
    @ExcelColumn(order = 80)
    @ApiModelProperty("BeanName")
    private String beanName;
    @ExcelColumn(order = 90)
    @ApiModelProperty("MethodName")
    private String methodName;
    @ExcelColumn(order = 100)
    @ApiModelProperty("Api地址")
    private String apiUrl;
    @ExcelColumn(order = 110)
    @ApiModelProperty("Api方法")
    private String apiMethod;
    @ExcelColumn(order = 120)
    @ApiModelProperty("消息模板编码")
    private String messageCode;
    @ExcelColumn(order = 130)
    @ApiModelProperty("WebHook服务编码")
    private String serverCode;
    @ExcelColumn(title = "调度顺序", order = 50)
    @ApiModelProperty("调度顺序")
    private Integer orderSeq;
    @ExcelColumn(title = "匹配规则", order = 20)
    @ApiModelProperty("匹配规则")
    private String matchingRule;
    @ApiModelProperty("是否返回结果")
    private Integer resultFlag;
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;
    @ApiModelProperty("规则描述")
    private String ruleDescription;
    private Long objectVersionNumber;

    @ExcelColumn(title = "是否同步", order = 40)
    private String syncFlagMeaning;
    @ExcelColumn(title = "调用类型", order = 30)
    private String callTypeMeaning;
    @ExcelColumn(title = "是否返回结果", order = 60)
    private String resultMeaning;
    @ExcelColumn(title = "是否启用", order = 70)
    private String enabledMeaning;

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return EventRule.class;
    }

    public Long getEventRuleId() {
        return eventRuleId;
    }

    public void setEventRuleId(Long eventRuleId) {
        this.eventRuleId = eventRuleId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getSyncFlag() {
        return syncFlag;
    }

    public void setSyncFlag(Integer syncFlag) {
        this.syncFlag = syncFlag;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getMatchingRule() {
        return matchingRule;
    }

    public void setMatchingRule(String matchingRule) {
        this.matchingRule = matchingRule;
    }

    public Integer getResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(Integer resultFlag) {
        this.resultFlag = resultFlag;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getSyncFlagMeaning() {
        return syncFlagMeaning;
    }

    public void setSyncFlagMeaning(String syncFlagMeaning) {
        this.syncFlagMeaning = syncFlagMeaning;
    }

    public String getCallTypeMeaning() {
        return callTypeMeaning;
    }

    public void setCallTypeMeaning(String callTypeMeaning) {
        this.callTypeMeaning = callTypeMeaning;
    }

    public String getResultMeaning() {
        return resultMeaning;
    }

    public void setResultMeaning(String resultMeaning) {
        this.resultMeaning = resultMeaning;
    }

    public String getEnabledMeaning() {
        return enabledMeaning;
    }

    public void setEnabledMeaning(String enabledMeaning) {
        this.enabledMeaning = enabledMeaning;
    }

    @Override
    public String toString() {
        return "EventRuleDTO{" +
                "eventRuleId=" + eventRuleId +
                ", eventId=" + eventId +
                ", syncFlag=" + syncFlag +
                ", callType='" + callType + '\'' +
                ", beanName='" + beanName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", apiMethod='" + apiMethod + '\'' +
                ", messageCode='" + messageCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", orderSeq=" + orderSeq +
                ", matchingRule='" + matchingRule + '\'' +
                ", resultFlag=" + resultFlag +
                ", enabledFlag=" + enabledFlag +
                ", ruleDescription='" + ruleDescription + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                ", syncFlagMeaning='" + syncFlagMeaning + '\'' +
                ", callTypeMeaning='" + callTypeMeaning + '\'' +
                ", resultMeaning='" + resultMeaning + '\'' +
                ", enabledMeaning='" + enabledMeaning + '\'' +
                "} " + super.toString();
    }
}
