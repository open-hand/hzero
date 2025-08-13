package org.hzero.imported.infra.constant;

/**
 * @author shuangfei.zhu@hand-china.com
 */
public class HimpConstants {
    private HimpConstants() {
    }

    /**
     * 模板列类型值集
     */
    public static final class ColumnType {
        private ColumnType() {
        }

        /**
         * 值集编码
         */
        public static final String CODE = "HIMP.TEMPLATE.COLUMNTYPE";

        /**
         * 字符串
         */
        public static final String STRING = "String";
        /**
         * 整数
         */
        public static final String LONG = "Long";
        /**
         * 浮点数
         */
        public static final String DECIMAL = "Decimal";
        /**
         * 日期
         */
        public static final String DATE = "Date";
        /**
         * 序列
         */
        public static final String SEQUENCE = "Sequence";
    }

    /**
     * 模板类型值集
     */
    public static final class TemplateType {
        private TemplateType() {
        }

        /**
         * 值集编码
         */
        public static final String CODE = "HIMP.TEMPLATE.TEMPLATETYPE";
        /**
         * 服务端
         */
        public static final String SERVER = "S";
        /**
         * 客户端
         */
        public static final String CLIENT = "C";
    }

    public static final class Export {
        private Export() {
        }

        /**
         * 数据提示起始行
         */
        public static final Integer FIRST_ROW = 2;
        /**
         * 数据提示终止行
         * excel2007最大行号1048576
         */
        public static final Integer END_ROW = 1048575;
    }

}
