package org.hzero.imported.domain.entity;

/**
 * description
 *
 * @author fanghan.liu 2020/03/26 13:42
 */
public class TemplateLineTl {

    private Long id;
    private String lang;
    private String columnName;

    public Long getId() {
        return id;
    }

    public TemplateLineTl setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public TemplateLineTl setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getColumnName() {
        return columnName;
    }

    public TemplateLineTl setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    @Override
    public String toString() {
        return "TemplateLineTl{" +
                "lang='" + lang + '\'' +
                ", columnName='" + columnName + '\'' +
                '}';
    }
}
