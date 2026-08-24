package org.hzero.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 驼峰法-划线互转
 *
 * @author cshaper
 * @version 1.0.0
 * @since 2015.07.04
 */
public class FieldNameUtils {
    private static final Pattern CAMEL_PATTERN = Pattern.compile("[A-Z]([a-z\\d]+)?");

    private static final Pattern UNDERLINE_TO_CAMEL_PATTERN = Pattern.compile("([A-Za-z\\d]+)(_)?");
    private static final Pattern CAMEL_TO_UNDERLINE_PATTERN = CAMEL_PATTERN;

    private static final Pattern MIDDLE_LINE_TO_CAMEL_PATTERN = Pattern.compile("([A-Za-z\\d]+)(-)?");
    private static final Pattern CAMEL_TO_MIDDLE_LINE_PATTERN = CAMEL_PATTERN;


    private FieldNameUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * 下划线转驼峰法
     *
     * @param line       源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        return toCamel(line, smallCamel, '_', UNDERLINE_TO_CAMEL_PATTERN);
    }

    /**
     * 驼峰法转下划线
     *
     * @param line      源字符串
     * @param upperCase 是否大写
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line, boolean upperCase) {
        return camelTo(line, upperCase, CAMEL_TO_UNDERLINE_PATTERN, '_');
    }

    /**
     * 中划线转驼峰法
     *
     * @param line       源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    public static String middleLine2Camel(String line, boolean smallCamel) {
        return toCamel(line, smallCamel, '-', MIDDLE_LINE_TO_CAMEL_PATTERN);
    }

    /**
     * 驼峰法转中划线
     *
     * @param line      源字符串
     * @param upperCase 是否大写
     * @return 转换后的字符串
     */
    public static String camel2MiddleLine(String line, boolean upperCase) {
        return camelTo(line, upperCase, CAMEL_TO_MIDDLE_LINE_PATTERN, '-');
    }

    /**
     * ?转驼峰法
     *
     * @param line       源字符串
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @param split      源字符串分割字符
     * @param pattern    源字符串匹配模式
     * @return 转换后的字符串
     */
    private static String toCamel(String line, boolean smallCamel, char split, Pattern pattern) {
        if (StringUtils.isEmpty(line)) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0))
                    : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf(split);
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转?
     *
     * @param line      源字符串
     * @param upperCase 是否大写
     * @param pattern   源字符串匹配模式
     * @param split     新字符串的分隔符
     * @return 转换后的字符串
     */
    private static String camelTo(String line, boolean upperCase, Pattern pattern, char split) {
        if (StringUtils.isEmpty(line)) {
            return StringUtils.EMPTY;
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(upperCase ? word.toUpperCase() : word.toLowerCase());
            sb.append(matcher.end() == line.length() ? StringUtils.EMPTY : split);
        }
        return sb.toString();
    }
}
