package org.hzero.boot.imported.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.excel.entity.Column;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 通用模板行
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
public class TemplateColumn extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_TARGET_ID = "targetId";
    public static final String FIELD_COLUMN_INDEX = "columnIndex";
    public static final String FIELD_COLUMN_NAME = "columnName";
    public static final String FIELD_COLUMN_CODE = "columnCode";
    public static final String FIELD_COLUMN_TYPE = "columnType";
    public static final String FIELD_LENGTH = "length";
    public static final String FIELD_FORMAT_MASK = "formatMask";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_NULLABLE_FLAG = "nullableFlag";
    public static final String FIELD_VALIDATE_FLAG = "validateFlag";
    public static final String FIELD_CHANGE_DATA_FLAG = "changeDataFlag";
    public static final String FIELD_CHANGE_DATA_URL = "changeDataUrl";
    public static final String FIELD_CHANGE_DATA_CODE = "changeDataCode";
    public static final String FIELD_MAX_VALUE = "maxValue";
    public static final String FIELD_MIN_VALUE = "minValue";
    public static final String FIELD_VALIDATE_SET = "validateSet";
    public static final String FIELD_REGULAR_EXPRESSION = "regularExpression";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public Column toColumn() {
        return new Column().setColumnType(HimpBootConstants.ColumnType.MULTI.equals(columnType) ? Column.STRING : columnType).setIndex(columnIndex).setName(columnName).setFormat(formatMask);
    }


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


    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getId() {
        return id;
    }

    public TemplateColumn setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * @return 模板目标ID, himp_template_target.id
     */
    public Long getTargetId() {
        return targetId;
    }

    public TemplateColumn setTargetId(Long targetId) {
        this.targetId = targetId;
        return this;
    }

    /**
     * @return 列序号
     */
    public Integer getColumnIndex() {
        return columnIndex;
    }

    public TemplateColumn setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }

    /**
     * @return 列名称
     */
    public String getColumnName() {
        return columnName;
    }

    public TemplateColumn setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * @return 列代码
     */
    public String getColumnCode() {
        return columnCode;
    }

    public TemplateColumn setColumnCode(String columnCode) {
        this.columnCode = columnCode;
        return this;
    }

    /**
     * @return 列类型
     */
    public String getColumnType() {
        return columnType;
    }

    public TemplateColumn setColumnType(String columnType) {
        this.columnType = columnType;
        return this;
    }

    /**
     * @return 列长度
     */
    public Long getLength() {
        return length;
    }

    public TemplateColumn setLength(Long length) {
        this.length = length;
        return this;
    }

    /**
     * @return 列值格式掩码
     */
    public String getFormatMask() {
        return formatMask;
    }

    public TemplateColumn setFormatMask(String formatMask) {
        this.formatMask = formatMask;
        return this;
    }

    /**
     * @return 是否有效
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public TemplateColumn setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 必输验证标识
     */
    public Integer getNullableFlag() {
        return nullableFlag;
    }

    public TemplateColumn setNullableFlag(Integer nullableFlag) {
        this.nullableFlag = nullableFlag;
        return this;
    }

    /**
     * @return 验证标识
     */
    public Integer getValidateFlag() {
        return validateFlag;
    }

    public TemplateColumn setValidateFlag(Integer validateFlag) {
        this.validateFlag = validateFlag;
        return this;
    }

    /**
     * @return 是否开启数据转换
     */
    public Integer getChangeDataFlag() {
        return changeDataFlag;
    }

    public TemplateColumn setChangeDataFlag(Integer changeDataFlag) {
        this.changeDataFlag = changeDataFlag;
        return this;
    }

    /**
     * @return 示例数据
     */
    public String getSampleData() {
        return sampleData;
    }

    public TemplateColumn setSampleData(String sampleData) {
        this.sampleData = sampleData;
        return this;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public TemplateColumn setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * @return 数据中最大值
     */
    public String getMaxValue() {
        return maxValue;
    }

    public TemplateColumn setMaxValue(String maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    /**
     * @return 数据中最小值
     */
    public String getMinValue() {
        return minValue;
    }

    public TemplateColumn setMinValue(String minValue) {
        this.minValue = minValue;
        return this;
    }

    /**
     * @return 验证值集
     */
    public String getValidateSet() {
        return validateSet;
    }

    public TemplateColumn setValidateSet(String validateSet) {
        this.validateSet = validateSet;
        return this;
    }

    /**
     * @return 正则表达式验证
     */
    public String getRegularExpression() {
        return regularExpression;
    }

    public TemplateColumn setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
        return this;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public TemplateColumn setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getColumnTypeMeaning() {
        return columnTypeMeaning;
    }

    public TemplateColumn setColumnTypeMeaning(String columnTypeMeaning) {
        this.columnTypeMeaning = columnTypeMeaning;
        return this;
    }

    @JsonIgnore
    @Override
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @JsonIgnore
    @Override
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @JsonIgnore
    @Override
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @JsonIgnore
    @Override
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }

    public List<TemplateColumnTl> getTemplateColumnTls() {
        return templateColumnTls;
    }

    public TemplateColumn setTemplateColumnTls(List<TemplateColumnTl> templateColumnTls) {
        this.templateColumnTls = templateColumnTls;
        return this;
    }
}
