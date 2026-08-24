package org.hzero.boot.platform.event.vo;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.event.Constants;
import org.hzero.boot.platform.event.helper.RuleMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Map;

/**
 * 事件规则值对象
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 10:16
 */
public class EventRuleVO implements Serializable {
    private static final long serialVersionUID = -7441784022238054295L;

    private Long eventRuleId;
    private Long eventId;
    private Integer syncFlag;
    private String callType;
    private String beanName;
    private String methodName;
    private String apiUrl;
    private String apiMethod;
    /**
     * 消息模板编码
     */
    private String messageCode;
    /**
     * WebHook配置编码
     */
    private String serverCode;
    private Integer orderSeq;
    private String matchingRule;
    private Integer enabledFlag;
    private Integer resultFlag;

    public EventRuleVO() {
    }

    public EventRuleVO(Long eventRuleId, Long eventId, Integer syncFlag, String callType, String beanName,
                       String methodName, String apiUrl, String apiMethod, Integer orderSeq, String matchingRule,
                       Integer enabledFlag, Integer resultFlag) {
        this.eventRuleId = eventRuleId;
        this.eventId = eventId;
        this.syncFlag = syncFlag;
        this.callType = callType;
        this.beanName = beanName;
        this.methodName = methodName;
        this.apiUrl = apiUrl;
        this.apiMethod = apiMethod;
        this.orderSeq = orderSeq;
        this.matchingRule = matchingRule;
        this.enabledFlag = enabledFlag;
        this.resultFlag = resultFlag;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

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

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Integer getResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(Integer resultFlag) {
        this.resultFlag = resultFlag;
    }

    /**
     * @return 根据 apiMethod 返回 HttpMethod
     */
    public HttpMethod httpMethod() {
        Assert.notNull(HttpMethod.resolve(StringUtils.upperCase(this.apiMethod)),
                String.format("apiMethod[%s] is not support", this.apiMethod));
        return HttpMethod.valueOf(StringUtils.upperCase(this.apiMethod));
    }

    /**
     * @return 是否返回调度结果
     */
    public boolean enableResult() {
        return Constants.Flag.YES.equals(this.resultFlag);
    }

    /**
     * @return 是否同步调用
     */
    public boolean syncCall() {
        return Constants.Flag.YES.equals(this.syncFlag);
    }

    /**
     * @return 规则是否启用
     */
    public boolean enabled() {
        return Constants.Flag.YES.equals(this.enabledFlag);
    }

    /**
     * 校验规则
     *
     * @param ruleMatcher {@link RuleMatcher} 规则匹配器
     * @param condition   校验条件
     * @return 校验是否通过
     * @throws IllegalArgumentException 解析规则失败
     */
    public boolean checkRulePass(RuleMatcher ruleMatcher, Map<String, Object> condition) {
        return ruleMatcher.matches(this.matchingRule, condition);
    }

    @Override
    public String toString() {
        return "EventRuleVO{" +
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
                ", enabledFlag=" + enabledFlag +
                ", resultFlag=" + resultFlag +
                '}';
    }
}
