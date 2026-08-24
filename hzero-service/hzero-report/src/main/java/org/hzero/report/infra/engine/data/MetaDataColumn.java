package org.hzero.report.infra.engine.data;

import org.hzero.report.infra.constant.HrptConstants;

/**
 * 报表元数据列类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午6:59:25
 */
public class MetaDataColumn {

    private int ordinal;
    private String name;
    private String text;
    private String dataType;
    private String expression;
    private String linkReportCode;
    private String linkReportParam;
    private String format;
    private String comment;
    private int width;
    private int decimals;
    private String type = HrptConstants.ColumnType.LAYOUT;
    private String sortType = HrptConstants.ColumnSortType.DEFAULT;
    private int percent;
    private int hidden;

    public MetaDataColumn() {}

    public MetaDataColumn(String name, String text, String type) {
        this.name = name;
        this.text = text;
        this.type = type;
    }

    /**
     * 获取报表元数据列的顺序(从1开始)
     *
     * @return 报表元数据列的顺序
     */
    public int getOrdinal() {
        return this.ordinal;
    }

    /**
     * 设置报表元数据列的顺序
     */
    public MetaDataColumn setOrdinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    /**
     * 获取报表元数据列名
     *
     * @return 元数据列名
     */
    public String getName() {
        return this.name == null ? "" : this.name.trim();
    }

    /**
     * 设置报表元数据列名
     */
    public MetaDataColumn setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 获取报表元数据列对应的标题文本
     *
     * @return 报表元数据列对应的标题文本
     */
    public String getText() {
        if (this.text == null || this.text.trim().length() == 0) {
            return this.name;
        }
        return this.text;
    }

    /**
     * 设置报表元数据列对应的标题文本
     */
    public MetaDataColumn setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * 获取报表元数据列数据类型名称
     *
     * @return dataType java.sql.Types中的类型名称
     */
    public String getDataType() {
        return this.dataType;
    }

    /**
     * 设置报表元数据列数据类型名称
     *
     * @param dataType java.sql.Types中的类型名称
     */
    public MetaDataColumn setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * 获取报表元数据计算列的计算表达式
     *
     * @return 计算表达式
     */
    public String getExpression() {
        return this.expression == null ? "" : this.expression;
    }

    /**
     * 设置报表元数据计算列的计算表达式
     *
     * @param expression 计算表达式
     */
    public MetaDataColumn setExpression(String expression) {
        this.expression = expression;
        return this;
    }

    /**
     * 获取报表元数据列的格式 日期：yyyy-MM-dd HH:mm:ss decimal：###，###
     *
     * @return 格式化字符串
     */
    public String getFormat() {
        return this.format == null ? "" : this.format;
    }

    /**
     * 设置报表元数据列的格式
     *
     * @param format 格式化字符串
     */
    public MetaDataColumn setFormat(String format) {
        this.format = format;
        return this;
    }

    public String getLinkReportCode() {
        return linkReportCode;
    }

    public MetaDataColumn setLinkReportCode(String linkReportCode) {
        this.linkReportCode = linkReportCode;
        return this;
    }
    
    public String getLinkReportParam() {
        return linkReportParam;
    }

    public MetaDataColumn setLinkReportParam(String linkReportParam) {
        this.linkReportParam = linkReportParam;
        return this;
    }

    /**
     * 获取报表元数据列宽(单位:像素)
     *
     * @return 列宽(单位 : 像素)
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * 设置报表元数据列宽(单位:像素)
     */
    public MetaDataColumn setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * 获取报表元数据列的精度（即保留多少位小数,默认浮点数为4位，百分比为2位，其他为0)
     *
     * @return 小数精度, 默认浮点数为4位，百分比为2位
     */
    public int getDecimals() {
        return this.decimals;
    }

    /**
     * 设置报表元数据列的精度（即保留多少位小数,默认浮点数为4位，百分比为2位，其他为0)
     *
     * @param decimals 小数精度,默认浮点数为4位，百分比为2位，其他为0
     */
    public MetaDataColumn setDecimals(int decimals) {
        this.decimals = decimals;
        return this;
    }

    /**
     * 获取报表元数据列类型
     *
     * @return 列类型(L ： 布局列, D : 维度列 ， S : 统计列, C : 计算列)
     */
    public String getType() {
        return this.type;
    }

    /**
     * 设置报表元数据列类型
     *
     * @param type (L：布局列,D:维度列，S:统计列,C:计算列)
     */
    public MetaDataColumn setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 设置列排序类型(D:默认,DA：数字优先升序,DD:数字优先降序,CA：字符优先升序,CD:字符优先降序)
     */
    public String getSortType() {
        return this.sortType;
    }

    /**
     * 设置列排序类型
     *
     * @param sortType (D:默认,DA：数字优先升序,DD:数字优先降序,CA：字符优先升序,CD:字符优先降序)
     */
    public MetaDataColumn setSortType(String sortType) {
        this.sortType = sortType;
        return this;
    }

    /**
     * 获取元数据列是否支持百分比显示
     *
     * @return 1|0
     */
    public int getPercent() {
        return this.percent;
    }

    /**
     * 设置元数据列是否支持百分比显示
     */
    public MetaDataColumn setPercent(int percent) {
        this.percent = percent;
        return this;
    }

    /**
     * 获取元数据列是否隐藏
     *
     * @return 1|0
     */
    public int getHidden() {
        return this.hidden;
    }

    /**
     * 设置元数据列是否隐藏
     */
    public MetaDataColumn setHidden(int hidden) {
        this.hidden = hidden;
        return this;
    }

    /**
     * 获取元数据列备注
     *
     * @return
     */
    public String getComment() {
        return this.comment == null ? "" : this.comment;
    }

    /**
     * 设置元数据列备注
     */
    public MetaDataColumn setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public MetaDataColumn copyToNew() {
        return this.copyToNew(this.name, this.text);
    }

    public MetaDataColumn copyToNew(String name, final String text) {
        return this.copyToNew(name, text, this.percent);
    }

    public MetaDataColumn copyToNew(String name, String text, int percent) {
        final MetaDataColumn column = new MetaDataColumn();
        column.setName(name);
        column.setText(text);
        column.setPercent(percent);
        column.setDataType(this.dataType);
        column.setHidden(this.hidden);
        column.setType(this.type);
        column.setSortType(this.sortType);
        column.setWidth(this.width);
        column.setDecimals(this.decimals);
        column.setComment(this.comment);
        return column;
    }
}
