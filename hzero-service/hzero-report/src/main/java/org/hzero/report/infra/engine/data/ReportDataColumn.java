package org.hzero.report.infra.engine.data;

/**
 * 报表数据列
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:22:49
 */
public class ReportDataColumn {

    public static final String FILED_NAME = "name";
    public static final String FILED_TEXT = "text";
    public static final String FILED_LINK_REPORT_CODE = "linkReportCode";
    public static final String FILED_LINK_REPORT_PARAM = "linkReportParam";

    private MetaDataColumn metaDataColumn;
    private String parentName;

    public ReportDataColumn(final MetaDataColumn metaDataColumn) {
        this.metaDataColumn = metaDataColumn;
    }

    /**
     * 获取报表列的元数据
     *
     * @return MetaDataColumn
     */
    public MetaDataColumn getMetaData() {
        return this.metaDataColumn;
    }

    /**
     * 获取报表列名
     *
     * @return 报表列名
     */
    public String getName() {
        return this.metaDataColumn.getName();
    }

    /**
     * 获取报表列对应的标题
     *
     * @return 报表列对应的标题
     */
    public String getText() {
        return this.metaDataColumn.getText();
    }

    /**
     * 获取报表列类型(L：布局列,D:维度列，S:统计列)
     *
     * @return 列类型(L ： 布局列, D : 维度列 ， S : 统计列)
     */
    public String getType() {
        return this.metaDataColumn.getType();
    }
    
    /**
     * 获取报表链接报表代码
     *
     * @return 链接报表代码
     */
    public String geLinkReportCode() {
        return this.metaDataColumn.getLinkReportCode();
    }
    
    /**
     * 获取报表链接报表参数名
     *
     * @return 链接报表参数名
     */
    public String geLinkReportParam() {
        return this.metaDataColumn.getLinkReportParam();
    }

    /**
     * 获取报表列名的所属的父列 如果当前列为子统计,则可能需要设置该属性，否则与name属性相同
     *
     * @return 报表列名
     */
    public String getParentName() {
        return (this.parentName == null) ? this.getName() : this.parentName;
    }

    /**
     * 设置报表列名的所属的父列 如果当前列为子统计,则可能需要设置该属性，否则与name属性相同
     *
     * @param parentName 父
     */
    public void setParentName(final String parentName) {
        this.parentName = parentName;
    }

}
