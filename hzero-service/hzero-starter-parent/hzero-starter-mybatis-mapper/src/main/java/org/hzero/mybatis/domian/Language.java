package org.hzero.mybatis.domian;

/**
 * @author superlee
 */
public class Language {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";

    private Long id;

    private String code;

    private String name;

    private String description;

    public Long getId() {
        return id;
    }

    public Language setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Language setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public Language setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Language setDescription(String description) {
        this.description = description;
        return this;
    }
}
