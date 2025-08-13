package org.hzero.admin.infra.util.config;

import io.choerodon.core.exception.CommonException;

public enum ConfigFileFormat {

    PROPERTIES("properties"), YML("yml"), YAML("yaml"), JSON("json");

    private String value;

    ConfigFileFormat(String value) {
        this.value = value;
    }

    /**
     * 判断文件类型
     *
     * @param value 文件后缀
     * @return 文件类型
     */
    public static ConfigFileFormat fromString(String value) {
        if (value == null || value.length() == 0) {
            throw new CommonException("error.format.type");
        }
        switch (value) {
            case "properties":
                return PROPERTIES;
            case "yml":
                return YML;
            case "yaml":
                return YAML;
            case "json":
                return JSON;
            default:
                throw new CommonException("error.format.type");
        }
    }

    /**
     * 判断格式化
     *
     * @param value 文件后缀
     * @return boolean
     */
    public static boolean isValidFormat(String value) {
        try {
            fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getValue() {
        return value;
    }
}
