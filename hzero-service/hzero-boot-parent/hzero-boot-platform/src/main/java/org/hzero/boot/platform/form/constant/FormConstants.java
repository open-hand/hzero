package org.hzero.boot.platform.form.constant;

/**
 * 表单配置常量
 *
 * @author liufanghan 2019/11/20 11:01
 */
public class FormConstants {

    /**
     * 可更新
     */
    public static final Integer UPDATABLE = 1;
    public static final String LOV_VIEW_ITEM_CODE = "LOV_VIEW";
    public static final String LOV_ITEM_CODE = "LOV";
    public static final String PASSWORD_ITEM_CODE = "PASSWORD";

    /**
     * 错误消息
     */
    public static class ErrorMessage {

        /**
         * 值校验与正则表达式不匹配
         */
        public static final String ERROR_VALUE_MISMATCH = "value {%s} does not match the rule";

        /**
         * 字段不允许更新
         */
        public static final String ERROR_VALUE_MODIFY = "field {%s} is not allowed to update";

        /**
         * 无效的正则表达式
         */
        public static final String INVALID_REGULAR_EXPRESSION = "regular expression {%s} is invalid";

    }

}
