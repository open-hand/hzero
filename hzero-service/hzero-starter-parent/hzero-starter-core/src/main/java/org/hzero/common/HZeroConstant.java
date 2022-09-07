package org.hzero.common;


import org.hzero.core.base.BaseConstants;

/**
 * HZero 全局常量
 */
public class HZeroConstant {

    /**
     * 管理员账户
     */
    public static final String ADMIN = "admin";

    /**
     * 默认全局层租户ID
     */
    public static final Long SITE_TENANT_ID = BaseConstants.DEFAULT_TENANT_ID;

    /**
     * 角色
     */
    public static class RoleCode {
        /**
         * 全局层角色编码
         */
        public static final String SITE = "role/site/default/administrator";
        /**
         * 租户层角色编码
         */
        public static final String TENANT = "role/organization/default/administrator";
        /**
         * 小项目层角色编码
         */
        public static final String PROJECT = "role/project/default/administrator";
        /**
         * 访客角色编码
         */
        public static final String GUEST = "role/site/default/guest";

        /**
         * 租户层角色模板
         */
        public static final String TENANT_TEMPLATE = "role/organization/default/template/administrator";
    }

    /**
     * 值集
     */
    public static class Lov {

        /**
         * 缓存相关常量
         */
        public static class Cache {

            /**
             * 缓存失效时间,单位--秒
             */
            public static final long EXPIRE = 30 * 86400L;

            /**
             * 头表之外的数据的额外失效时间.避免,单位--秒
             */
            public static final long EXPIRE_DELTA = 600;


        }

        /**
         * API地址
         */
        public static class ApiAddress {
            /**
             * 查询值集SQL
             */
            public static final String LOV_SQL_SERVICE_ADDRESS = "/v1/lovs/sql";
            /**
             * 查询值集反查SQL
             */
            public static final String LOV_TRANS_SQL_SERVICE_ADDRESS = "/v1/lovs/translation-sql";
            /**
             * 查询值集值
             */
            public static final String LOV_VALUE_SERVICE_ADDRESS = "/v1/lovs/value";
            /**
             * 查询全局SQL值集数据
             */
            public static final String SQL_LOV_SERVICE_ADDRESS = "/v1/lovs/sql/data";
            /**
             * 查询租户级SQL值集数据
             */
            public static final String SQL_ORG_LOV_SERVICE_ADDRESS = "/v1/{organizationId}/lovs/sql/data";

        }

        /**
         * 值集数据集成查询默认页码
         */
        public static final String LOV_DATA_DEFAULT_PAGE = BaseConstants.PAGE;
        /**
         * 值集数据集成查询默认页面大小
         */
        public static final String LOV_DATA_DEFAULT_SIZE = "100";
        /**
         * 值集数据集成查询最大页面大小
         */
        public static final String LOV_DATA_MAX_SIZE = "100000000";

    }

    /**
     * 系统配置常量
     */
    public static class Config {

        /**
         * 标题
         */
        public static final String CONFIG_CODE_TITLE = "TITLE";

        /**
         * LOGO
         */
        public static final String CONFIG_CODE_LOGO = "LOGO";
    }
}
