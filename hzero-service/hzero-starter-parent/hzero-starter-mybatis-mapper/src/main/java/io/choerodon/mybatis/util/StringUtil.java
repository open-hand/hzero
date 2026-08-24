/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.mybatis.util;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.choerodon.mybatis.code.Style;

/**
 * Created by liuzh_3nofxnp on 2015/8/26.
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * 空
     *
     * @param str str
     * @return isEmpty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 非空
     *
     * @param str str
     * @return isNotEmpty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * 根据指定的样式进行转换
     *
     * @param str   str
     * @param style style
     * @return convertString
     */
    public static String convertByStyle(String str, Style style) {
        switch (style) {
            case CAMELHUMP:
                return camelhumpToUnderline(str);
            case UPPERCASE:
                return str.toUpperCase();
            case LOWERCASE:
                return str.toLowerCase();
            case CAMELHUMP_AND_LOWERCASE:
                return camelhumpToUnderline(str).toLowerCase();
            case CAMELHUMP_AND_UPPERCASE:
                return camelhumpToUnderline(str).toUpperCase();
            case NORMAL:
            default:
                return str;
        }
    }

    /**
     * 将驼峰风格替换为下划线风格
     * @param str str
     * @return String String
     */
    public static String camelhumpToUnderline(String str) {
        final char[] chars = str.toCharArray();
        final int size = chars.length;
        final StringBuilder sb = new StringBuilder(size * 3 / 2 + 1);
        char c;
        for (int i = 0; i < size; i++) {
            c = chars[i];
            if (isUppercaseAlpha(c)) {
                sb.append('_').append(toLowerAscii(c));
            } else {
                sb.append(c);
            }
        }
        return sb.charAt(0) == '_' ? sb.substring(1) : sb.toString();
    }

    /**
     * 将下划线风格替换为驼峰风格
     * @param str str
     * @return String string
     */
    private static final Pattern pattern = Pattern.compile("_[a-z]");
    public static String underlineToCamelhump(String str) {
        Matcher matcher = pattern.matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }

    public static boolean isUppercaseAlpha(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public static boolean isLowercaseAlpha(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    /**
     * {@inheritDoc}
     *
     * @param c char
     * @return upperAscii
     */
    public static char toUpperAscii(char c) {
        if (isLowercaseAlpha(c)) {
            c -= (char) 0x20;
        }
        return c;
    }

    /**
     * {@inheritDoc}
     *
     * @param c char
     * @return lowerAscii
     */
    public static char toLowerAscii(char c) {
        if (isUppercaseAlpha(c)) {
            c += (char) 0x20;
        }
        return c;
    }

    /**
     * {@inheritDoc}
     *
     * @param iterable  iterable
     * @param separator separator
     * @return join String
     */
    public static String join(Iterable<?> iterable, String separator) {
        StringBuilder builder = new StringBuilder();
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    /**
     * 判断下划线分割的表名是不是全大写，如果有一个小写字母，判定为小写
     * @param tableName
     * @return
     */
    public static boolean tableNameAllUpperCase(String tableName) {
        String s = tableName.replaceAll("_", "");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }
}
