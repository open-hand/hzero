package org.hzero.plugin.platform.hr.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 岗位表实体
 * </p>
 *
 * @author qingsheng.chen 2018/6/20 星期三 14:00
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_position")
@MultiLanguage
@ApiModel("部门实体")
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("all")
public class Position extends AuditDomain {
    public static final String FIELD_POSITION_ID = "positionId";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_POSITION_CODE = "positionCode";
    public static final String FIELD_POSITION_NAME = "positionName";

    public static final String ENCRYPT = "hpfm_position";


    @Id
    @GeneratedValue
    @ApiModelProperty("岗位ID")
    @Encrypt
    private Long positionId;
    @JsonIgnore
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty("组织架构公司ID")
    @Encrypt
    private Long unitCompanyId;
    @ApiModelProperty("组织架构部门ID")
    @Encrypt
    private Long unitId;
    @ApiModelProperty("父岗位ID")
    @Encrypt
    private Long parentPositionId;
    @NotNull
    @Size(max = 20)
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("岗位编码")
    private String positionCode;
    @NotNull
    @Size(max = 100)
    @MultiLanguageField
    @ApiModelProperty("岗位名称")
    private String positionName;
    @Size(max = 255)
    @MultiLanguageField
    @ApiModelProperty("岗位描述")
    private String description;
    @NotNull
    @Min(0)
    @ApiModelProperty("排序")
    private Integer orderSeq;
    @NotNull
    @ApiModelProperty("主管岗位标记")
    private Integer supervisorFlag;
    @NotNull
    @Range(min = 0, max = 1)
    @ApiModelProperty("启用标记")
    private Integer enabledFlag;
    @ApiModelProperty("等级路径")
    private String levelPath;

    @Transient
    private String unitName;
    @Transient
    private String companyName;

    /**
     * @return 返回岗位ID
     */
    public Long getPositionId() {
        return positionId;
    }

    public Position setPositionId(Long positionId) {
        this.positionId = positionId;
        return this;
    }

    /**
     * @return 返回租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public Position setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 公司ID
     */
    public Long getUnitCompanyId() {
        return unitCompanyId;
    }

    public Position setUnitCompanyId(Long unitCompanyId) {
        this.unitCompanyId = unitCompanyId;
        return this;
    }

    /**
     * @return 返回部门ID
     */
    public Long getUnitId() {
        return unitId;
    }

    public Position setUnitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    /**
     * @return 返回父岗位ID，允许为空
     */
    public Long getParentPositionId() {
        return parentPositionId;
    }

    public Position setParentPositionId(Long parentPositionId) {
        this.parentPositionId = parentPositionId;
        return this;
    }

    /**
     * @return 返回岗位编码
     */
    public String getPositionCode() {
        return positionCode;
    }

    public Position setPositionCode(String positionCode) {
        this.positionCode = positionCode;
        return this;
    }

    /**
     * @return 返回岗位名称
     */
    public String getPositionName() {
        return positionName;
    }

    public Position setPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    /**
     * @return 返回岗位描述，允许为空
     */
    public String getDescription() {
        return description;
    }

    public Position setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * @return 返回岗位排序
     */
    public Integer getOrderSeq() {
        return orderSeq;
    }

    public Position setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    /**
     * @return 返回岗位是否为主管岗位
     */
    public Integer getSupervisorFlag() {
        return supervisorFlag;
    }

    public Position setSupervisorFlag(Integer supervisorFlag) {
        this.supervisorFlag = supervisorFlag;
        return this;
    }

    /**
     * @return 返回岗位是否启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Position setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 返回等级路径
     */
    @JsonIgnore
    public String getLevelPath() {
        return levelPath;
    }

    public Position setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    public String getName() {
        return positionName;
    }

    public String getUnitName() {
        return unitName;
    }

    public Position setUnitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }
}
