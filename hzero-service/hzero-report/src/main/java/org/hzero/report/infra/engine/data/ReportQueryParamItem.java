package org.hzero.report.infra.engine.data;

/**
 * 查询参数
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午6:57:52
 */
public class ReportQueryParamItem {

    /**
     * 参数值
     */
    public static final String FIELD_VALUE = "value";
    /**
     * 参数值含义
     */
    public static final String FIELD_MEANING = "meaning";

    private String value;
    private String meaning;
    private boolean selected;

    public ReportQueryParamItem(String value, String meaning) {
        this.value = value;
        this.meaning = meaning;
    }

    public ReportQueryParamItem(String name, String text, boolean selected) {
        this(name, text);
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
