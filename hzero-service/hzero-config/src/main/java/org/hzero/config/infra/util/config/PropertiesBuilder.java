package org.hzero.config.infra.util.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * properties类型配置文件构建器实现
 *
 * @author wuguokai
 */
public class PropertiesBuilder implements Builder {
    @Override
    public String build(Map<String, Object> kv) {
        StringBuilder res = new StringBuilder();
        List<String> keyList = new ArrayList<>(kv.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            res.append(key);
            res.append("=");
            res.append(kv.getOrDefault(key, ""));
            res.append("\n");
        }
        return res.toString();
    }
}
