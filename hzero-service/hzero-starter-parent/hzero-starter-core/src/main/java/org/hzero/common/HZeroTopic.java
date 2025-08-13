package org.hzero.common;

/**
 * HZero kafka topic
 */
public final class HZeroTopic {

    /**
     * 平台服务消息
     */
    public static final class Platform {
        /**
         * 租户创建
         */
        public static final String TENANT_CREATED = "HPFM.TENANT.CREATED";
        /**
         * 租户更新
         */
        public static final String TENANT_UPDATED = "HPFM.TENANT.UPDATED";

    }

    /**
     * IAM服务消息
     */
    public static final class Iam {
        /**
         * 租户创建
         */
        public static final String TENANT_CREATED = "HIAM.TENANT.CREATED";
        /**
         * 租户更新
         */
        public static final String TENANT_UPDATED = "HIAM.TENANT.UPDATED";

    }


}
