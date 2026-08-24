package org.hzero.boot.platform.data.permission.helper;

import java.util.Map;

/**
 * <p>
 * 数据权限方法封装类，用于保存代码中讲到替换的sql
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/08/14 10:26
 */
public abstract class PermissionDataMethod {

    protected PermissionDataMethod() {

    }

    protected static final ThreadLocal<Map<String, String>> LOCAL_TABLE = new ThreadLocal<>();

    /**
     * 进行数据屏蔽拦截
     *
     * @param map key为表名，value为需要注入的sql
     */
    public static void startPermission(Map<String, String> map) {
        if (map != null) {
            LOCAL_TABLE.set(map);
        }
    }

    /**
     * 根据表名得到被注入的sql值
     *
     * @param tableName 表名
     * @return sql
     */
    public static String getTableSql(String tableName) {
        if (LOCAL_TABLE.get() != null) {
            return LOCAL_TABLE.get().get(tableName.trim());
        }
        return null;
    }

    /**
     * 删掉sql值
     *
     * @param tableName
     */
    public static void removeTableSql(String tableName) {
        if (LOCAL_TABLE.get() != null) {
            LOCAL_TABLE.get().remove(tableName);
        }
    }
}
