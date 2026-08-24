package org.hzero.report.domain.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.core.util.UUIDUtils;
import org.hzero.mybatis.common.query.JoinColumn;
import org.hzero.mybatis.common.query.Where;
import org.hzero.report.domain.repository.ReportRepository;
import org.hzero.report.infra.enums.ReportTypeEnum;
import org.hzero.report.infra.meta.form.FormElement;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 报表信息
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
@ApiModel("报表信息")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_report")
@MultiLanguage
public class Report extends AuditDomain {

    public static final String FIELD_REPORT_ID = "reportId";
    public static final String FIELD_DATASET_ID = "datasetId";
    public static final String FIELD_REPORT_UUID = "reportUuid";
    public static final String FIELD_REPORT_TYPE_CODE = "reportTypeCode";
    public static final String FIELD_REPORT_CODE = "reportCode";
    public static final String FIELD_REPORT_NAME = "reportName";
    public static final String FIELD_META_COLUMNS = "metaColumns";
    public static final String FIELD_OPTIONS = "options";
    public static final String FIELD_TEMPLATE_TYPE_CODE = "templateTypeCode";
    public static final String FIELD_PAGE_FLAG = "pageFlag";
    public static final String FIELD_ASYNC_FLAG = "asyncFlag";
    public static final String FIELD_LIMIT_ROWS = "limitRows";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_EXPORT_TYPE = "exportType";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 重复性校验
     *
     * @param reportRepository 仓库
     * @return 是否重复
     */
    public boolean validateReportRepeat(ReportRepository reportRepository) {
        Report rep = new Report();
        rep.setTenantId(tenantId);
        rep.setReportCode(reportCode);
        int i = reportRepository.selectCount(rep);
        return i <= 0;
    }

    /**
     * 验证报表模板类型合法性
     *
     * @return 是否合法
     */
    public boolean validateReportTemplateType() {
        return !Objects.equals(ReportTypeEnum.DOCUMENT.getValue(), reportTypeCode) || !StringUtils.isBlank(templateTypeCode);
    }

    /**
     * 初始化Uuid
     */
    public void initReportUuid() {
        this.reportUuid = UUIDUtils.generateUUID();
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Where
    @Id
    @GeneratedValue
    @Encrypt
    private Long reportId;
    @ApiModelProperty("报表UUID")
    @Where
    private String reportUuid;
    @ApiModelProperty("数据集，hrpt_dataset.dataset_id")
    @Encrypt
    private Long datasetId;
    @ApiModelProperty(value = "报表类型，值集:HRPT.REPORT_TYPE")
    @LovValue(value = "HRPT.REPORT_TYPE", meaningField = "reportTypeMeaning")
    @NotBlank
    private String reportTypeCode;
    @ApiModelProperty(value = "报表代码")
    @Pattern(regexp = Regexs.CODE)
    @NotBlank
    private String reportCode;
    @ApiModelProperty(value = "报表名称")
    @NotBlank
    @MultiLanguageField
    private String reportName;
    @ApiModelProperty(value = "报表列集合元数据(JSON格式)")
    private String metaColumns;
    @ApiModelProperty(value = "报表配置选项(JSON格式)")
    private String options;
    @ApiModelProperty(value = "模板类型，值集:HRPT.TEMPLATE_TYPE")
    @LovValue(value = "HRPT.TEMPLATE_TYPE", meaningField = "templateTypeMeaning")
    private String templateTypeCode;
    @ApiModelProperty(value = "分页标识")
    @NotNull
    private Integer pageFlag;
    @ApiModelProperty(value = "异步标识")
    @NotNull
    private Integer asyncFlag;
    @ApiModelProperty(value = "限制行数")
    private Long limitRows;
    @ApiModelProperty(value = "启用标识")
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "排序号")
    private Long orderSeq;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty("租户ID")
    @NotNull
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty("导出类型")
    private String exportType;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty(value = "报表类型描述")
    @Transient
    private String reportTypeMeaning;
    @Transient
    private String templateTypeMeaning;
    @ApiModelProperty(value = "数据集名称")
    @Transient
    private String datasetName;
    @ApiModelProperty(value = "租户名称")
    @Transient
    private String tenantName;
    /**
     * 数据集关联属性
     */
    @ApiModelProperty(value = "报表SQL语句")
    @JsonIgnore
    @Transient
    @JoinColumn(joinName = "datasetJoin", field = Dataset.FIELD_SQL_TEXT)
    private String sqlText;
    @ApiModelProperty(value = "查询条件列属性集合(JSON格式)")
    @Transient
    @JoinColumn(joinName = "datasetJoin", field = Dataset.FIELD_QUERY_PARAMS)
    private String queryParams;
    @ApiModelProperty(value = "数据源")
    @JsonIgnore
    @Transient
    @JoinColumn(joinName = "datasetJoin", field = Dataset.FIELD_DATASOURCE_CODE)
    private String datasourceCode;
    @ApiModelProperty(value = "报表参数对象集合")
    @Transient
    private List<FormElement> formElements;
    @ApiModelProperty(value = "导出类型")
    @Transient
    private List<String> exportTypeList;
    @ApiModelProperty(value = "导出类型")
    @Transient
    private String reportFilename;
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getReportId() {
        return reportId;
    }

