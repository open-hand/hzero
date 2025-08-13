package org.hzero.iam.domain.entity;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 * 组织部门,涵盖公司, 部门
 * </p>
 *
 * @author allen 2018/6/26
 */
@ModifyAudit
@VersionAudit
@MultiLanguage
@Table(name = "hpfm_unit")
public class HrUnit extends AuditDomain {

    public static final String ENCRYPT_KEY = "hpfm_unit";

    @Id
    @GeneratedValue
    @Encrypt
    private Long unitId;
    private String unitCode;
    @MultiLanguageField
    private String unitName;
    private String unitTypeCode;
    private Long tenantId;
    @MultiLanguageField
    private String description;
    private Integer orderSeq;
    private Long parentUnitId;
    private Boolean enabledFlag;
    private String levelPath;

    @Transient
    @JsonIgnore
    private HrUnit parentHrUnit;

    @Transient
    private List<HrUnit> children;



    /**
     * <p>
     *     验证当前组织是否属于参数组织树
     * </p>
     * @param hrUnit
     * @return
     */
    public Boolean validateBelongTo(HrUnit hrUnit) {
        if (hrUnit == null) {
            return false;
        }

        // 如果匹配, 直接返回
        if (this.getUnitId().equals(hrUnit.getUnitId())) {
            return true;
        }

        // 如果当前组织不匹配, 则继续查找父组织
        if (hrUnit.getParentHrUnit() != null) {
            return this.validateBelongTo(hrUnit.getParentHrUnit());
        }

        return false;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitTypeCode() {
        return unitTypeCode;
    }

    public void setUnitTypeCode(String unitTypeCode) {
        this.unitTypeCode = unitTypeCode;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    public HrUnit getParentHrUnit() {
        return parentHrUnit;
    }

    public void setParentHrUnit(HrUnit parentHrUnit) {
        this.parentHrUnit = parentHrUnit;
    }

    public List<HrUnit> getChildren() {
        return children;
    }

    public void setChildren(List<HrUnit> children) {
        this.children = children;
    }

    public Integer getChildrenCount() {
        if (children == null) {
            return null;
        }
        return children.size();
    }
}
