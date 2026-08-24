package org.hzero.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 安全工具
 *
 * @author bojiangzhou 2019/12/18
 */
public class SecurityUtils {

    /**
     * 防止CSV注入，替换 <i>+、-、=、@</i> 等特殊字符，加上 <i>\t</i> 制表符
     * 
     * @param str 替换的字符串
     * @return 处理后的字符串
     */
    public static String preventCsvInjection(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }

        if (str.startsWith("+")) {
            str = StringUtils.replace(str, "+", "'+");
        }
        else if (str.startsWith("=")) {
            str = StringUtils.replace(str, "=", "'=");
        }
        else if (str.startsWith("-")) {
            str = StringUtils.replace(str, "-", "'-");
        }
        else if (str.startsWith("@")) {
            str = StringUtils.replace(str, "@", "'@");
        }

        return str;
    }

}