    public Report setReportId(Long reportId) {
        this.reportId = reportId;
        return this;
    }

    public String getReportUuid() {
        return reportUuid;
    }

    public Report setReportUuid(String reportUuid) {
        this.reportUuid = reportUuid;
        return this;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public Report setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
        return this;
    }

    public String getReportTypeCode() {
        return reportTypeCode;
    }

    public Report setReportTypeCode(String reportTypeCode) {
        this.reportTypeCode = reportTypeCode;
        return this;
    }

    public String getReportCode() {
        return reportCode;
    }

    public Report setReportCode(String reportCode) {
        this.reportCode = reportCode;
        return this;
    }

    public String getReportName() {
        return reportName;
    }

    public Report setReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public String getMetaColumns() {
        return metaColumns;
    }

    public Report setMetaColumns(String metaColumns) {
        this.metaColumns = metaColumns;
        return this;
    }

    public String getOptions() {
        return options;
    }

    public Report setOptions(String options) {
        this.options = options;
        return this;
    }

    public String getTemplateTypeCode() {
        return templateTypeCode;
    }

    public Report setTemplateTypeCode(String templateTypeCode) {
        this.templateTypeCode = templateTypeCode;
        return this;
    }

    public Integer getPageFlag() {
        return pageFlag;
    }

    public Report setPageFlag(Integer pageFlag) {
        this.pageFlag = pageFlag;
        return this;
    }

    public Integer getAsyncFlag() {
        return asyncFlag;
    }

    public Report setAsyncFlag(Integer asyncFlag) {
        this.asyncFlag = asyncFlag;
        return this;
    }

    public Long getLimitRows() {
        return limitRows;
    }

    public Report setLimitRows(Long limitRows) {
        this.limitRows = limitRows;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Report setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public Report setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Report setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Report setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getExportType() {
        return exportType;
    }

    public Report setExportType(String exportType) {
        this.exportType = exportType;
        return this;
    }

    public String getReportTypeMeaning() {
        return reportTypeMeaning;
    }

    public Report setReportTypeMeaning(String reportTypeMeaning) {
        this.reportTypeMeaning = reportTypeMeaning;
        return this;
    }

    public String getTemplateTypeMeaning() {
        return templateTypeMeaning;
    }

    public Report setTemplateTypeMeaning(String templateTypeMeaning) {
        this.templateTypeMeaning = templateTypeMeaning;
        return this;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public Report setDatasetName(String datasetName) {
        this.datasetName = datasetName;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Report setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getSqlText() {
        return sqlText;
    }

    public Report setSqlText(String sqlText) {
        this.sqlText = sqlText;
        return this;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public Report setQueryParams(String queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public String getDatasourceCode() {
        return datasourceCode;
    }

    public Report setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
        return this;
    }

    public List<FormElement> getFormElements() {
        return formElements;
    }

    public Report setFormElements(List<FormElement> formElements) {
        this.formElements = formElements;
        return this;
    }

    public List<String> getExportTypeList() {
        return exportTypeList;
    }

    public Report setExportTypeList(List<String> exportTypeList) {
        this.exportTypeList = exportTypeList;
        return this;
    }

    public String getReportFilename() {
        return reportFilename;
    }

    public Report setReportFilename(String reportFilename) {
        this.reportFilename = reportFilename;
        return this;
    }
}
