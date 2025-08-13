package org.hzero.boot.platform.data.permission.constant;

import org.hzero.common.HZeroService;

/**
 * <p>
 * 数据屏蔽常量
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 11:50
 */
public interface PermissionConstants {

    /**
     * 服务简称
     */
    String APP_CODE = HZeroService.Platform.CODE;
    
    interface CacheKey {
        /**
         * 数据屏蔽
         */
        String PERMISSION_KEY = PermissionConstants.APP_CODE + ":permission";

        /**
         * 数据权限数据库关系
         */
        String DATABASE_KEY = PermissionConstants.APP_CODE + ":database";
    }
}
