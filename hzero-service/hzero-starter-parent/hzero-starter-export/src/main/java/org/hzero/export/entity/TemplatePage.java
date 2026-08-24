package org.hzero.export.entity;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 导入sheet页
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
public class TemplatePage {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    private Long id;
    private Long headerId;
    @NotNull
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getDatasourceCode() {
        return datasourceCode;
    }

    public void setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRuleScriptCode() {
        return ruleScriptCode;
    }

    public void setRuleScriptCode(String ruleScriptCode) {
        this.ruleScriptCode = ruleScriptCode;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public List<TemplateColumn> getTemplateColumnList() {
        return templateColumnList;
    }

    public void setTemplateColumnList(List<TemplateColumn> templateColumnList) {
        this.templateColumnList = templateColumnList;
    }

    public String getSheetIndexMeaning() {
        return sheetIndexMeaning;
    }

    public void setSheetIndexMeaning(String sheetIndexMeaning) {
        this.sheetIndexMeaning = sheetIndexMeaning;
    }

    public String getDatasourceDesc() {
        return datasourceDesc;
    }

    public void setDatasourceDesc(String datasourceDesc) {
        this.datasourceDesc = datasourceDesc;
    }

    public String getScriptDescription() {
        return scriptDescription;
    }

    public void setScriptDescription(String scriptDescription) {
        this.scriptDescription = scriptDescription;
    }

    @Override
    public String toString() {
        return "TemplatePage{" +
                "id=" + id +
                ", headerId=" + headerId +
                ", sheetIndex=" + sheetIndex +
                ", sheetName='" + sheetName + '\'' +
                ", datasourceCode='" + datasourceCode + '\'' +
                ", tableName='" + tableName + '\'' +
                ", ruleScriptCode='" + ruleScriptCode + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", startLine=" + startLine +
                ", tenantId=" + tenantId +
                ", templateColumnList=" + templateColumnList +
                ", sheetIndexMeaning='" + sheetIndexMeaning + '\'' +
                ", datasourceDesc='" + datasourceDesc + '\'' +
                ", scriptDescription='" + scriptDescription + '\'' +
                '}';
    }
}
