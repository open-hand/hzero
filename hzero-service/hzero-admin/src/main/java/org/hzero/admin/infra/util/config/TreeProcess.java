package org.hzero.admin.infra.util.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 递归解析map数据
 *
 * @author wuguokai
 */
class TreeProcess {

    private TreeProcess() {
    }

    /**
     * 递归解析map形式集合
     *
     * @param map map
     * @return map
     */
    @SuppressWarnings("unchecked")
    static Object mapParseRecursive(Map<String, Object> map) {
        Map<String, Object> res = new LinkedHashMap<>();
        map.forEach((key, o) -> {
            if (o instanceof Map) {
                Map tmpMap = (Map) o;
                Map kvMap = (Map) mapParseRecursive(tmpMap);
                kvMap.forEach((kvKey, value) ->
                    res.put(key + "." + kvKey, value)
                );
            } else if (o instanceof List) {
                Map tmpMap = listParseRecursive((List) o);
                tmpMap.forEach((tmpKey, value) ->
                    res.put(key + tmpKey, value.toString())
                );
            } else {
                Object value = o;
                res.put(key, value);
            }
        });
        return res;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> listParseRecursive(List list) {
        Map<String, Object> res = new LinkedHashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof Map) {
                Map tmpMap = (Map) mapParseRecursive((Map) o);
                int finalI = i;
                tmpMap.forEach((kvKey, value) -> {
                    res.put("[" + finalI + "]." + kvKey, value.toString());
                });
            } else if (o instanceof List) {
                Map tmpMap = listParseRecursive((List) o);
                int finalI1 = i;
                tmpMap.forEach((key, value) -> {
                    res.put("[" + finalI1 + "]" + key, value.toString());
                });
            } else if (o != null) {
                res.put("[" + i + "]", o.toString());
            }
        }
        return res;
    }
}
