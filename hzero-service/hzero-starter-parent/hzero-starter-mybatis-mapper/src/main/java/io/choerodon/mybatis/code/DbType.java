package io.choerodon.mybatis.code;

import io.choerodon.mybatis.constant.CommonMapperConfigConstant;

/**
 * @author qixiangyu18@163.com on 2018/8/7.
 */
public enum DbType {

    MYSQL("mysql", true, false, CommonMapperConfigConstant.IDENTITY_JDBC),
    ORACLE("oracle", false, true, CommonMapperConfigConstant.IDENTITY_SEQUENCE),
    HANA("hana", false, true, CommonMapperConfigConstant.IDENTITY_SEQUENCE),
    H2("h2", true, false, CommonMapperConfigConstant.IDENTITY_JDBC),
    //MySQL和SQLServer执行auto-generated key field，因此当数据库设置好自增长主键后，可通过JDBC的getGeneratedKeys方法获取
    SQLSERVER("sqlserver", true, false, CommonMapperConfigConstant.IDENTITY_JDBC),
    POSTGRES("postgres", false, true, CommonMapperConfigConstant.IDENTITY_SEQUENCE),
    GAUSS("gauss", false, true, CommonMapperConfigConstant.IDENTITY_SEQUENCE);

    private boolean supportAutoIncrement;

    private boolean supportSequence;

    private String identity;

    private String value;

    DbType(String value, boolean supportAutoIncrement, boolean supportSequence, String identity) {
        this.value = value;
        this.supportAutoIncrement = supportAutoIncrement;
        this.supportSequence = supportSequence;
        this.identity = identity;
    }

    public String getValue() {
        return value;
    }

    public boolean isSupportAutoIncrement() {
        return supportAutoIncrement;
    }

    public boolean isSupportSequence() {
        return supportSequence;
    }

    public String getIdentity() {
        return identity;
    }

    public static DbType getByValue(String value) {
        for (DbType dbType : values()) {
            if (dbType.getValue().equalsIgnoreCase(value)) {
                return dbType;
            }
        }
        return null;
    }
}
