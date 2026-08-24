package org.hzero.report.infra.util;

import org.apache.commons.lang3.StringUtils;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/01 19:57
 */
public class CustomTokenUtils {

    private CustomTokenUtils() {
    }

    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    public static String getToken() {
        String token = "";
        try {
            token = org.hzero.core.util.TokenUtils.getToken();
        } catch (Exception e) {
            if (StringUtils.isNotBlank(TOKEN.get())) {
                token = TOKEN.get();
            }
        }
        return token;
    }

    public static void setToken(String token) {
        TOKEN.set(token);
    }

    public static void clear() {
        TOKEN.remove();
    }
}
