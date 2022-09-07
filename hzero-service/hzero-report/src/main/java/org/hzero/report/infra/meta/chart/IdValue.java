package org.hzero.report.infra.meta.chart;

/**
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:23:55
 */
public class IdValue {
    private String id;
    private String value;
    private boolean selected;

    public IdValue() {
    }

    public IdValue(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public IdValue(String id, String value, boolean selected) {
        this(id, value);
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
