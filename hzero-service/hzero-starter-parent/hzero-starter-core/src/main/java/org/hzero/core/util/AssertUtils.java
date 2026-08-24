package org.hzero.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * Assertion utility class that assists in validating arguments.
 * </p>
 *
 * @author allen 2018/6/27
 */
public class AssertUtils extends Assert {
    /**
     * 替代标准Assert.notNull; CommonException可以使用多语言消息
     *
     * @param message    异常消息内容
     * @param parameters 异常参数
     */
    public static void notNull(Object object, String message, Object... parameters) {
        if (object == null) {
            throw new CommonException(message, parameters);
        }
    }

    /**
     * 如果字符串不匹配正则表达式, 则异常
     *
     * @param regex      正则表达式
     * @param str        目标字符串
     * @param message    异常消息内容
     * @param parameters 异常参数
     */
    public static void notMatch(String regex, String str, String message, Object... parameters) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            throw new CommonException(message, parameters);
        }
    }

    /**
     * 如果字符串长度不在min与max之间
     *
     * @param str        目标字符串
     * @param min        最小长度
     * @param max        最大长度
     * @param message    异常消息内容
     * @param parameters 异常参数
     */
    public static void notBetweenInSize(String str, int min, int max, String message, Object... parameters) {
        AssertUtils.notNull(str, message, parameters);
        if (str.length() < min || str.length() > max) {
            throw new CommonException(message, parameters);
        }
    }
}
