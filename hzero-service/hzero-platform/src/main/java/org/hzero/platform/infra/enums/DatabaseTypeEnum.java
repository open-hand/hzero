package org.hzero.platform.infra.enums;

import org.hzero.platform.infra.constant.Constants;

/**
 * 
 * 数据库类型
 * 
 * @author xianzhi.chen@hand-china.com 2018年10月23日下午2:47:03
 */
public enum DatabaseTypeEnum {
    /**
     * MySQL
     */
    MYSQL("MYSQL"),

    /**
     * SqlServer
     */
    SQLSERVER("SQLSERVER"),

    /**
     * Oracle
     */
    ORACLE("ORACLE"),

    /**
     * TiDB
     */
    TIDB("TIDB"),

    /**
     * OtherDB
     */
    OTHERDB("OTHERDB");

    private final String value;

    DatabaseTypeEnum(final String value) {
        this.value = value;
    }

    public static DatabaseTypeEnum valueOf2(String arg) {
        switch (arg) {
            case Constants.Datasource.DB_MYSQL:
                return MYSQL;
            case Constants.Datasource.DB_MSSQL:
                return SQLSERVER;
            case Constants.Datasource.DB_ORACLE:
                return ORACLE;
            case Constants.Datasource.DB_TIDB:
                return MYSQL;
            default:
                return OTHERDB;
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
