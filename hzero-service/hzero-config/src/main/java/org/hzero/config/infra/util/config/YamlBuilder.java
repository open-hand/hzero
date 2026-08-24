package org.hzero.config.infra.util.config;

import java.util.Map;
import java.util.Set;

import io.codearte.props2yaml.Props2YAML;

/**
 * yaml类型配置文件的构建器实现
 *
 * @author wuguokai
 */
public class YamlBuilder implements Builder {
    @Override
    public String build(Map<String, Object> kv) {
        StringBuilder res = new StringBuilder();
        Set<String> keySet = kv.keySet();
        for (String key : keySet) {
            res.append(key);
            res.append("=");
            res.append(kv.getOrDefault(key, ""));
            res.append("\n");
        }
        return Props2YAML.fromContent(res.toString())
                .convert();
    }
}
