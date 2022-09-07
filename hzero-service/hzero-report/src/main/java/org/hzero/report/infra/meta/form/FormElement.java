package org.hzero.report.infra.meta.form;

/**
 * 表单元素通用属性
 *
 * @author xianzhi.chen@hand-china.com 2018年11月30日下午1:22:02
 */
public abstract class FormElement {
    protected String name;
    protected String text;
    protected String type;
    protected String dataType = "string";
    protected String comment;
    protected int width = 200;
    protected int height = 28;
    protected int isRequired;
    private String defaultText;
    private String defaultValue;

    protected FormElement(String name, String text) {
        this.name = name;
        this.text = text;
    }

    /**
     * 获取Html表单(Form)元素名称
     *
     * @return Html表单(Form)元素名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取Html表单(Form)元素对应的标题(中文名)
     *
     * @return Html表单(Form)元素对应的标题(中文名)
     */
    public String getText() {
        if (this.text == null || this.text.trim().length() == 0) {
            return this.name;
        }
        return this.text.trim();
    }

    /**
     * 获取Html表单(Form)元素对应的控件类型
     *
     * @return text|select|checkbox|datebox|checkboxlist
     */
    public String getType() {
        return this.type;
    }

    /**
     * 获取Html表单(Form)元素表单控件的宽度(单位：像素)
     */
    public String getDataType() {
        return this.dataType;
    }

    /**
     * 获取Html表单(Form)元素的数据来源
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取Html表单(Form)元素的备注
     */
    public String getComment() {
        return this.comment == null ? "" : this.comment;
    }

    /**
     * 设置Html表单(Form)元素的备注
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 获取Html表单(Form)元素的数据来源
     *
     * @return 宽度的像素值, 默认为100
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * 设置Html表单(Form)元素表单控件的宽度(单位：像素)
     *
     * @param width 宽度的像素值
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 获取Html表单(Form)元素表单控件的高度(单位：像素)
     *
     * @return 高度的像素值, 默认为25
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * 设置Html表单(Form)元素表单控件的高度(单位：像素)
     *
     * @param height 高度的像素
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 获取Html表单(Form)元素的是否必选
     */
    public int getIsRequired() {
        return this.isRequired;
    }

    /**
     * 设置Html表单(Form)元素是否必选
     *
     * @param isRequired 1|0
     */
    public void setIsRequired(int isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * 获取Html表单(Form)元素对应的默认值
     *
     * @return Html表单(Form)元素对应的默认值
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * 设置Html表单(Form)元素对应的默认值
     *
     * @param defaultValue Html表单(Form)元素对应的默认值
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 获取Html表单(Form)元素的默认值对应的标题
     *
     * @return Html表单(Form)元素的默认值对应的标题
     */
    public String getDefaultText() {
        return this.defaultText;
    }

    /**
     * 设置Html表单(Form)元素的默认值对应的标题
     *
     * @param defaultText Html表单(Form)元素的默认值对应的标题
     */
    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }
}
