package org.hzero.platform.api.dto;

import java.util.List;

import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.Event;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 事件DTO
 *
 * @author jiangzhou.bo@hand-china.com 2018年7月20日下午4:41:03
 */
@ApiModel("事件")
@ExcelSheet(title = "事件")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EventDTO extends AuditDomain {

    public static final String EVENT_EVENT_RULE_LIST = "ruleList";

    @ApiModelProperty("事件ID")
    @Encrypt
    private Long eventId;
    @ApiModelProperty("事件编码")
    @Pattern(regexp = Regexs.CODE)
    @ExcelColumn(title = "事件编码", showInChildren = true)
    private String eventCode;
    @ApiModelProperty("是否启用标识")
    @LovValue(value = "HPFM.FLAG", meaningField = "enabledFlagMeaning")
    private Integer enabledFlag;
    @ApiModelProperty("是否启用含义-导出使用")
    @ExcelColumn(title = "是否启用")
    private String enabledFlagMeaning;
    @ApiModelProperty("事件描述")
    @ExcelColumn(title = "事件描述")
    private String eventDescription;
    private Long objectVersionNumber;
    @ApiModelProperty("事件规则列表")
    @ExcelColumn(title = "事件规则", child = true)
    private List<EventRuleDTO> ruleList;
    @ApiModelProperty("租户")
    @ExcelColumn(title = "租户Id")
    private Long tenantId;
    @ApiModelProperty("租户名称")
    @Transient
    @ExcelColumn(title = "租户名称")
    private String tenantName;
    @Transient
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Integer siteQueryFlag;
    @Transient
    @ApiModelProperty("事件规则Ids，用于勾选导出事件规则")
    @Encrypt
    private List<Long> eventIds;

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return Event.class;
    }

    public Integer getSiteQueryFlag() {
        return siteQueryFlag;
    }

    public void setSiteQueryFlag(Integer siteQueryFlag) {
        this.siteQueryFlag = siteQueryFlag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getEnabledFlagMeaning() {
        return enabledFlagMeaning;
    }

    public void setEnabledFlagMeaning(String enabledFlagMeaning) {
        this.enabledFlagMeaning = enabledFlagMeaning;
    }

    public List<EventRuleDTO> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<EventRuleDTO> ruleList) {
        this.ruleList = ruleList;
    }

    @Override
    public String toString() {
        return "EventDTO{" + "eventId=" + eventId + ", eventCode='" + eventCode + '\'' + ", enabledFlag=" + enabledFlag
            + ", enabledFlagMeaning='" + enabledFlagMeaning + '\'' + ", eventDescription='" + eventDescription + '\''
            + ", objectVersionNumber=" + objectVersionNumber + ", ruleList=" + ruleList + '}';
    }
}
