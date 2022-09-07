package org.hzero.core.util;

import org.hzero.core.jackson.annotation.Trim;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 空格过滤
 * </p>
 *
 * @author qingsheng.chen 2018/10/16 星期二 11:16
 */
public class TrimUtils {
    private TrimUtils() {

    }

    public static String trim(Trim trim, String value) {
        if (trim == null || value == null) {
            return value;
        }
        if (Trim.TrimMode.ALL.equals(trim.mode())) {
            return trim(value);
        } else if (Trim.TrimMode.LEFT.equals(trim.mode())) {
            return trimLeft(value);
        } else if (Trim.TrimMode.RIGHT.equals(trim.mode())) {
            return trimRight(value);
        }
        return value;
    }

    public static String trimLeft(String value) {
        return StringUtils.trimLeadingWhitespace(value);
    }

    public static String trimRight(String value) {
        return StringUtils.trimTrailingWhitespace(value);
    }

    public static String trim(String value) {
        if (value != null) {
            return value.trim();
        }
        return null;
    }
}
