package org.hzero.boot.platform.lov.constant;


/**
 * 常量类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午6:55:22
 */
public class LovConstants {

    public static final String CONTENT_REGEX = "\\$\\{.*?\\}";
    
    /**
     * 错误消息
     */
    public static class ErrorMessage{
        
        /**
         * {xxx}不能为空
         */
        public static final String ERROR_NULL = "{%s} should not be null!";
        
        /**
         * 无法获得sql
         */
        public static final String ERROR_NO_SQL_GET = "can not get any sql by code {%s} and tenant id {%s}";
        
        /**
         * sql错误
         */
        public static final String ERROR_INVALIDE_SQL = "invalide sql!";
        
        /**
         * 翻译时找不到字段
         */
        public static final String ERROR_INVALIDE_MEANING_FIELD = "field not found {%s} in class {%s} when process lov value";
    }
    
    /**
     * 字段名
     */
    public static class Field{
        /**
         * 失败
         */
        public static final String FAIL = "fail";
        /**
         * 消息
         */
        public static final String MESSAGE = "message";
        /**
         * 租户ID
         */
        public static final String TENANT_ID = "tenantId";
        /**
         * 值集代码
         */
        public static final String LOV_CODE = "lovCode";
        /**
         * 唯一键
         */
        public static final String IDENTITY = "identity";
        /**
         * 唯一键csv
         */
        public static final String IDENTITIES = "identities";
        /**
         * 含义
         */
        public static final String MEANING = "meaning";
        /**
         * Page对象特殊字段--总页数
         */
        public static final String TOTAL_PAGES = "totalPages";
        /**
         * Page对象特殊字段--内容
         */
        public static final String CONTENT = "content";
    }
    
    /**
     * 值集类型
     */
    public static class LovTypes{
        /**
         * URL
         */
        public static final String URL = "URL";
        /**
         * 独立值集
         */
        public static final String IDP = "IDP";
        /**
         * 自定义SQL
         */
        public static final String SQL = "SQL";
    }
    
}
