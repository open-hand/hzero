package org.hzero.core.util;

import java.util.UUID;

/**
 * UUID 工具类
 *
 * @author bojiangzhou 2018/07/11
 */
public class UUIDUtils {

    private UUIDUtils() {
    }

    /**
     * UUID 去掉连接符
     *
     * @param tenantId 租户ID
     * @return 租户ID+UUID
     */
    public static String generateTenantUUID(Long tenantId) {
        return tenantId + generateUUID();
    }

    /**
     * @return 去掉连接符的UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }



}
