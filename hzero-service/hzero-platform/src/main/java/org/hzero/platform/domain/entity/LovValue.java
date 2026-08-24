package org.hzero.platform.domain.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Where;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 值集值实体
 *
 * @author gaokuo.dai@hand-china.com 2018年6月12日上午10:28:14
 */
@ApiModel("值集值实体")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_lov_value")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LovValue extends AuditDomain {
    
    public static final String FIELD_LOV_VALUE_ID = "lovValueId";
    public static final String FIELD_LOV_ID = "lovId";
    public static final String FIELD_LOV_CODE = "lovCode";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_MEANING = "meaning";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TAG = "tag";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_PARENT_VALUE = "parentValue";
    public static final String FIELD_START_DATE_ACTIVE = "startDateActive";
    public static final String FIELD_END_DATE_ACTIVE = "endDateActive";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";


    /**
     * 值集值校验
     *
     * @param lovValueRepository
     */
    public void validate(LovValueRepository lovValueRepository) {
        // 更新的时候校验租户ID
        if(this.lovValueId != null) {
            Assert.notNull(this.tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        }
        // 校验编码重复(value + parentValue)
        Assert.isTrue(lovValueRepository.selectRepeatCodeCount(this) <= 0, BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        // 校验生失效日期
        if(this.startDateActive != null && this.endDateActive != null && DateUtils.truncatedCompareTo(this.startDateActive, this.endDateActive, Calendar.DATE) > 0) {
            DateFormat dateFormat = new SimpleDateFormat(BaseConstants.Pattern.DATE);
            throw new CommonException(HpfmMsgCodeConstants.DATE_RANGE_ERROR, dateFormat.format(this.startDateActive), dateFormat.format(this.endDateActive));
        }
    }

    /**
     * 租户级校验值集值租户Id是否与数据库中值集Id相同
     */
    public void checkOrgLovValueTenant(LovValueRepository lovValueRepository) {
        LovValue lovValue = lovValueRepository.selectByPrimaryKey(this.lovValueId);
        Assert.notNull(lovValue, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 更新值集值时需要校验传入的值集Id是否与当前租户Id匹配，不匹配则抛出异常
        if (!Objects.equals(lovValue.getTenantId(), this.tenantId)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_TENANT_NOT_MATCH);
        }
    }
    
    @Id
    @GeneratedValue
    @ApiModelProperty("值集值ID")
    @Encrypt
    private Long lovValueId;
    @ApiModelProperty("值集ID")
    @Where
    @Encrypt
    private Long lovId;
    @NotEmpty
    @Length(max = 60)
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("值集代码")
    private String lovCode;
    @NotEmpty
    @Length(max = 150)
    @ApiModelProperty("值集值")
    private String value;
    @NotEmpty
    @Length(max = 480)
    @MultiLanguageField
    @ApiModelProperty("含义")
    private String meaning;
    @Length(max = 240)
    @MultiLanguageField
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;
    @Length(max = 240)
    @ApiModelProperty("标记")
    private String tag;
    @NotNull
    @ApiModelProperty("排序号")
    private Integer orderSeq;
    @Length(max = 30)
    @ApiModelProperty("父级值集值")
    private String parentValue;
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    @ApiModelProperty("有效期起")
    private Date startDateActive;
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    @ApiModelProperty("有效期止")
    private Date endDateActive;
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty("生效标识")
    private Integer enabledFlag;
    
    @Transient
    @ApiModelProperty("父级含义")
    private String parentMeaning;
    @Transient
    @ApiModelProperty("租户名称")
    private String tenantName;
    
    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getLovValueId() {
        return lovValueId;
    }
    public void setLovValueId(Long lovValueId) {
        this.lovValueId = lovValueId;
    }
    /**
     * @return 关联值集ID
     */
    public Long getLovId() {
        return lovId;
    }
    public void setLovId(Long lovId) {
        this.lovId = lovId;
    }
    /**
     * @return 值集代码
     */
    public String getLovCode() {
        return lovCode;
    }
    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }
    /**
     * @return 值集值
     */
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * @return 含义
     */
    public String getMeaning() {
        return meaning;
    }
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    /**
     * @return 标记
     */
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    /**
     * @return 排序号
     */
    public Integer getOrderSeq() {
        return orderSeq;
    }
    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }
    /**
     * @return 父级LOV值
     */
    public String getParentValue() {
        return parentValue;
    }
    public void setParentValue(String parentValue) {
        this.parentValue = parentValue;
    }
    /**
     * @return 有效期起
     */
    public Date getStartDateActive() {
        return startDateActive;
    }
    public void setStartDateActive(Date startDateActive) {
        this.startDateActive = startDateActive;
    }
    /**
     * @return 有效期止
     */
    public Date getEndDateActive() {
        return endDateActive;
    }
    public void setEndDateActive(Date endDateActive) {
        this.endDateActive = endDateActive;
    }
    /**
     * @return 生效标识：1:生效，0:失效
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }
    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
    /**
     * @return 查询字段：父级值集值含义
     */
    public String getParentMeaning() {
        return parentMeaning;
    }
    public void setParentMeaning(String parentMeaning) {
        this.parentMeaning = parentMeaning;
    }
    public String getTenantName() {
        return tenantName;
    }
    /**
     * @return 查询字段：租户名称
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LovValue [lovValueId=");
        builder.append(lovValueId);
        builder.append(", lovId=");
        builder.append(lovId);
        builder.append(", lovCode=");
        builder.append(lovCode);
        builder.append(", value=");
        builder.append(value);
        builder.append(", meaning=");
        builder.append(meaning);
        builder.append(", description=");
        builder.append(description);
        builder.append(", tenantId=");
        builder.append(tenantId);
        builder.append(", tag=");
        builder.append(tag);
        builder.append(", orderSeq=");
        builder.append(orderSeq);
        builder.append(", parentValue=");
        builder.append(parentValue);
        builder.append(", startDateActive=");
        builder.append(startDateActive);
        builder.append(", endDateActive=");
        builder.append(endDateActive);
        builder.append(", enabledFlag=");
        builder.append(enabledFlag);
        builder.append(", parentMeaning=");
        builder.append(parentMeaning);
        builder.append(", tenantName=");
        builder.append(tenantName);
        builder.append("]");
        return builder.toString();
    }
    
}
