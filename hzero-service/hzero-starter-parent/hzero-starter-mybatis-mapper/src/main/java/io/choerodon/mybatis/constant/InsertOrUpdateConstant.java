package io.choerodon.mybatis.constant;

/**
 * 自动生成更新和插入sql时使用的常量
 *
 * @author superleader8@gmail.com
 */
public class InsertOrUpdateConstant {
    private InsertOrUpdateConstant() {}

    public static final String CREATION_DATE = "creationDate";
    public static final String CREATED_BY = "createdBy";
    public static final String LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String LAST_UPDATE_BY = "lastUpdatedBy";
    public static final String OBJECT_VERSION_NUMBER = "objectVersionNumber";
    public static final String AUDIT_NOW = "#{audit.now, jdbcType = TIMESTAMP},";
    public static final String AUDIT_USER = "#{audit.user,jdbcType = BIGINT},";
    public static final String MODIFY_EXCEPTION = "未知的Modify列";
    public static final String VERSION_EXCEPTION = "未知的Version列";

    public static final String IDENTITY_VAL_LOCAL = "VALUES IDENTITY_VAL_LOCAL()";
}
