package io.choerodon.mybatis.constant;

/**
 * @author superlee
 */
public enum DatabaseProductName {

    H2("H2"),

    SQL_SERVER("Microsoft SQL Server"),

    ORACLE("Oracle"),

    MYSQL("MySQL"),

    GAUSS("Zenith"),

    HDB("HDB"),

    POSTGRE("PostgreSQL");

    private final String value;

    DatabaseProductName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
