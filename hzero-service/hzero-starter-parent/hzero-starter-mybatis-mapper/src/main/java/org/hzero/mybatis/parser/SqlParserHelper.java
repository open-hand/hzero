package org.hzero.mybatis.parser;

import org.apache.commons.lang3.BooleanUtils;

/**
 * @author ThisO 2020/4/29 17:30
 */
public class SqlParserHelper {
    public static void open() {
        SqlParserInterceptor.sqlParserEnable.set(true);
    }

    public static void close() {
        SqlParserInterceptor.sqlParserEnable.set(false);
    }

    public static void clear() {
        SqlParserInterceptor.sqlParserEnable.remove();
    }

    public static boolean isOpen() {
        return BooleanUtils.isNotFalse(SqlParserInterceptor.sqlParserEnable.get());
    }

    public static boolean isClose() {
        return BooleanUtils.isFalse(SqlParserInterceptor.sqlParserEnable.get());
    }
}
