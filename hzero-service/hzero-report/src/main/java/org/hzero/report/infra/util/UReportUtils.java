package org.hzero.report.infra.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bstek.ureport.definition.ReportDefinition;
import org.apache.commons.lang3.StringUtils;

/**
 * ureport工具类
 *
 * @author shuangfei.zhu@hand-china.com 2021/06/17 15:15
 */
public class UReportUtils {

    private static final Map<String, ReportDefinition> CACHE = new ConcurrentHashMap<>(16);

    public static ReportDefinition getCache(String uuid) {
        return CACHE.get(uuid);
    }

    public static void removeCache(String uuid) {
        CACHE.remove(uuid);
    }

    public static void addCache(String uuid, ReportDefinition report) {
        CACHE.put(uuid, report);
    }

    public static Map<String, String> getParams(String url) {
        Map<String, String> result = new HashMap<>(16);
        if (!url.contains("?")) {
            return result;
        }
        String paramStr = url.substring(url.indexOf("?") + 1);
        return getParamsWithQueryString(paramStr);
    }

    public static String getParam(String url, String name) {
        return getParams(url).get(name);
    }

    public static Map<String, String> getParamsWithQueryString(String queryString) {
        Map<String, String> result = new HashMap<>(16);
        if (StringUtils.isBlank(queryString)) {
            return result;
        }
        String[] params = queryString.split("&");
        for (String item : params) {
            int index = item.indexOf("=");
            result.put(item.substring(0, index), item.substring(index + 1));
        }
        return result;
    }

    public static String getParamsWithQueryString(String queryString, String name) {
        return getParamsWithQueryString(queryString).get(name);
    }
}
