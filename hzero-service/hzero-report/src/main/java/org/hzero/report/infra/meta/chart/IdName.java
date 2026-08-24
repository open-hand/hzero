package org.hzero.report.infra.meta.chart;

/**
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:23:55
 */
public class IdName {
    private String id;
    private String name;
    private boolean selected;

    public IdName() {
    }

    public IdName(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public IdName(String id, String name, boolean selected) {
        this(id, name);
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
