package org.hzero.report.infra.engine.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 报表参数类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:37:50
 */
public class ReportParameter {
    private Long reportId;
    private String reportCode;
    private String reportName;
    private String layout;
    private String statColumnLayout;
    private String sqlText;
    private SqlPageInfo sqlPageInfo;
    private List<MetaDataColumn> metaColumns;
    private Set<String> enabledStatColumns;
    private boolean isRowSpan = true;

    public ReportParameter() {
    }

    /**
     * 简单报表参数构造函数
     *
     * @param reportId           报表唯一reportId
     * @param reportCode         报表代码
     * @param reportName         报表名称
     * @param metaColumns        报表元数据列集合
     * @param enabledStatColumns 报表中启用的统计(含计算)列名集合
     * @param isRowSpan          是否生成rowspan（跨行)的表格,默认为true
     * @param sqlText            报表sql查询语句
     * @param sqlPageInfo        SQL分页相关参数（仅适用于简单SQL）
     */
    public ReportParameter(Long reportId, String reportCode, String reportName, List<MetaDataColumn> metaColumns,
                           Set<String> enabledStatColumns, boolean isRowSpan, String sqlText, SqlPageInfo sqlPageInfo) {
        this.reportId = reportId;
        this.reportCode = reportCode;
        this.reportName = reportName;
        this.metaColumns = metaColumns;
        this.enabledStatColumns = enabledStatColumns;
        this.isRowSpan = isRowSpan;
        this.sqlText = sqlText;
        this.sqlPageInfo = sqlPageInfo;
    }

    /**
     * 报表参数构造函数
     *
     * @param reportId           报表唯一reportId
     * @param reportCode         报表代码
     * @param reportName         报表名称
     * @param layout             报表布局形式 H:横向;V:纵向)
     * @param statColumnLayout   报表统计列或计算列布局形式 (H:横向;V:纵向)
     * @param metaColumns        报表元数据列集合
     * @param enabledStatColumns 报表中启用的统计(含计算)列名集合
     * @param isRowSpan          是否生成rowspan（跨行)的表格,默认为true
     * @param sqlText            报表sql查询语句
     * @param sqlPageInfo        SQL分页相关参数（仅适用于简单SQL）
     */
    public ReportParameter(Long reportId, String reportCode, String reportName, String layout, String statColumnLayout,
                           List<MetaDataColumn> metaColumns, Set<String> enabledStatColumns, boolean isRowSpan, String sqlText,
                           SqlPageInfo sqlPageInfo) {
        this.reportId = reportId;
        this.reportCode = reportCode;
        this.reportName = reportName;
        this.layout = layout;
        this.statColumnLayout = statColumnLayout;
        this.metaColumns = metaColumns;
        this.enabledStatColumns = enabledStatColumns;
        this.isRowSpan = isRowSpan;
        this.sqlText = sqlText;
        this.sqlPageInfo = sqlPageInfo;
    }

    /**
     * 获取报表唯一id
     *
     * @return 报表Id
     */
    public Long getReportId() {
        return this.reportId;
    }

    /**
     * 设置报表唯一id
     *
     * @param reportId 报表Id
     */
    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    /**
     * 获取报表代码
     */
    public String getReportCode() {
        return reportCode;
    }

    /**
     * 设置报表代码
     *
     * @param reportCode 报表编码
     */
    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    /**
     * 获取报表名称
     *
     * @return 报表名称
     */
    public String getReportName() {
        return this.reportName;
    }

    /**
     * 设置报表名称
     *
     * @param reportName 报表名称
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * 获取报表布局形式(H:横向;V:纵向)
     *
     * @return Layout
     */
    public String getLayout() {
        return this.layout;
    }

    /**
     * 设置报表布局形式(H:横向;V:纵向)
     *
     * @param layout 报表布局形式(H:横向;V:纵向)
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

    /**
     * 获取报表统计列或计算列布局形式 (H:横向;V:纵向)
     *
     * @return (H : 横向 ; V : 纵向)
     */
    public String getStatColumnLayout() {
        return this.statColumnLayout;
    }

    /**
     * 设置报表统计列或计算列布局形式 (H:横向;V:纵向)
     *
     * @param statColumnLayout (H:横向;V:纵向)
     */
    public void setStatColumnLayout(final String statColumnLayout) {
        this.statColumnLayout = statColumnLayout;
    }

    /**
     * 获取报表SQL语句
     *
     * @return 报表SQL语句
     */
    public String getSqlText() {
        return this.sqlText;
    }

    /**
     * 设置报表SQL语句
     *
     * @param sqlText sql
     */
    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    /**
     * 设置SQL参数
     *
     * @return SqlPageInfo
     */
    public SqlPageInfo getSqlPageInfo() {
        return sqlPageInfo;
    }

    /**
     * 获取SQL参数
     *
     * @param sqlPageInfo 分页信息
     */
    public void setSqlPageInfo(SqlPageInfo sqlPageInfo) {
        this.sqlPageInfo = sqlPageInfo;
    }

    /**
     * 获取报表元数据列集合
     *
     * @return 报表元数据列集合
     */
    public List<MetaDataColumn> getMetaColumns() {
        return this.metaColumns;
    }

    /**
     * 设置报表元数据列集合
     *
     * @param metaColumns 报表元数据列集合
     */
    public void setMetaColumns(List<MetaDataColumn> metaColumns) {
        this.metaColumns = metaColumns;
    }

    /**
     * 获取报表中启用的统计(含计算)列名集合。
     * <p/>
     * 如果未设置任何列名，则在报表中启用全部统计统计(含计算)列
     *
     * @return 报表中启用的统计(含计算)列名集合
     */
    public Set<String> getEnabledStatColumns() {
        return this.enabledStatColumns == null ? new HashSet<>(0) : this.enabledStatColumns;
    }

    /**
     * 设置报表中启用的统计(含计算)列名集合。
     *
     * @param enabledStatColumns 报表中启用的统计(含计算)列名集合
     */
    public void setEnabledStatColumns(Set<String> enabledStatColumns) {
        this.enabledStatColumns = enabledStatColumns;
    }

    /**
     * 获取是否生成rowspan（跨行)的表格,默认为true
     *
     * @return true|false
     */
    public boolean isRowSpan() {
        return this.isRowSpan;
    }

    /**
     * 设置是否生成rowspan（跨行)的表格,默认为true
     *
     * @param isRowSpan true|false
     */
    public void setRowSpan(boolean isRowSpan) {
        this.isRowSpan = isRowSpan;
    }
}
