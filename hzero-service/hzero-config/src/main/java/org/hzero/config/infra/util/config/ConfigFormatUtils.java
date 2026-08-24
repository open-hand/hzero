package org.hzero.config.infra.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.choerodon.core.exception.CommonException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 配置文件格式工具类
 * @author XCXCXCXCX
 */
public class ConfigFormatUtils {

    private static final ObjectMapper YAM_MAPPER = new ObjectMapper(new YAMLFactory());

    public static Map<String, Object> convertTextToMap(final ConfigFileFormat type, final String configText) {
        if (StringUtils.isEmpty(configText)) {
            return new HashMap<>();
        }
        switch (type) {
            case YAML:
                return parseYaml(configText);
            case JSON:
                return parseJson(configText);
            case PROPERTIES:
                return parseProperties(configText);
            default:
                throw new UnsupportedOperationException("never happened");
        }
    }

    public static Map<String, Object> jsonToProperties(Map<String, Object> jsonMap) {
        Map<String, Object> propertiesMap = new HashMap<>();
        transfer(null, jsonMap, propertiesMap);
        return propertiesMap;
    }

    private static void transfer(String parentKey, Map<String, Object> jsonMap, Map<String, Object> propertiesMap) {
        if (CollectionUtils.isEmpty(jsonMap)) {
            return;
        }
        String prefix = parentKey == null ? "" : parentKey + ".";
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String key = prefix + entry.getKey();
            doTransfer(key, entry.getValue(), propertiesMap);
        }
    }

    private static void doTransfer(String key, Object value, Map<String, Object> propertiesMap) {
        if (value instanceof Map) {
            transfer(key, (Map<String, Object>) value, propertiesMap);
        } else if (value instanceof String) {
            propertiesMap.put(key, value);
        }
    }

    private static Map<String, Object> parseJson(String configText) {
        return com.alibaba.fastjson.JSON.parseObject(configText, Map.class);
    }


    private static Map<String, Object> parseProperties(String content) {

        try {
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
            Set<Object> keys = properties.keySet();
            Map<String, Object> map = new LinkedHashMap<>();
            for (Object k : keys) {
                map.put((String) k, properties.get(k));
            }
            return map;
        } catch (IOException e) {
            throw new CommonException("parse properties failed", e);
        }

    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseYaml(String content) {
        try {
            LinkedHashMap<String, Object> root = YAM_MAPPER.readValue(content, LinkedHashMap.class);
            return (LinkedHashMap) TreeProcess.mapParseRecursive(root);
        } catch (IOException e) {
            throw new CommonException("parse yaml failed", e);
        }
    }

}
