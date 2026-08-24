package org.hzero.imported.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 导入目标
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
@ApiModel("导入目标")
@VersionAudit
@ModifyAudit
@Table(name = "himp_template_target")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class TemplateTarget extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_HEADER_ID = "headerId";
    public static final String FIELD_SHEET_INDEX = "sheetIndex";
    public static final String FIELD_SHEET_NAME = "sheetName";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_RULE_SCRIPT_CODE = "ruleScriptCode";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_START_LINE = "startLine";

//
// 业务方法(按public protected private顺序排列)
// ------------------------------------------------------------------------------

    public interface Insert{}

//
// 数据库字段
// ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @Encrypt
    private Long headerId;
    @NotNull
    @LovValue(lovCode = "HIMP.IMPORT_SHEET")
    private Integer sheetIndex;
    @NotBlank
    @MultiLanguageField
    private String sheetName;
    @NotNull(groups = TemplateHeader.ServerImport.class)
    private String datasourceCode;
    @NotBlank(groups = TemplateHeader.ServerImport.class)
    private String tableName;
    private String ruleScriptCode;
    private Integer enabledFlag;
    @ApiModelProperty(value = "导入起始行")
    private Integer startLine;
    @ApiModelProperty(value = "租户ID")
    @NotNull(groups = Insert.class)
    @MultiLanguageField
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    /**
     * 模板ID
     */
    @Transient
    @Valid
    private List<TemplateLine> templateLineList;
    @Transient
    private String datasourceDesc;
    @Transient
    private String scriptDescription;
    @Transient
    private String sheetIndexMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getId() {
        return id;
    }

    public TemplateTarget setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * @return 模板头ID, himp_template_header.id
     */
    public Long getHeaderId() {
        return headerId;
    }

    public TemplateTarget setHeaderId(Long headerId) {
        this.headerId = headerId;
        return this;
    }

    /**
     * @return sheet页序号
     */
    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public TemplateTarget setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    /**
     * @return Sheet页名称
     */
    public String getSheetName() {
        return sheetName;
    }

    public TemplateTarget setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    /**
     * @return 数据源，himp_datasource.datasource_id
     */
    public String getDatasourceCode() {
        return datasourceCode;
    }

    public TemplateTarget setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
        return this;
    }

    /**
     * @return 正式数据表名
     */
    public String getTableName() {
        return tableName;
    }

    public TemplateTarget setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * @return 脚本编码, hpfm_rule_script.script_code
     */
    public String getRuleScriptCode() {
        return ruleScriptCode;
    }

    public TemplateTarget setRuleScriptCode(String ruleScriptCode) {
        this.ruleScriptCode = ruleScriptCode;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public TemplateTarget setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public List<TemplateLine> getTemplateLineList() {
        return templateLineList;
    }

    public TemplateTarget setTemplateLineList(List<TemplateLine> templateLineList) {
        this.templateLineList = templateLineList;
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

    public String getDatasourceDesc() {
        return datasourceDesc;
    }

    public TemplateTarget setDatasourceDesc(String datasourceDesc) {
        this.datasourceDesc = datasourceDesc;
        return this;
    }

    public String getScriptDescription() {
        return scriptDescription;
    }

    public TemplateTarget setScriptDescription(String scriptDescription) {
        this.scriptDescription = scriptDescription;
        return this;
    }

    public String getSheetIndexMeaning() {
        return sheetIndexMeaning;
    }

    public TemplateTarget setSheetIndexMeaning(String sheetIndexMeaning) {
        this.sheetIndexMeaning = sheetIndexMeaning;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TemplateTarget setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public TemplateTarget setStartLine(Integer startLine) {
        this.startLine = startLine;
        return this;
    }
}
