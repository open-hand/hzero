package org.hzero.config.infra.util.config;

import io.choerodon.core.exception.CommonException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum ConfigFileFormat {

    PROPERTIES("properties"), YAML("yml", "yaml"), JSON("json");

    private Set<String> keys = new HashSet<>();

    ConfigFileFormat(String... keys) {
        this.keys.addAll(Arrays.asList(keys));
    }

    /**
     * 判断文件类型
     *
     * @param key 文件后缀
     * @return 文件类型
     */
    public static ConfigFileFormat fromString(String key) {
        if (key == null || key.length() == 0) {
            throw new CommonException("error.format.type");
        }
        for (ConfigFileFormat fileFormat : values()) {
            if (fileFormat.keys.contains(key)) {
                return fileFormat;
            }
        }
        throw new CommonException("error.format.type");
    }

}
