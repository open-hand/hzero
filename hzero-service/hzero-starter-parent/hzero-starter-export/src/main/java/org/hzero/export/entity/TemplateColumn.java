package org.hzero.export.entity;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 通用模板行
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
public class TemplateColumn {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    private Long id;
    @NotNull
    private Long targetId;
    @NotNull
    private Integer columnIndex;
    @NotBlank
    private String columnName;
    @NotBlank
    private String columnCode;
    @NotBlank
    private String columnType;
    private Long length;
    private String formatMask;
    @NotNull
    private Integer enabledFlag;
    @NotNull
    private Integer nullableFlag;
    @NotNull
    private Integer validateFlag;
    @NotNull
    private Integer changeDataFlag;
    private String sampleData;
    private String description;
    private String maxValue;
    private String minValue;
    private String validateSet;
    private String regularExpression;
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    private String columnTypeMeaning;

    @JsonProperty("templateLineTls")
    private List<TemplateColumnTl> templateColumnTls;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getFormatMask() {
        return formatMask;
    }

    public void setFormatMask(String formatMask) {
        this.formatMask = formatMask;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Integer getNullableFlag() {
        return nullableFlag;
    }

    public void setNullableFlag(Integer nullableFlag) {
        this.nullableFlag = nullableFlag;
    }

    public Integer getValidateFlag() {
        return validateFlag;
    }

    public void setValidateFlag(Integer validateFlag) {
        this.validateFlag = validateFlag;
    }

    public Integer getChangeDataFlag() {
        return changeDataFlag;
    }

    public void setChangeDataFlag(Integer changeDataFlag) {
        this.changeDataFlag = changeDataFlag;
    }

    public String getSampleData() {
        return sampleData;
    }

    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getValidateSet() {
        return validateSet;
    }

    public void setValidateSet(String validateSet) {
        this.validateSet = validateSet;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getColumnTypeMeaning() {
        return columnTypeMeaning;
    }

    public void setColumnTypeMeaning(String columnTypeMeaning) {
        this.columnTypeMeaning = columnTypeMeaning;
    }

    public List<TemplateColumnTl> getTemplateColumnTls() {
        return templateColumnTls;
    }

    public void setTemplateColumnTls(List<TemplateColumnTl> templateColumnTls) {
        this.templateColumnTls = templateColumnTls;
    }

    @Override
    public String toString() {
        return "TemplateColumn{" +
                "id=" + id +
                ", targetId=" + targetId +
                ", columnIndex=" + columnIndex +
                ", columnName='" + columnName + '\'' +
                ", columnCode='" + columnCode + '\'' +
                ", columnType='" + columnType + '\'' +
                ", length=" + length +
                ", formatMask='" + formatMask + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", nullableFlag=" + nullableFlag +
                ", validateFlag=" + validateFlag +
                ", changeDataFlag=" + changeDataFlag +
                ", sampleData='" + sampleData + '\'' +
                ", description='" + description + '\'' +
                ", maxValue='" + maxValue + '\'' +
                ", minValue='" + minValue + '\'' +
                ", validateSet='" + validateSet + '\'' +
                ", regularExpression='" + regularExpression + '\'' +
                ", tenantId=" + tenantId +
                ", columnTypeMeaning='" + columnTypeMeaning + '\'' +
                ", templateColumnTls=" + templateColumnTls +
                '}';
    }
}
