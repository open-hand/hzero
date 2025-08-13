package io.choerodon.core.iam;

/**
 * role,permission中所用层级
 * @author flyleft
 * 2018/3/26
 */
public enum ResourceLevel {

    /**
     * 全局层
     */
    SITE("site"),

    /**
     * 组织层
     */
    ORGANIZATION("organization"),

    /**
     * 项目层
     */
    PROJECT("project"),

    /**
     * 用户层
     */
    USER("user");

    private final String value;

    ResourceLevel(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
