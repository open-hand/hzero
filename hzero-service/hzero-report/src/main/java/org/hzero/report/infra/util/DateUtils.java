package org.hzero.report.infra.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hzero.core.base.BaseConstants;

/**
 * 日期工具类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:59:55
 */
public class DateUtils {

    private DateUtils() {
    }

    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>(BaseConstants.Digital.SIXTEEN);

    private static final int PATTERN_CACHE_SIZE = 500;

    /**
     * 获取当前时间的yyyy-MM-dd格式字符串
     *
     * @return 当前时间的yyyy-MM-dd格式字符串
     */
    public static String getNow() {
        return getNow(BaseConstants.Pattern.NONE_DATETIME);
    }

    /**
     * 获取当前时间指定格式字符串
     *
     * @param pattern 日期时间格式(如:yyyy-MM-dd,MM-dd-yyyy等)
     * @return 当前时间指定格式字符串
     */
    private static String getNow(String pattern) {
        LocalDateTime ldt = LocalDateTime.now();
        return ldt.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Date转换为格式化时间
     *
     * @param date    date
     * @param pattern 格式
     * @return 时间
     */
    public static String format(Date date, String pattern) {
        return format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), pattern);
    }

    /**
     * localDateTime转换为格式化时间
     *
     * @param localDateTime localDateTime
     * @param pattern       格式
     * @return 时间
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * 格式化字符串转为Date
     *
     * @param time    格式化时间
     * @param pattern 格式
     * @return 时间
     */
    public static Date parseDate(String time, String pattern) {
        return Date.from(parseLocalDateTime(time, pattern).atZone(ZoneId.systemDefault()).toInstant());

    }

    /**
     * 格式化字符串转为LocalDateTime
     *
     * @param time    格式化时间
     * @param pattern 格式
     * @return 时间
     */
    private static LocalDateTime parseLocalDateTime(String time, String pattern) {
        DateTimeFormatter formatter = createCacheFormatter(pattern);
        return LocalDateTime.parse(time, formatter);
    }

    /**
     * 在缓存中创建DateTimeFormatter
     *
     * @param pattern 格式
     * @return 时间
     */
    private static DateTimeFormatter createCacheFormatter(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
        DateTimeFormatter formatter = FORMATTER_CACHE.get(pattern);
        if (formatter == null && FORMATTER_CACHE.size() < PATTERN_CACHE_SIZE) {
            formatter = DateTimeFormatter.ofPattern(pattern);
            DateTimeFormatter oldFormatter = FORMATTER_CACHE.putIfAbsent(pattern, formatter);
            if (oldFormatter != null) {
                formatter = oldFormatter;
            }
        }
        return formatter;
    }

}
