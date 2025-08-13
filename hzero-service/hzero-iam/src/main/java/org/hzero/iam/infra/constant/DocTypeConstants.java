package org.hzero.iam.infra.constant;

/**
 * 单据类型常量
 *
 * @author min.wang01@hand-china.com 2018/08/09 15:41
 */
public class DocTypeConstants {

    public static class ErrorCode {
        /**
         * 单据类型重复
         */
        public static final String DOC_TYPE_REPEAT = "doc.type.repeat";

        /**
         * 单据类型不存在
         */
        public static final String DOC_TYPE_NOT_EXISTS = "doc.type.not.exists";
    }

    /**
     * 单据类型层级编码
     */
    public static class DocTypeLevelCode {
        /**
         * 平台级
         */
        public static final String GLOBAL = "GLOBAL";
        /**
         * 租户级
         */
        public static final String TENANT = "TENANT";
    }
}
