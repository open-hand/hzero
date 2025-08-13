package org.hzero.boot.imported.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 导入目标
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
public class TemplatePage extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_HEADER_ID = "headerId";
    public static final String FIELD_SHEET_INDEX = "sheetIndex";
    public static final String FIELD_SHEET_NAME = "sheetName";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_RULE_SCRIPT_CODE = "ruleScriptCode";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";

//
// 业务方法(按public protected private顺序排列)
// ------------------------------------------------------------------------------

    private Long id;
    private Long headerId;
    @NotNull
    @LovValue(lovCode = "HIMP.IMPORT_SHEET")
    private Integer sheetIndex;
    @NotBlank
    private String sheetName;
    private String datasourceCode;
    private String tableName;
    private String ruleScriptCode;
    private Integer enabledFlag;
    private Integer startLine;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    private Long tenantId;

    /**
     * 模板ID
     */
    @Valid
    @JsonProperty("templateLineList")
    private List<TemplateColumn> templateColumnList;

    private String sheetIndexMeaning;
    private String datasourceDesc;
    private String scriptDescription;
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getId() {
        return id;
    }

    public TemplatePage setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * @return 模板头ID, himp_template_header.id
     */
    public Long getHeaderId() {
        return headerId;
    }

    public TemplatePage setHeaderId(Long headerId) {
        this.headerId = headerId;
        return this;
    }

    /**
     * @return sheet页序号
     */
    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public TemplatePage setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    /**
     * @return Sheet页名称
     */
    public String getSheetName() {
        return sheetName;
    }

    public TemplatePage setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    /**
     * @return 数据源，hpfm_datasource.datasource_code
     */
    public String getDatasourceCode() {
        return datasourceCode;
    }

    public TemplatePage setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
        return this;
    }

    /**
     * @return 正式数据表名
     */
    public String getTableName() {
        return tableName;
    }

    public TemplatePage setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * @return 脚本编码, hpfm_rule_script.script_code
     */
    public String getRuleScriptCode() {
        return ruleScriptCode;
    }

    public TemplatePage setRuleScriptCode(String ruleScriptCode) {
        this.ruleScriptCode = ruleScriptCode;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public TemplatePage setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public List<TemplateColumn> getTemplateColumnList() {
        return templateColumnList;
    }

    public TemplatePage setTemplateColumnList(List<TemplateColumn> templateColumnList) {
        this.templateColumnList = templateColumnList;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TemplatePage setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getSheetIndexMeaning() {
        return sheetIndexMeaning;
    }

    public TemplatePage setSheetIndexMeaning(String sheetIndexMeaning) {
        this.sheetIndexMeaning = sheetIndexMeaning;
        return this;
    }

    public String getDatasourceDesc() {
        return datasourceDesc;
    }

    public TemplatePage setDatasourceDesc(String datasourceDesc) {
        this.datasourceDesc = datasourceDesc;
        return this;
    }

    public String getScriptDescription() {
        return scriptDescription;
    }

    public TemplatePage setScriptDescription(String scriptDescription) {
        this.scriptDescription = scriptDescription;
        return this;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public TemplatePage setStartLine(Integer startLine) {
        this.startLine = startLine;
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
}
