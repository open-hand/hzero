package org.hzero.platform.domain.vo;

import org.hzero.platform.domain.entity.EventRule;
import org.springframework.beans.BeanUtils;

/**
 * 事件规则值对象，存储到缓存
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/12 14:08
 */
public class EventRuleVO {
    private Long eventRuleId;
    private Long eventId;
    private Integer syncFlag;
    private String callType;
    private String ruleCode;
    private String beanName;
    private String methodName;
    private String apiUrl;
    private String apiMethod;
    private String messageCode;
    private String serverCode;
    private Integer orderSeq;
    private String matchingRule;
    private Integer resultFlag;
    private Integer enabledFlag;

    public EventRuleVO(EventRule eventRule) {
        BeanUtils.copyProperties(eventRule, this);
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

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
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

    @Override
    public String toString() {
        return "EventRuleVO{" +
                "eventRuleId=" + eventRuleId +
                ", eventId=" + eventId +
                ", syncFlag=" + syncFlag +
                ", callType='" + callType + '\'' +
                ", ruleCode='" + ruleCode + '\'' +
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
                '}';
    }
}
