package org.hzero.export.entity;

/**
 * description
 *
 * @author fanghan.liu 2020/03/26 15:31
 */
public class TemplateColumnTl {

    private Long id;
    private String lang;
    private String columnName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return "TemplateColumnTl{" +
                "id=" + id +
                ", lang='" + lang + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}
