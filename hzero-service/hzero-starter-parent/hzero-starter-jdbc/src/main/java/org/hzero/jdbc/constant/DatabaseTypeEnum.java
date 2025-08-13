package org.hzero.jdbc.constant;

/**
 * 数据库类型
 *
 * @author xianzhi.chen@hand-china.com 2018年10月23日下午2:47:03
 */
public enum DatabaseTypeEnum {
    /**
     * MySQL
     */
    MYSQL(QueryConstants.Datasource.DB_MYSQL),

    /**
     * SqlServer
     */
    SQLSERVER(QueryConstants.Datasource.DB_MSSQL),

    /**
     * Oracle
     */
    ORACLE(QueryConstants.Datasource.DB_ORACLE),

    /**
     * TiDB
     */
    TIDB(QueryConstants.Datasource.DB_TIDB),

    /**
     * Hive
     */
    HIVE(QueryConstants.Datasource.DB_HIVE);

    private final String value;

    DatabaseTypeEnum(final String value) {
        this.value = value;
    }

    public static DatabaseTypeEnum of(String arg) {
        return valueOf2(arg);
    }

    public static DatabaseTypeEnum valueOf2(String arg) {
        switch (arg.toUpperCase()) {
            case "SQLSERVER":
                return SQLSERVER;
            case "ORACLE":
                return ORACLE;
            case "HIVE":
                return HIVE;
            case "TIDB":
            case "MYSQL":
            default:
                return MYSQL;
        }
    }

    public static boolean isInEnum(String value) {
        for (DatabaseTypeEnum databaseType : DatabaseTypeEnum.values()) {
            if (databaseType.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.value;
    }
}
