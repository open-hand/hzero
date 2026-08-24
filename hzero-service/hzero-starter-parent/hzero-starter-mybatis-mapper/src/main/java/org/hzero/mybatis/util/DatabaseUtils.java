package org.hzero.mybatis.util;

import io.choerodon.mybatis.code.DbType;

/**
 * @author bojiangzhou 2020/08/24
 */
public class DatabaseUtils {

    private static DbType dbType;

    public static String getDatabaseType() {
        return dbType.getValue();
    }

    public static boolean isDatabase(DbType type) {
        return type.getValue().equals(dbType.getValue());
    }

    /**
     * 获取数据库批量插入最大的批次数
     *
     * @param paramSize 单条记录参数个数
     * @return 最大批次数
     */
    public static int maxBatchSize(int paramSize) {
        int maxSize;
        if (DbType.MYSQL.equals(dbType)) {
            maxSize = 2000;
        } else if (DbType.ORACLE.equals(dbType)) {
            maxSize = 999;
        } else if (DbType.SQLSERVER.equals(dbType)) {
            maxSize = 2090 / paramSize;
        } else {
            maxSize = 999;
        }
        return maxSize;
    }

}
