package org.hzero.boot.platform.form.domain.vo;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 表单配置行
 *
 * @author xiaoyu.zhao@hand-china.com 2019-11-22
 */
@Table(name = "hpfm_form_line")
public class FormLineVO {

    /**
     * 表单行Id
     */
    @Id
    private Long formLineId;
    /**
     * 表单头Id
     */
    private Long formHeaderId;
    /**
     * 表单行编码
     */
    private String itemCode;
    /**
     * 表单行名称
     */
    private String itemName;
    /**
     * 表单行描述
     */
    private String itemDescription;
    /**
     * 排序
     */
    private Long orderSeq;
    /**
     * 表单行字段类型
     */
    private String itemTypeCode;
    /**
     * 是否必输
     */
    private Integer requiredFlag;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否启用
     */
    private Integer enabledFlag;
    /**
     * 租户Id
     */
    private Long tenantId;
    /**
     * 是否允许更新
     */
    private Integer updatableFlag;
    /**
     * 值约束，正则表达式
     */
    private String valueConstraint;
    /**
     * 值集/值集视图编码
     */
    private String valueSet;

    public String getValueSet() {
        return valueSet;
    }

    public FormLineVO setValueSet(String valueSet) {
        this.valueSet = valueSet;
        return this;
    }

    /**
     * @return 表单配置行ID
     */
    public Long getFormLineId() {
        return formLineId;
    }

    public void setFormLineId(Long formLineId) {
        this.formLineId = formLineId;
    }

    /**
     * @return 表单配置头表
     */
    public Long getFormHeaderId() {
        return formHeaderId;
    }

    public void setFormHeaderId(Long formHeaderId) {
        this.formHeaderId = formHeaderId;
    }

    /**
     * @return 配置项编码
     */
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return 配置项名称
     */
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return 配置项说明
     */
    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
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
     * @return 配置类型，HPFM.ITEM_TYPE
     */
    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    /**
     * @return 是否必输 1:必输 0：不必输
     */
    public Integer getRequiredFlag() {
        return requiredFlag;
    }

    public void setRequiredFlag(Integer requiredFlag) {
        this.requiredFlag = requiredFlag;
    }

    /**
     * @return 默认值
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return 是否启用 1:启用 0：不启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
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
     * @return 是否允许更新
     */
    public Integer getUpdatableFlag() {
        return updatableFlag;
    }

    public void setUpdatableFlag(Integer updatableFlag) {
        this.updatableFlag = updatableFlag;
    }

    /**
     * @return 值约束，正则表达式
     */
    public String getValueConstraint() {
        return valueConstraint;
    }

    public void setValueConstraint(String valueConstraint) {
        this.valueConstraint = valueConstraint;
    }

    @Override
    public String toString() {
        return "FormLineVO{" + "formLineId=" + formLineId + ", formHeaderId=" + formHeaderId + ", itemCode='" + itemCode
                + '\'' + ", itemName='" + itemName + '\'' + ", itemDescription='" + itemDescription + '\''
                + ", orderSeq=" + orderSeq + ", itemTypeCode='" + itemTypeCode + '\'' + ", requiredFlag="
                + requiredFlag + ", defaultValue='" + defaultValue + '\'' + ", enabledFlag=" + enabledFlag
                + ", tenantId=" + tenantId + ", updatableFlag=" + updatableFlag + ", valueConstraint='"
                + valueConstraint + '\'' + '}';
    }
}
