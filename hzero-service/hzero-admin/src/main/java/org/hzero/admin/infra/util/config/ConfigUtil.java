package org.hzero.admin.infra.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.hzero.admin.infra.constant.ConfigConstant;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtil {

    private static final ObjectMapper YAM_MAPPER = new ObjectMapper(new YAMLFactory());

    private ConfigUtil() {
    }

    public static String convertMapToText(final Map<String, Object> configMap, final String type) {
        ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(type);
        Builder builder = BuilderFactory.getBuilder(configFileFormat);
        return builder.build(configMap);
    }

    public static Map<String, Object> convertTextToMap(final String type, final String configText) throws IOException {
        switch (type) {
            case ConfigConstant.CONFIG_TYPE_YAML:
                return parseYaml(configText);
            case ConfigConstant.CONFIG_TYPE_PROPERTIES:
                return parseProperties(configText);
            default:
                return parseProperties(configText);
        }
    }


    private static Map<String, Object> parseProperties(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        Set<Object> keys = properties.keySet();
        Map<String, Object> map = new LinkedHashMap<>();
        for (Object k : keys) {
            map.put((String) k, properties.get(k));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseYaml(String content) throws IOException {
        LinkedHashMap<String, Object> root = YAM_MAPPER.readValue(content, LinkedHashMap.class);
        return (LinkedHashMap) TreeProcess.mapParseRecursive(root);
    }

    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

}
