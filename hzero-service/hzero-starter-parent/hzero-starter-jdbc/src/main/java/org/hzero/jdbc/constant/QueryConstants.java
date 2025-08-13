package org.hzero.jdbc.constant;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class QueryConstants {
    private QueryConstants() {
    }

    public static class Datasource {
        private Datasource() {
        }
        /**
         * 数据库类型-Oracle
         */
        public static final String DB_ORACLE = "Oracle";
        /**
         * 数据库类型-Mysql
         */
        public static final String DB_MYSQL = "MySql";
        /**
         * 数据库类型-Tidb
         */
        public static final String DB_TIDB = "TiDB";
        /**
         * 数据库类型-SqlServer
         */
        public static final String DB_MSSQL = "SqlServer";
        /**
         * 数据库类型-Hana
         */
        public static final String DB_HANA = "Hana";
        /**
         * 数据库类型-Hive
         */
        public static final String DB_HIVE = "Hive";

        /**
         * 查询器模板
         */
        public static final String QUERYER_TEMPLATE = "org.hzero.jdbc.query.%sQueryer";
        /**
         * 连接池模板
         */
        public static final String DBPOOL_TEMPLATE = "org.hzero.jdbc.dbpool.%sDataSourcePool";
    }

    public static class DataXmlAttr {
        private DataXmlAttr() {
        }
        /**
         * 默认数据源
         */
        public static final String DEFAULT_DS = "DATA";
    }

    public static class Error {
        private Error() {
        }
        /**
         * 查询参数设置有错误,可能未设置SQL语句或SQL语句不正确，或不正确的参数设置:{0}
         */
        public static final String ERROR_PARAMETER_SETTING = "error.parameter.setting";
        /**
         * 未指定报表查询器对象!
         */
        public static final String ERROR_QUERYER_NOT_FOUND = "error.queryer.not_found";
        /**
         * {0}: 数据源连接池创建错误
         */
        public static final String ERROR_DATASOURCE_POOL_CREATE = "error.datasource.pool.create";
        /**
         * 数据源连接池类加载错误
         */
        public static final String ERROR_POOL_FACTORY_LOAD_CLASS = "error.poolFactory.LoadClass";
        /**
         * 数据库资源释放异常
         */
        public static final String ERROR_RELEASE_JDBC_RESOURCE = "error.release.jdbc.resource";
    }
}
