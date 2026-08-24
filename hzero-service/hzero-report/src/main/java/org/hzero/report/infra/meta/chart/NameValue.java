package org.hzero.report.infra.meta.chart;

/**
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:23:55
 */
public class NameValue {
    private String name;
    private String value;
    private boolean selected;

    public NameValue() {
    }

    public NameValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public NameValue(String name, String value, boolean selected) {
        this(name, value);
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
