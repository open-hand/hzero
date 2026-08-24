package io.choerodon.mybatis.pagehelper.dialect;

import io.choerodon.mybatis.pagehelper.Dialect;

/**
 * Created by xausky on 3/23/17.
 */
public class DialectHelper {

    private static Dialect dialect;

    private DialectHelper() {
    }

    public static Dialect getDialect() {
        return dialect;
    }

    public static void setDialect(Dialect dialect) {
        DialectHelper.dialect = dialect;
    }
}
