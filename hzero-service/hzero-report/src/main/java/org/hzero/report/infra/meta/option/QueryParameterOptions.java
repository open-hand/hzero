package org.hzero.report.infra.meta.option;

import java.io.Serializable;

/**
 * 报表查询参数类（用于构建JSON配置字符）
 *
 * @author xianzhi.chen@hand-china.com 2018年10月22日下午4:33:39
 */
public class QueryParameterOptions implements Serializable {

    private static final long serialVersionUID = 7975880105664108114L;

    private int ordinal;
    private String name;
    private String text;
    private String formElement;
    private String content;
    private String defaultValue;
    private String defaultMeaning;
    private String dataSource;
    private String dataType = "string";
    private String comment;
    private int width = 100;
    private int height = 25;
    private int isRequired;

    public int getOrdinal() {
        return ordinal;
    }

    public QueryParameterOptions setOrdinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }

    /**
     * 获取报表查询参数名称
     *
     * @return 报表查询参数名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置报表查询参数名称
     *
     * @param name 参数名称
     */
    public QueryParameterOptions setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 获取报表查询参数对应的标题(中文名)
     *
     * @return 报表查询参数对应的标题(中文名)
     */
    public String getText() {
        if (this.text == null || this.text.trim().length() == 0) {
            return "";
        }
        return this.text.trim();
    }

    /**
     * 设置报表查询参数对应的标题(中文名)
     *
     * @param text 报表查询参数名称对应的标题(中文名)
     */
    public QueryParameterOptions setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * 获取报表查询参数对应的html表单input元素
     *
     * @return html表单input元素(select, text等)
     */
    public String getFormElement() {
        return this.formElement == null ? "" : this.formElement.trim();
    }

    /**
     * 设置报表查询参数对应的html表单input元素
     *
     * @param formElement html表单input元素(select,text等)
     */
    public QueryParameterOptions setFormElement(String formElement) {
        this.formElement = formElement;
        return this;
    }

    /**
     * 获取报表查询参数对应的内容（sql语句|文本字符|空)
     *
     * @return 报表查询参数对应的内容（sql语句|文本字符|空)
     */
    public String getContent() {
        return this.content == null ? "" : this.content.trim();
    }

    /**
     * 设置报表查询参数对应的内容（sql语句|文本字符|空)
     *
     * @param content t报表查询参数对应的内容（sql语句|文本字符|空)
     */
    public QueryParameterOptions setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 获取报表查询参数对应的默认值
     *
     * @return 报表查询参数对应的默认值
     */
    public String getDefaultValue() {
        return (this.defaultValue == null || this.defaultValue.trim().length() == 0) ? ""
                : this.defaultValue.trim();
    }

    /**
     * 设置报表查询参数对应的默认值
     *
     * @param defaultValue 报表查询参数对应的默认值
     */
    public QueryParameterOptions setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * 获取报表查询参数的默认值对应的标题
     *
     * @return 报表查询参数的默认值对应的标题
     */
    public String getDefaultMeaning() {
        return (this.defaultMeaning == null || this.defaultMeaning.trim().length() == 0) ? ""
                : this.defaultMeaning.trim();
    }

    /**
     * 设置报表查询参数的默认值对应的标题
     *
     * @param defaultMeaning 报表查询参数的默认值对应的标题
     */
    public QueryParameterOptions setDefaultMeaning(String defaultMeaning) {
        this.defaultMeaning = defaultMeaning;
        return this;
    }

    /**
     * 获取报表查询参数的数据来源 取值 为[sql:根据sql语句查询得出,text:从文本内容得出,none:无数据来源]
     *
     * @return sql|text|none
     */
    public String getDataSource() {
        return (this.dataSource == null || this.dataSource.trim().length() == 0) ? "none" : this.dataSource;
    }

    /**
     * 设置报表查询参数数据来源
     *
     * @param dataSource 取值 为[sql:根据sql语句查询得出,text:从文本内容得出,none:无数据来源]
     */
    public QueryParameterOptions setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    /**
     * 获取表查询参数的数据类型(string|float|integer|date)，默认是string
     *
     * @return
     */
    public String getDataType() {
        return (this.dataType == null || this.dataType.trim().length() == 0) ? "string" : this.dataType;
    }

    /**
     * 获取报表查询参数的数据类型(string|float|integer|date)，默认是string
     *
     * @param dataType(string|float|integer|date)
     */
    public QueryParameterOptions setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * 获取报表查询参数备注
     *
     * @return
     */
    public String getComment() {
        return this.comment == null ? "" : this.comment;
    }

    /**
     * 设置报表查询参数备注
     *
     * @param comment
     */
    public QueryParameterOptions setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * 获取报表查询参数的数据来源
     *
     * @return 宽度的像素值, 默认为100
     */
    public int getWidth() {
        return width;
    }

    /**
     * 设置报表查询参数表单控件的宽度(单位：像素)
     *
     * @param width 宽度的像素值
     */
    public QueryParameterOptions setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * 获取报表查询参数表单控件的高度(单位：像素)
     *
     * @return 高度的像素值, 默认为25
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * 设置报表查询参数表单控件的高度(单位：像素)
     *
     * @param height 高度的像素
     */
    public QueryParameterOptions setHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * 获取报表查询参数的是否必选
     *
     * @return
     */
    public int getIsRequired() {
        return this.isRequired;
    }

    /**
     * 设置报表查询参数是否必选
     *
     * @param isRequired 1|0
     */
    public QueryParameterOptions setIsRequired(int isRequired) {
        this.isRequired = isRequired;
        return this;
    }

}
