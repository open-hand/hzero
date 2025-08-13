package org.hzero.plugin.platform.hr.api.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.algorithm.tree.Child;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

/**
 * description
 *
 * @author gaokuo.dai@hand-china.com 2018年6月25日上午9:40:07
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UnitDTO extends Child<UnitDTO> implements SecurityToken{

    @ApiModelProperty("部门ID")
    @Encrypt
    private Long unitId;
    @NotEmpty
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty(value = "部门代码", required = true)
    private String unitCode;
    @NotEmpty
    @ApiModelProperty(value = "部门名称", required = true)
    private String unitName;
    @NotEmpty
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty(value = "部门类型", required = true)
    @LovValue(lovCode = "HPFM.HR.UNIT_TYPE", meaningField = "unitTypeMeaning")
    private String unitTypeCode;
    @NotNull
    @ApiModelProperty(value = "关联公司ID")
    @Encrypt
    private Long companyId;
    @NotNull
    @ApiModelProperty(value = "租户ID", required = true)
    private Long tenantId;
    @ApiModelProperty(value = "描述")
    private String description;
    @NotNull
    @ApiModelProperty(value = "序号", required = true)
    private Long orderSeq;
    @ApiModelProperty("父级组织ID")
    @Encrypt
    private Long parentUnitId;
    @ApiModelProperty(value = "是否主管部门", required = true)
    private Integer supervisorFlag;
    @ApiModelProperty(value = "是否启用", required = true)
    private Integer enabledFlag;
    @ApiModelProperty("层级路径")
    private String levelPath;
    @ApiModelProperty("组织架构公司ID")
    @Encrypt
    private Long unitCompanyId;
    @ApiModelProperty("组织架构公司名称")
    private String unitCompanyName;
    public String getUnitCompanyName() {
		return unitCompanyName;
	}

	public void setUnitCompanyName(String unitCompanyName) {
		this.unitCompanyName = unitCompanyName;
	}
	@ApiModelProperty(value = "组织类型含义", required = true)
    private String unitTypeMeaning;
    @ApiModelProperty("父级组织名称")
    private String parentUnitName;
    @ApiModelProperty("关联公司名称")
    private String companyName;
    @ApiModelProperty("快速索引")
    private String quickIndex;
    @ApiModelProperty("拼音")
    private String phoneticize;
    @ApiModelProperty("是否存在下级菜单")
    private Integer hasNextFlag;
    @ApiModelProperty("名称层级集合")
    private List<String> nameLevelPaths;
    @ApiModelProperty("是否启用预算")
    private Integer enableBudgetFlag;
    @ApiModelProperty("所属成本中心编码")
    private String costCode;
    @ApiModelProperty("所属成本中心名称")
    private String costName;

    @ApiModelProperty(hidden = true)
    private Date creationDate;
    @ApiModelProperty(hidden = true)
    private Long createdBy;
    @ApiModelProperty(hidden = true)
    private Date lastUpdateDate;
    @ApiModelProperty(hidden = true)
    private Long lastUpdatedBy;
    @ApiModelProperty(hidden = true)
    private Long objectVersionNumber;
    @ApiModelProperty(hidden = true)
    private String _token;
    /**
     * 存放弹性域数据
     */
    @Transient
    @ApiModelProperty(hidden = true)
    private Map<String, Object> flex = new HashMap<>(32);

    /**
     * 未知的反序列化字段
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(hidden = true)
    private Map<String, Object> _innerMap;

    public Map<String, Object> getFlex() {
        return flex;
    }

    public UnitDTO setFlex(Map<String, Object> flex) {
        this.flex = flex;
        return this;
    }

    public List<String> getNameLevelPaths() {
        return nameLevelPaths;
    }

    public void setNameLevelPaths(List<String> nameLevelPath) {
        this.nameLevelPaths = nameLevelPath;
    }

    public Integer getHasNextFlag() {
        return hasNextFlag;
    }

    public void setHasNextFlag(Integer hasNextFlag) {
        this.hasNextFlag = hasNextFlag;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getUnitId() {
        return unitId;
    }
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
    /**
     * @return 部门代码
     */
    public String getUnitCode() {
        return unitCode;
    }
    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    /**
     * @return 部门名称
     */
    public String getUnitName() {
        return unitName;
    }
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    /**
     * @return 类型,代码 HPFM.HR.UNIT_TYPE
     */
    public String getUnitTypeCode() {
        return unitTypeCode;
    }
    public void setUnitTypeCode(String unitTypeCode) {
        this.unitTypeCode = unitTypeCode;
    }
    /**
     * @return 公司ID,hpfm_company.company_id
     */
    public Long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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
     * @return 描述
     */
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return 排序号
     */
    public Long getOrderSeq() {
        return orderSeq;
    }
    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }
    /**
     * @return 上级部门ID,hpfm_unit.unit_id
     */
    public Long getParentUnitId() {
        return parentUnitId;
    }
    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }
    /**
     * @return 是否主管部门
     */
    public Integer getSupervisorFlag() {
        return supervisorFlag;
    }
    public void setSupervisorFlag(Integer supervisorFlag) {
        this.supervisorFlag = supervisorFlag;
    }
    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }
    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
    /**
     * @return 层级路径
     */
    public String getLevelPath() {
        return levelPath;
    }
    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }
    /**
     * @return 查询字段: 组织类型含义
     */
    public String getUnitTypeMeaning() {
        return unitTypeMeaning;
    }
    public void setUnitTypeMeaning(String unitTypeMeaning) {
        this.unitTypeMeaning = unitTypeMeaning;
    }
    /**
     * @return 查询字段: 父级组织名称
     */
    public String getParentUnitName() {
        return parentUnitName;
    }
    public void setParentUnitName(String parentUnitName) {
        this.parentUnitName = parentUnitName;
    }
    /**
     * @return 查询字段: 关联公司名称
     */
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    /**
     * @return 部门所属公司ID,hpfm_unit.unit_id
     */
    public Long getUnitCompanyId() {
        return unitCompanyId;
    }
    public void setUnitCompanyId(Long unitCompanyId) {
        this.unitCompanyId = unitCompanyId;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public Long getCreatedBy() {
        return createdBy;
    }
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public Integer getEnableBudgetFlag() {
        return enableBudgetFlag;
    }

    public void setEnableBudgetFlag(Integer enableBudgetFlag) {
        this.enableBudgetFlag = enableBudgetFlag;
    }

    public String getCostCode() {
        return costCode;
    }

    public void setCostCode(String costCode) {
        this.costCode = costCode;
    }

    public String getCostName() {
        return costName;
    }

    public void setCostName(String costName) {
        this.costName = costName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UnitDTO [unitId=");
        builder.append(unitId);
        builder.append(", unitCode=");
        builder.append(unitCode);
        builder.append(", unitName=");
        builder.append(unitName);
        builder.append(", unitTypeCode=");
        builder.append(unitTypeCode);
        builder.append(", companyId=");
        builder.append(companyId);
        builder.append(", tenantId=");
        builder.append(tenantId);
        builder.append(", description=");
        builder.append(description);
        builder.append(", orderSeq=");
        builder.append(orderSeq);
        builder.append(", parentUnitId=");
        builder.append(parentUnitId);
        builder.append(", supervisorFlag=");
        builder.append(supervisorFlag);
        builder.append(", enabledFlag=");
        builder.append(enabledFlag);
        builder.append(", levelPath=");
        builder.append(levelPath);
        builder.append(", unitCompanyId=");
        builder.append(unitCompanyId);
        builder.append(", unitTypeMeaning=");
        builder.append(unitTypeMeaning);
        builder.append(", parentUnitName=");
        builder.append(parentUnitName);
        builder.append(", companyName=");
        builder.append(companyName);
        builder.append(", creationDate=");
        builder.append(creationDate);
        builder.append(", createdBy=");
        builder.append(createdBy);
        builder.append(", lastUpdateDate=");
        builder.append(lastUpdateDate);
        builder.append(", lastUpdatedBy=");
        builder.append(lastUpdatedBy);
        builder.append(", objectVersionNumber=");
        builder.append(objectVersionNumber);
        builder.append(", quickIndex=");
        builder.append(quickIndex);
        builder.append(", phoneticize=");
        builder.append(phoneticize);
        builder.append(", enableBudgetFlag=");
        builder.append(enableBudgetFlag);
        builder.append(", costCode=");
        builder.append(costCode);
        builder.append(", costName=");
        builder.append(costName);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public String get_token() {
        return _token;
    }
    @Override
    public void set_token(String _token) {
        this._token = _token;
    }
    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return Unit.class;
    }

    public void set_innerMap(Map<String, Object> _innerMap) {
        this._innerMap = _innerMap;
    }

    @JsonAnySetter
    public void set_innerMap(String key, Object value) {
        if (this._innerMap == null) {
            this._innerMap = new HashMap<>();
        }
        this._innerMap.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> get_innerMap() {
        return _innerMap;
    }
}
