package org.hzero.plugin.platform.hr.api.dto;

/**
 * 岗位导入DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/04/24 17:52
 */
public class PositionImportDTO {
    /**
     * 部门编码
     */
    private String unitCode;
    /**
     * 岗位编码
     */
    private String positionCode;
    /**
     * 岗位名称
     */
    private String positionName;
    /**
     * 描述
     */
    private String description;
    /**
     * 排序号
     */
    private Integer orderSeq;
    /**
     * 上级岗位编号
     */
    private String parentPositionCode;

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
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

    public String getParentPositionCode() {
        return parentPositionCode;
    }

    public void setParentPositionCode(String parentPositionCode) {
        this.parentPositionCode = parentPositionCode;
    }

    @Override
    public String toString() {
        return "PositionImportDTO{" +
                "unitCode='" + unitCode + '\'' +
                ", positionCode='" + positionCode + '\'' +
                ", positionName='" + positionName + '\'' +
                ", description='" + description + '\'' +
                ", orderSeq=" + orderSeq +
                ", parentPositionCode='" + parentPositionCode + '\'' +
                '}';
    }
}
