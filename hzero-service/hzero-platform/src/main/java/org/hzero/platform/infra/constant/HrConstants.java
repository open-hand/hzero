package org.hzero.platform.infra.constant;

/**
 * <p>
 * Hr 业务常量类
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 14:54
 */
public class HrConstants {
    private HrConstants() {
    }

    public static class LovCode {
        /**
         * 数据来源Lov code
         */
        public static final String DATA_SOURCE = "HPFM.DATA_SOURCE";
    }


    /**
     * 维度类型
     *
     * @author jian.zhang02@hand-china.com 2018年6月26日下午3:14:53
     */
    public static class DimensionType {
        /**
         * 平台级
         */
        public static final String DIMENSION_PLATFORM_TYPE = "platform";
        /**
         * 租户级
         */
        public static final String DIMENSION_TENANT_TYPE = "tenant";
        /**
         * 公司级
         */
        public static final String DIMENSION_COMPANY_TYPE = "company";
        /**
         * 用户级
         */
        public static final String DIMENSION_USER_TYPE = "user";
    }
}
