package org.hzero.plugin.platform.hr.domain.entity;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.PinyinUtils;
import org.hzero.core.util.Regexs;
import org.hzero.plugin.platform.hr.domain.repository.UnitRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

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
 * 部门
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 17:05:02
 */
@VersionAudit
@ModifyAudit
@MultiLanguage
@ApiModel("部门")
@Table(name = "hpfm_unit")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Unit extends AuditDomain {

    public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_UNIT_CODE = "unitCode";
    public static final String FIELD_UNIT_NAME = "unitName";
    public static final String FIELD_UNIT_TYPE_CODE = "unit_type_code";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_PARENT_UNIT_ID = "parentUnitId";
    public static final String UNIT_COMPANY_ID = "unitCompanyId";
    public static final String COMPANY_ID = "companyId";
    public static final String FIELD_SUPERVISOR_FLAG = "supervisorFlag";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_LEVEL_PATH = "levelPath";
    public static final String FIELD_ENABLE_BUDGET_FLAG = "enableBudgetFlag";
    public static final String FIELD_COST_CODE = "costCode";
    public static final String FIELD_COST_NAME = "costName";
    // 主键加密Key
    public static final String ENCRYPT = "hpfm_unit";
    public static final String COMPANY_ENCRYPT = "hpfm_company";

    public static final String FLAG_Y = "Y";
    public static final String FLAG_N = "N";

    public static final Long ROOT_KEY = 0L;

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void validate(UnitRepository unitRepository) {
        List<String> topUnitCodes = unitRepository.getTopUnitCodes(this.tenantId);
        boolean isCompany = topUnitCodes.contains(this.unitTypeCode);
        // 校验顶级组织一定是公司
        if (this.parentUnitId == null && !isCompany) {
            throw new CommonException(PlatformHrConstants.ErrorCode.ERROR_UNIT_TOP_TYPE_ERROR);
        }
        // 主管岗位查重
        if (this.parentUnitId != null && BaseConstants.Flag.YES.equals(this.supervisorFlag)
                        && unitRepository.selectSupervisorCountByParentUnitId(this) > 0) {
            throw new CommonException(PlatformHrConstants.ErrorCode.ERROR_UNIT_MULTIPLE_SUPERVISOR_EXCEPTIONS);
        }
        // code查重
        Unit queryParam = new Unit();
        queryParam.tenantId = this.tenantId;
        queryParam.unitCode = this.unitCode;
        queryParam.unitId = this.unitId;
        // 部门级组织必须有关联公司级组织ID
        if (!isCompany) {
            Assert.notNull(this.unitCompanyId, BaseConstants.ErrorCode.DATA_INVALID);
            // 若为部门则在同一公司下部门编码不可重复
            queryParam.unitCompanyId = this.unitCompanyId;
        }
        if (unitRepository.selectRepeatCodeCount(queryParam) > 0) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    /**
     * 生成拼音和快速索引
     */
    public void generateQuickIndexAndPinyin() {
        if (this.quickIndex == null) {
            // 快速索引为空则生成快速索引
            this.quickIndex = PinyinUtils.getPinyinCapital(this.unitName);
            if (quickIndex.length() > PlatformHrConstants.QUICK_INDEX_LENGTH) {
                this.quickIndex =
                                StringUtils.join(StringUtils.substringBeforeLast(
                                                StringUtils.substring(quickIndex, BaseConstants.Digital.ZERO,
                                                                PlatformHrConstants.QUICK_INDEX_LENGTH),
                                BaseConstants.Symbol.VERTICAL_BAR), BaseConstants.Symbol.VERTICAL_BAR);
            }
        }
        if (this.phoneticize == null) {
            // 拼音为空则生成拼音
            this.phoneticize = PinyinUtils.getPinyin(this.unitName);
            if (phoneticize.length() > PlatformHrConstants.PINYIN_LENGTH) {
                this.phoneticize =
                        StringUtils.join(StringUtils.substringBeforeLast(
                                        StringUtils.substring(phoneticize, BaseConstants.Digital.ZERO,
                                                        PlatformHrConstants.PINYIN_LENGTH),
                        BaseConstants.Symbol.VERTICAL_BAR), BaseConstants.Symbol.VERTICAL_BAR);
            }
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("部门ID")
    @Encrypt
    private Long unitId;
    @NotEmpty
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Size(max = 30)
    @ApiModelProperty(value = "部门代码")
    private String unitCode;
    @NotEmpty
    @MultiLanguageField
    @Size(max = 240)
    @ApiModelProperty(value = "部门名称")
    private String unitName;
    @NotEmpty
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty(value = "部门类型")
    private String unitTypeCode;
    @NotNull
    @ApiModelProperty(value = "租户ID")
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty("组织架构公司ID")
    @Encrypt
    private Long unitCompanyId;
    @ApiModelProperty("关联企业ID")
    @Encrypt
    private Long companyId;
    @MultiLanguageField
    @Size(max = 240)
    @ApiModelProperty("描述")
    private String description;
    @NotNull
    @ApiModelProperty(value = "序号")
    private Long orderSeq;
    @ApiModelProperty("父级组织ID")
    @Encrypt
    private Long parentUnitId;
    @NotNull
    @Range(max = 1, min = 0)
    @ApiModelProperty(value = "是否启用")
    private Integer enabledFlag;
    @NotNull
    @Range(max = 1, min = 0)
    @ApiModelProperty(value = "是否主管部门")
    private Integer supervisorFlag;
    @Size(max = 480)
    @ApiModelProperty("层级路径")
    private String levelPath;

    @ApiModelProperty("快速索引")
    @MultiLanguageField
    @Length(max = PlatformHrConstants.QUICK_INDEX_LENGTH)
    private String quickIndex;

    @ApiModelProperty("拼音")
    @Length(max = 240)
    private String phoneticize;

    @ApiModelProperty("是否启用预算")
    @Range(max = 1L, min = 0L)
    private Integer enableBudgetFlag;
    @ApiModelProperty("所属成本中心编码")
    private String costCode;
    @ApiModelProperty("所属成本中心名称")
    private String costName;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty(hidden = true)
    private String treeFlag;
    @Transient
    @ApiModelProperty(hidden = true)
    private String parentLevelPath;
    @Transient
    private String companyName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getUnitId() {
        return unitId;
    }

    public Unit setUnitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    /**
     * @return 部门代码
     */
    public String getUnitCode() {
        return unitCode;
    }

    public Unit setUnitCode(String unitCode) {
        this.unitCode = unitCode;
        return this;
    }

    /**
     * @return 部门名称
     */
    public String getUnitName() {
        return unitName;
    }

    public Unit setUnitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    /**
     * @return 类型, 代码 HPFM.HR.UNIT_TYPE
     */
    public String getUnitTypeCode() {
        return unitTypeCode;
    }

    public Unit setUnitTypeCode(String unitTypeCode) {
        this.unitTypeCode = unitTypeCode;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public Unit setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public Unit setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * @return 排序号
     */
    public Long getOrderSeq() {
        return orderSeq;
    }

    public Unit setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    /**
     * @return 部门所属公司ID, hpfm_unit.unit_id
     */
    public Long getUnitCompanyId() {
        return unitCompanyId;
    }

    public Unit setUnitCompanyId(Long unitCompanyId) {
        this.unitCompanyId = unitCompanyId;
        return this;
    }

    /**
     * @return 关联企业ID
     */
    public Long getCompanyId() {
        return companyId;
    }

    public Unit setCompanyId(Long companyId) {
        this.companyId = companyId;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Unit setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    /**
     * @return 上级部门ID, hpfm_unit.unit_id
     */
    public Long getParentUnitId() {
        return parentUnitId;
    }

    public Unit setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
        return this;
    }

    /**
     * @return 是否主管部门
     */
    public Integer getSupervisorFlag() {
        return supervisorFlag;
    }

    public Unit setSupervisorFlag(Integer supervisorFlag) {
        this.supervisorFlag = supervisorFlag;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Unit setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 层级路径
     */
    public String getLevelPath() {
        return levelPath;
    }

    public Unit setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    /**
     * @return 查询字段: 是否树状查询
     */
    public String getTreeFlag() {
        return treeFlag;
    }

    public Unit setTreeFlag(String treeFlag) {
        this.treeFlag = treeFlag;
        return this;
    }

    /**
     * @return 查询字段, 父级层级路径
     */
    public String getParentLevelPath() {
        return parentLevelPath;
    }

    public Unit setParentLevelPath(String parentLevelPath) {
        this.parentLevelPath = parentLevelPath;
        return this;
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public void setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
    }

    public String getPhoneticize() {
        return phoneticize;
    }

    public void setPhoneticize(String phoneticize) {
        this.phoneticize = phoneticize;
    }

    /**
     * @return 是否启用预算 1:启用 0:不启用
     */
    public Integer getEnableBudgetFlag() {
        return enableBudgetFlag;
    }

    public void setEnableBudgetFlag(Integer enableBudgetFlag) {
        this.enableBudgetFlag = enableBudgetFlag;
    }

    /**
     * @return 所属成本中心编码
     */
    public String getCostCode() {
        return costCode;
    }

    public void setCostCode(String costCode) {
        this.costCode = costCode;
    }

    /**
     * @return 所属成本中心名称
     */
    public String getCostName() {
        return costName;
    }

    public void setCostName(String costName) {
        this.costName = costName;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "unitId=" + unitId +
                ", unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
                ", unitTypeCode='" + unitTypeCode + '\'' +
                ", tenantId=" + tenantId +
                ", unitCompanyId=" + unitCompanyId +
                ", companyId=" + companyId +
                ", description='" + description + '\'' +
                ", orderSeq=" + orderSeq +
                ", parentUnitId=" + parentUnitId +
                ", enabledFlag=" + enabledFlag +
                ", supervisorFlag=" + supervisorFlag +
                ", levelPath='" + levelPath + '\'' +
                ", quickIndex='" + quickIndex + '\'' +
                ", phoneticize='" + phoneticize + '\'' +
                ", enableBudgetFlag=" + enableBudgetFlag +
                ", costCode='" + costCode + '\'' +
                ", costName='" + costName + '\'' +
                ", treeFlag='" + treeFlag + '\'' +
                ", parentLevelPath='" + parentLevelPath + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
