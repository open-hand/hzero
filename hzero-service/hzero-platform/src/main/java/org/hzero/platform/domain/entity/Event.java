package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.common.query.Where;
import org.hzero.platform.domain.repository.EventRepository;
import org.hzero.platform.domain.repository.EventRuleRepository;
import org.hzero.platform.domain.vo.EventRuleVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * 事件，每个事件拥有唯一编码，通过编码触发事件。如果禁用该事件，则事件下的所有规则都默认失效.
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/08 15:53
 */
@ApiModel
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_event")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event extends AuditDomain {

    public static final String EVENT_ID = "eventId";
    public static final String EVENT_EVENT_CODE = "eventCode";
    public static final String TENANT_ID = "tenantId";


    /**
     * 缓存key为事件编码，value为事件规则列表<br/>
     * 如果为启用状态，查询出事件规则，并刷新到缓存中<br/>
     * 如果为禁用状态，清除该事件缓存
     *
     * @param eventRuleRepository EventRuleRepository
     * @param redisHelper RedisHelper
     */
    public void refreshCache(EventRuleRepository eventRuleRepository, RedisHelper redisHelper, ObjectMapper objectMapper) {
        clearCache(redisHelper);
        if (BaseConstants.Flag.YES.equals(enabledFlag)) {
            this.ruleList = eventRuleRepository.select(Event.EVENT_ID, this.eventId);
            if (CollectionUtils.isEmpty(ruleList)) {
                return;
            }
            List<EventRuleVO> voList = createEventRuleVO(this.ruleList);
            voList.forEach(vo -> {
                try {
                    redisHelper.lstRightPush(FndConstants.CacheKey.EVENT_KEY + ":" + this.eventCode, objectMapper.writeValueAsString(vo));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 清除缓存
     *
     * @param redisHelper RedisHelper
     */
    public void clearCache(RedisHelper redisHelper) {
        String key = FndConstants.CacheKey.EVENT_KEY + ":" + this.eventCode;
        redisHelper.delKey(key);
    }

    /**
     * 比较两个事件的事件编码是否一致
     *
     * @param anotherEvent 事件
     * @throws CommonException 如果两个事件的编码不一致
     */
    public void equalsEventCode(Event anotherEvent) {
        if (StringUtils.isNotBlank(anotherEvent.getEventCode())
                        && !StringUtils.equals(this.eventCode, anotherEvent.getEventCode())) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    /**
     * 校验事件
     *
     * @throws IllegalArgumentException 参数校验不通过
     */
    public void validate(EventRepository eventRepository) {
        Assert.notNull(eventCode, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(eventDescription, BaseConstants.ErrorCode.DATA_INVALID);
        List<Event> existsEvents = eventRepository.selectOptional(this, new Criteria().where(TENANT_ID, EVENT_EVENT_CODE));
        if (CollectionUtils.isNotEmpty(existsEvents)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_EVENT_CODE_EXISTS);
        }
    }

    private List<EventRuleVO> createEventRuleVO(List<EventRule> ruleList) {
        List<EventRuleVO> voList = new ArrayList<>(ruleList.size());
        ruleList.forEach(eventRule -> voList.add(new EventRuleVO(eventRule)));
        return voList;
    }

    /**
     * 校验租户合法性以及事件编码
     * @param eventRepository 事件资源库
     */
    public void validateTenant(EventRepository eventRepository) {
        Event dbEvent = eventRepository.selectByPrimaryKey(this.getEventId());
        Assert.notNull(dbEvent, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 判断租户是否合法
        if (!dbEvent.getTenantId().equals(this.tenantId)){
            throw new CommonException(HpfmMsgCodeConstants.ERROR_EVENT_TENANT_NOT_MATCH);
        }
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long eventId;
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Length(max = 30)
    @Where
    private String eventCode;
    private Integer enabledFlag;
    @Length(max = 80)
    @MultiLanguageField
    private String eventDescription;
    private Long objectVersionNumber;
    @Where
    @MultiLanguageField
    private Long tenantId;
    @Transient
    private List<EventRule> ruleList;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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
     * @return 事件编码
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * @param eventCode 事件编码
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     *
     * @return 是否启用 <br/>
     *         <ul>
     *         <li>1 - 启用</li>
     *         <li>0 - 禁用</li>
     *         </ul>
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    /**
     * @param enabledFlag 是否启用 <br/>
     *        <ul>
     *        <li>1 - 启用</li>
     *        <li>0 - 禁用</li>
     *        </ul>
     */
    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    /**
     * @return 事件说明
     */
    public String getEventDescription() {
        return eventDescription;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    /**
     * @param eventDescription 事件说明
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    @ApiIgnore
    public List<EventRule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<EventRule> ruleList) {
        this.ruleList = ruleList;
    }
}
