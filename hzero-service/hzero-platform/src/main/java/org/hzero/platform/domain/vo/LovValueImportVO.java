package org.hzero.platform.domain.vo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModelProperty;

/**
 * 值集导入VO
 *
 * @author xiaoyu.zhao@hand-china.com 2020/06/29 19:12
 */
public class LovValueImportVO extends AuditDomain {

    @ApiModelProperty("值集值ID")
    private Long lovValueId;
    @ApiModelProperty("值集ID")
    private Long lovId;
    @ApiModelProperty("值集代码")
    private String lovCode;
    @ApiModelProperty("值集值")
    private String value;
    @ApiModelProperty("含义")
    private String meaning;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("标记")
    private String tag;
    @ApiModelProperty("排序号")
    private Integer orderSeq;
    @ApiModelProperty("父级值集值")
    private String parentValue;
    @ApiModelProperty("有效期起")
    private LocalDate startDateActive;
    @ApiModelProperty("有效期止")
    private LocalDate endDateActive;
    @ApiModelProperty("生效标识")
    private Integer enabledFlag;


    public Date getDateStartDateActive() {
        if (startDateActive == null) {
            return null;
        }
        return Date.from(startDateActive.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date getDateEndDateActive() {
        if (endDateActive == null) {
            return null;
        }
        return Date.from(endDateActive.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Long getLovValueId() {
        return lovValueId;
    }

    public LovValueImportVO setLovValueId(Long lovValueId) {
        this.lovValueId = lovValueId;
        return this;
    }

    public Long getLovId() {
        return lovId;
    }

    public LovValueImportVO setLovId(Long lovId) {
        this.lovId = lovId;
        return this;
    }

    public String getLovCode() {
        return lovCode;
    }

    public LovValueImportVO setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public String getValue() {
        return value;
    }

    public LovValueImportVO setValue(String value) {
        this.value = value;
        return this;
    }

    public String getMeaning() {
        return meaning;
    }

    public LovValueImportVO setMeaning(String meaning) {
        this.meaning = meaning;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public LovValueImportVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LovValueImportVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public LovValueImportVO setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public LovValueImportVO setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getParentValue() {
        return parentValue;
    }

    public LovValueImportVO setParentValue(String parentValue) {
        this.parentValue = parentValue;
        return this;
    }

    public LocalDate getStartDateActive() {
        return startDateActive;
    }

    public LovValueImportVO setStartDateActive(LocalDate startDateActive) {
        this.startDateActive = startDateActive;
        return this;
    }

    public LocalDate getEndDateActive() {
        return endDateActive;
    }

    public LovValueImportVO setEndDateActive(LocalDate endDateActive) {
        this.endDateActive = endDateActive;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public LovValueImportVO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }
}
