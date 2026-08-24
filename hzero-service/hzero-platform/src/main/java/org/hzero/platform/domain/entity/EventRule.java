package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpMethod;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 事件规则
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/08 16:02
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_event_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventRule extends AuditDomain {

    /**
     * 消息模板编码，表hmsg_message_template.template_code
     */
    private String messageCode;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long eventRuleId;
    @Encrypt
    private Long eventId;
    @Range(max = 1)
    private Integer syncFlag;
    private String callType;
    /**
     * WebHook服务编码，表hmsg_webhook_server.server_code
     */
    private String serverCode;
    @Pattern(regexp = Regexs.CODE)
    @Length(max = 240)
    private String beanName;
    @Pattern(regexp = Regexs.CODE)
    @Length(max = 240)
    private String methodName;
    @Length(max = 480)
    private String apiUrl;
    @Length(max = 240)
    private String apiMethod;

    /**
     * 校验事件规则
     *
     * @throws CommonException 数据校验不通过
     */
    public void validate() {
        // 同步标识为 1/0 必填
        if (!BaseConstants.Flag.YES.equals(syncFlag) && !BaseConstants.Flag.NO.equals(syncFlag)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        switch (this.callType) {
            // 如果调用类型为M beanName和methodName不能为空
            case FndConstants.CallType.METHOD:
                if (StringUtils.isBlank(beanName)) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                if (StringUtils.isBlank(methodName)) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                break;
            // 如果调用类型为API，apiUrl和apiMethod不能为空 apiMethod只能为GET/POST/PUT/DELETE
            case FndConstants.CallType.API:
                if (StringUtils.isBlank(apiUrl)) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                if (StringUtils.isBlank(apiMethod)) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                apiMethod = StringUtils.upperCase(apiMethod);
                if (!StringUtils.equalsAny(apiMethod, HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name())) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                break;
            // 如果调用类型未WEB_HOOK，messageCode和serverCode不能为空
            case FndConstants.CallType.WEB_HOOK:
                if (StringUtils.isBlank(this.messageCode)) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                if (StringUtils.isBlank(this.serverCode)) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
                break;
            default:
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }

        // 规则不能为空
        if (StringUtils.isBlank(matchingRule)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    /**
     * @return 是否同步<br />
     * <ul>
     * <li>1 - 同步</li>
     * <li>0 - 异步</li>
     * </ul>
     */
    public Integer getSyncFlag() {
        return syncFlag;
    }

    private Integer orderSeq;
    @Length(max = 500)
    private String matchingRule;
    @Range(max = 1)
    private Integer resultFlag;
    @Range(max = 1)
    private Integer enabledFlag;
    @Length(max = 255)
    private String ruleDescription;
    @NotNull(groups = Insert.class)
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public EventRule setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 事件规则ID
     */
    public Long getEventRuleId() {
        return eventRuleId;
    }

    /**
     * @param eventRuleId 事件规则ID
     */
    public void setEventRuleId(Long eventRuleId) {
        this.eventRuleId = eventRuleId;
    }

    /**
     * @return 事件ID
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * @param eventId 事件ID
     */
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * @return 调用类型<br />
     * <ul>
     * <li>M - 方法调用</li>
     * <li>A - API调用</li>
     * </ul>
     */
    public String getCallType() {
        return callType;
    }

    public void setSyncFlag(Integer syncFlag) {
        this.syncFlag = syncFlag;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getMessageCode() {
        return messageCode;
    }

    /**
     * @return 事件调度类名字
     */
    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /***
     * @return 事件调度方法名称
     */
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return API地址 调用类型为A时，不为空
     */
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * @return API方法，POST,GET,PUT,DELETE....
     */
    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
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

    /**
     * @return 事件规则，OGNL表达式 not null<br/>
     * Examples:
     * <ul>
     * <li>el or e2</li>
     * <li>el and e2</li>
     * <li>el <= e2</li>
     * <li>el == e2</li>
     * <li>el != e2</li>
     * <li>e.method(args)：调用对象方法</li>
     * <li>e.property：对象属性值</li>
     * <li>@class@method(args)：调用类的静态方法</li>
     * </ul>
     */
    public String getMatchingRule() {
        return matchingRule;
    }

    /**
     * @return 调用顺序
     */
    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    /**
     * @return enableFlag 是否启用
     * <ul>
     * <li>1 - 启用</li>
     * <li>0 - 禁用</li>
     * </ul>
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setMatchingRule(String matchingRule) {
        this.matchingRule = matchingRule;
    }

    /**
     * @return 针对同步调用，调度是否需要返回结果
     */
    public Integer getResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(Integer resultFlag) {
        this.resultFlag = resultFlag;
    }

    public interface Insert {
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 规则说明
     */
    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }
}
