package org.hzero.boot.imported.domain.entity;

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

    public TemplateColumnTl setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public TemplateColumnTl setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getColumnName() {
        return columnName;
    }

    public TemplateColumnTl setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
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
