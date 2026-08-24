package org.hzero.report.infra.meta.chart;

/**
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:23:55
 */
public class IdText {
    private String id;
    private String text;
    private boolean selected;

    public IdText() {
    }

    public IdText(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public IdText(String id, String text, boolean selected) {
        this(id, text);
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
