package org.hzero.platform.domain.vo;

/**
 * 个性化表格VO
 *
 * @author xiaoyu.zhao@hand-china.com 2019/01/23 11:41
 */
public class CustomTableColumnVO {
    private String dimensionType;
    private String fieldKey;
    private Long dimensionValue;
    private Integer hidden;
    private Integer fixedLeft;
    private Integer orderSeq;

    public CustomTableColumnVO(String dimensionType, String fieldKey, Long dimensionValue, Integer hidden, Integer fixedLeft,
            Integer orderSeq) {
        this.dimensionType = dimensionType;
        this.fieldKey = fieldKey;
        this.dimensionValue = dimensionValue;
        this.hidden = hidden;
        this.fixedLeft = fixedLeft;
        this.orderSeq = orderSeq;
    }
    public CustomTableColumnVO() {

    }

    public String getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(String dimensionType) {
        this.dimensionType = dimensionType;
    }

    public Long getDimensionValue() {
        return dimensionValue;
    }

    public void setDimensionValue(Long dimensionValue) {
        this.dimensionValue = dimensionValue;
    }

    public Integer getHidden() {
        return hidden;
    }

    public void setHidden(Integer hidden) {
        this.hidden = hidden;
    }

    public Integer getFixedLeft() {
        return fixedLeft;
    }

    public void setFixedLeft(Integer fixedLeft) {
        this.fixedLeft = fixedLeft;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    @Override
    public String toString() {
        return "CustomTableColumnVO{" + "dimensionType='" + dimensionType + '\'' + ", fieldKey='" + fieldKey + '\''
                + ", dimensionValue=" + dimensionValue + ", hidden=" + hidden + ", fixedLeft=" + fixedLeft
                + ", orderSeq=" + orderSeq + '}';
    }
}
