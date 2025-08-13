package org.hzero.iam.infra.common.utils;

import io.choerodon.core.exception.CommonException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/6/27
 */
public class AssertUtils {
    /**
     * <p>
     *     替代标准Assert.notNull; CommonException可以使用多语言消息
     * </p>
     * @param t
     * @param message
     * @param parameters
     * @param <T>
     */
    public static <T> void notNull(T t, String message, Object... parameters) {
        if (t == null) {
            throw new CommonException(message, parameters);
        }
    }

    /**
     * 如果字符串不匹配正则表达式, 则异常
     * @param regex 正则表达式
     * @param str 目标字符串
     * @param message
     * @param parameters
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
     * @param str
     * @param min
     * @param max
     * @param message
     * @param parameters
     */
    public static void notBetweenInSize(String str, int min, int max, String message, Object... parameters) {
        AssertUtils.notNull(str, message, parameters);

        if (str.length() < min || str.length() > max) {
            throw new CommonException(message, parameters);
        }
    }
}
