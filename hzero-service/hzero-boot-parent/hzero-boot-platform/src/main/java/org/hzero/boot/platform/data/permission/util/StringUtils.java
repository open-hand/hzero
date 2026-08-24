package org.hzero.boot.platform.data.permission.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 字符串工具类
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/08/01 16:03
 */
public class StringUtils {

    private StringUtils() {

    }

    /**
     * 正则匹配
     */
    private static final Pattern PATTERN = Pattern.compile("#\\{(.+?)}");
    private static final String START_WITH = "#{";
    private static final String END_WITH = "}";
    private static final String COUNT = "_COUNT";

    /**
     * 获取字符串中被#{}包围的字符串
     *
     * @param str
     * @return stringList
     */
    public static List<String> getFieldList(String str) {
        if (str == null || "".equals(str.trim())) {
            return Collections.emptyList();
        }
        Matcher matcher = PATTERN.matcher(str);
        List<String> fieldList = new ArrayList<>();
        while (matcher.find()) {
            fieldList.add(matcher.group());
        }
        return fieldList;
    }

    /**
     * 截取获得#{}中的值
     *
     * @param str
     * @return 截取值
     */
    public static String getField(String str) {
        if (str != null && str.startsWith(START_WITH) && str.endsWith(END_WITH)) {
            return str.substring(START_WITH.length(), str.length() - END_WITH.length());
        }
        return str;
    }


    /**
     * 处理值
     *
     * @param object object
     * @return string
     */
    static String generateValueString(Object object) {
        if (object instanceof Long) {
            return object.toString();
        } else if (object instanceof Integer) {
            return object.toString();
        } else if (object instanceof Collection) {
            return org.springframework.util.StringUtils.collectionToCommaDelimitedString((Collection<?>) object);
        } else {
            return "'" + object.toString() + "'";
        }
    }

    /**
     * 处理分页插件自动生成的count语句的sqlId,让count语句也被数据屏蔽
     *
     * @param sqlId sqlId
     * @return sqlId
     */
    public static String handleCountSqlId(String sqlId) {
        if (sqlId != null && sqlId.endsWith(COUNT)) {
            return sqlId.substring(0, sqlId.length() - COUNT.length());
        }
        return sqlId;
    }

    /**
     * 判断是否是count语句
     *
     * @param sqlId
     * @return boolean
     */
    public static boolean isCountSql(String sqlId) {
        if (sqlId != null) {
            return sqlId.endsWith(COUNT);
        }
        return false;
    }

    /**
     * 判断字符串不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(str);
    }
}
