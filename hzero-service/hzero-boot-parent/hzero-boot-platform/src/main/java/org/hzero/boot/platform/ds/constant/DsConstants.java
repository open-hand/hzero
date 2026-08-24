package org.hzero.boot.platform.ds.constant;

/**
 * 
 * 数据源组件常量类
 * 
 * @author xianzhi.chen@hand-china.com 2019年1月21日下午8:29:04
 */
public class DsConstants {

    /**
     * 
     * 数据源用途常量类
     * 
     * @author xianzhi.chen@hand-china.com 2019年1月21日下午8:29:00
     */
    public static class DsPurpose {
        /**
         * 数据分发
         */
        public static final String DT = "DT";
        /**
         * 数据报表
         */
        public static final String DR = "DR";
        /**
         * 数据导入
         */
        public static final String DI = "DI";
        /**
         * error
         */
        public static final String ERROR = "error";
    }

    /**
     * 异常编码
     */
    public static class ErrorCode{
        public static final String DATASOURCE_SERVICE_NAME_NOT_NULL = "hpfm.error.datasource.service_name.notNull";
    }



    /**
     * 数据源缓存Key
     */
    public static final String DATASOURCE = ":datasource:";

}
