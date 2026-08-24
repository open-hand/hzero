package org.hzero.boot.platform.data.permission.util;

import org.hzero.boot.platform.data.permission.constant.PermissionConstants;

/**
 * <p>
 * 生成redis缓存key的工具类
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 11:48
 */
public class KeyUtils {

    private KeyUtils() {

    }

    /**
     * 生成数据屏蔽cacheKey
     *
     * @param tableName 表名
     * @return key
     */
    public static String generateCacheKey(String tableName) {
        return PermissionConstants.CacheKey.PERMISSION_KEY + ":" + tableName.toLowerCase().trim();
    }

    /**
     * 生成数据屏蔽redis哈希map中的key
     *
     * @param tenantId    tenantId
     * @param serviceName 服务名
     * @param sqlId       sqlId
     * @return mapKey
     */
    public static String generateMapKey(Long tenantId, String serviceName, String sqlId) {
        if (serviceName != null && !"".equals(serviceName.trim())) {
            serviceName = "." + serviceName;
        }
        if (sqlId != null && !"".equals(sqlId.trim())) {
            sqlId = "." + sqlId;
        }
        return tenantId + (serviceName == null ? "" : serviceName) + (sqlId == null ? "" : sqlId);
    }

    /**
     * 产生数据库动态前缀缓存key
     *
     * @param tenantId 租户id
     * @return key
     */
    public static String generatePrefixCacheKey(Long tenantId, String serviceName) {
        return PermissionConstants.CacheKey.DATABASE_KEY + ":" + tenantId + "." + serviceName;
    }
}
