package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 表单配置行
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
@ApiModel("表单配置行")
@VersionAudit
@ModifyAudit
@MultiLanguage
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_form_line")
public class FormLine extends AuditDomain {

    public static final String FIELD_FORM_LINE_ID = "formLineId";
    public static final String FIELD_FORM_HEADER_ID = "formHeaderId";
    public static final String FIELD_ITEM_CODE = "itemCode";
    public static final String FIELD_ITEM_NAME = "itemName";
    public static final String FIELD_ITEM_DESCRIPTION = "itemDescription";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_ITEM_TYPE_CODE = "itemTypeCode";
    public static final String FIELD_REQUIRED_FLAG = "requiredFlag";
    public static final String FIELD_DEFAULT_VALUE = "defaultValue";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_UPDATABLE_FLAG = "updatableFlag";
    public static final String FIELD_VALUE_CONSTRAINT = "valueConstraint";
    public static final String FIELD_VALUE_SET = "valueSet";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    @Encrypt
    private Long formLineId;
    @ApiModelProperty(value = "表单配置头表")
    @NotNull
    @Encrypt
    private Long formHeaderId;
    @ApiModelProperty(value = "配置项编码")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE)
    private String itemCode;
    @ApiModelProperty(value = "配置项名称")
    @NotBlank
    @Length(max = 255)
    @MultiLanguageField
    private String itemName;
    @ApiModelProperty(value = "配置项说明")
    @Length(max = 480)
    @MultiLanguageField
    private String itemDescription;
    @ApiModelProperty(value = "排序号")
    @NotNull
    private Long orderSeq;
    @ApiModelProperty(value = "配置类型，HPFM.ITEM_TYPE")
    @NotBlank
    @LovValue(value = "HPFM.ITEM_TYPE", meaningField = "itemTypeMeaning")
    @Length(max = 30)
    private String itemTypeCode;
    @ApiModelProperty(value = "是否必输 1:必输 0：不必输")
    @NotNull
    @Range(max = 1)
    private Integer requiredFlag;
    @ApiModelProperty(value = "默认值")
    @Length(max = 255)
    private String defaultValue;
    @ApiModelProperty(value = "是否启用 1:启用 0：不启用")
    @NotNull
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID")
    @NotNull
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty("是否允许更新")
    @NotNull
    @Range(max = 1)
    private Integer updatableFlag;
    @ApiModelProperty("值约束，正则表达式")
    @Length(max = 480)
    private String valueConstraint;
    @ApiModelProperty("值集/值集视图编码")
    @Length(max = 80)
    private String valueSet;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String itemTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public String getValueSet() {
        return valueSet;
    }

    public FormLine setValueSet(String valueSet) {
        this.valueSet = valueSet;
        return this;
    }

    public String getItemTypeMeaning() {
        return itemTypeMeaning;
    }

    public void setItemTypeMeaning(String itemTypeMeaning) {
        this.itemTypeMeaning = itemTypeMeaning;
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
}
