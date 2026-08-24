package org.hzero.core.util;

import java.util.List;
import java.util.Map;

import org.springframework.util.AntPathMatcher;

/**
 * URL 工具类
 *
 * @author bojiangzhou 2020/05/07
 */
public class UrlUtils {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    /**
     * 从uri中解析Long类型的参数值
     *
     * @param uri        请求的URI
     * @param matchPath  定义的URI
     * @param parameters 参数列表
     * @return Long 类型的值
     */
    public static Long parseLongValueFromUri(final String uri, final String matchPath, List<String> parameters) {
        String val = parseStringValueFromUri(uri, matchPath, parameters);
        if (val != null) {
            return Long.parseLong(val);
        }
        return null;
    }

    /**
     * 从uri中解析String类型的参数值
     *
     * @param uri        请求的URI
     * @param matchPath  定义的URI
     * @param parameters 参数列表
     * @return String 类型的值
     */
    public static String parseStringValueFromUri(final String uri, final String matchPath, List<String> parameters) {
        Map<String, String> map = matcher.extractUriTemplateVariables(matchPath, uri);
        if (map.size() < 1) {
            return null;
        }
        for (String parameter : parameters) {
            String value = map.get(parameter);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

}
