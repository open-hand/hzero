package org.hzero.plugin.platform.hr.api.dto;

/**
 * 组织导入DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/04/24 17:52
 */
public class UnitImportDTO {
    /**
     * 组织编码
     */
    private String unitCode;
    /**
     * 组织名称
     */
    private String unitName;
    /**
     * 组织类型
     */
    private String unitType;
    /**
     * 描述
     */
    private String description;
    /**
     * 排序号
     */
    private Long orderSeq;
    /**
     * 上级组织编码
     */
    private String parentUnitCode;
    /**
     * 快速检索
     */
    private String quickIndex;
    /**
     * 拼音
     */
    private String phoneticize;
    /**
     * 公司名称
     */
    private String companyName;

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

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getParentUnitCode() {
        return parentUnitCode;
    }

    public void setParentUnitCode(String parentUnitCode) {
        this.parentUnitCode = parentUnitCode;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "UnitImportDTO{" +
                "unitCode='" + unitCode + '\'' +
                ", unitName='" + unitName + '\'' +
                ", unitType='" + unitType + '\'' +
                ", description='" + description + '\'' +
                ", orderSeq=" + orderSeq +
                ", parentUnitCode='" + parentUnitCode + '\'' +
                ", quickIndex='" + quickIndex + '\'' +
                ", phoneticize='" + phoneticize + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
