package org.hzero.iam.domain.service.ldap;

/**
 * ldap同步用户失败原因
 */
public enum LdapErrorUserCause {

    /**
     * 登录名属性字段为空
     */
    LOGIN_NAME_FIELD_NULL("login_name_field_null"),

    /**
     * 邮箱属性字段为空
     */
    EMAIL_FIELD_NULL("email_field_null"),

    /**
     * 登录名字段，attribute get()方法抛异常
     */
    LOGIN_NAME_GET_EXCEPTION("login_name_get_exception"),

    /**
     * 邮箱字段，attribute get()方法抛异常
     */
    EMAIL_GET_EXCEPTION("email_get_exception"),

    /**
     * 用户插入异常
     */
    USER_INSERT_ERROR("user_insert_error"),

    /**
     * 用户更新异常
     */
    USER_UPDATE_ERROR("user_update_error"),

    /**
     * 邮箱已经存在
     */
    EMAIL_ALREADY_EXISTED("email_already_existed"),

    /**
     * 邮箱已经存在
     */
    PHONE_ALREADY_EXISTED("phone_already_existed"),

    /**
     * 邮箱或手机已经存在
     */
    EMAIL_OR_PHONE_ALREADY_EXISTED("email_or_phone_already_existed"),

    /**
     * 系统中存在同名的非Ldap用户
     */
    EXISTS_NON_LDAP_SAME_LOGIN_NAME("exists_non_ldap_same_login_name");

    private String value;

    LdapErrorUserCause(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
