package org.hzero.report.infra.meta.chart;

/**
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:23:55
 */
public class TextValue {
    private String text;
    private String value;
    private boolean selected;

    public TextValue() {
    }

    public TextValue(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public TextValue(String text, String value, boolean selected) {
        this.text = text;
        this.value = value;
        this.selected = selected;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
