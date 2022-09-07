package org.hzero.oauth.security.exception;

/**
 * @author bojiangzhou
 */
public enum LoginExceptions {
    /**
     * 您输入的登录账户不存在
     */
    USERNAME_NOT_FOUND("hoth.warn.usernameNotFound"),
    /**
     * 您输入的手机号未注册
     */
    PHONE_NOT_FOUND("hoth.warn.phoneNotFound"),
    /**
     * 您的账号未激活，无法登录
     */
    USER_NOT_ACTIVATED("hoth.warn.userNotActive"),
    /**
     * 您的账户已锁定，请通过找回密码解锁，或联系平台运维中心
     */
    ACCOUNT_LOCKED("hoth.warn.accountLocked"),
    /**
     * 您的账户已过期，无法登录
     */
    ACCOUNT_EXPIRE("hoth.warn.accountExpire"),
    /**
     * 您的所属组织无效，请联系管理员
     */
    TENANT_INVALID("hoth.warn.tenantInvalid"),
    /**
     * 您的所属组织已被禁用，请联系管理员
     */
    TENANT_DISABLED("hoth.warn.tenantDisabled"),
    /**
     * 您的密码错误，还可以尝试{0}次
     */
    PASSWORD_ERROR("hoth.warn.passwordError"),
    /**
     * 您输入的账号或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR("hoth.warn.usernameNotFoundOrPasswordIsWrong"),
    /**
     * 密码错误次数超过限制，您的账户已锁定！请通过找回密码解锁，或联系平台运维中心！
     */
    LOGIN_ERROR_MORE_THEN_MAX_TIME("hoth.warn.loginErrorMaxTimes"),
    /**
     * LDAP已被禁用
     */
    LDAP_IS_DISABLE("hoth.warn.ldapIsDisable"),
    /**
     * 请输入验证码
     */
    CAPTCHA_NULL("hoth.warn.captchaNull"),
    /**
     * 您输入的验证码有误
     */
    CAPTCHA_ERROR("hoth.warn.captchaWrong"),
    /**
     * 您的手机未验证，可使用邮箱或账号登录
     */
    PHONE_NOT_CHECK("hoth.warn.phoneNotCheck"),
    /**
     * 您的邮箱未验证，可使用手机或账号登录
     */
    EMAIL_NOT_CHECK("hoth.warn.emailNotCheck"),
    /**
     * 您的手机和邮箱均未验证，可使用账号登录
     */
    PHONE_AND_EMAIL_NOT_CHECK("hoth.warn.phoneAndEmailNotCheck"),
    /**
     * 您的默认租户下没有分配角色，请联系管理员分配角色
     */
    DEFAULT_TENANT_ROLE_NONE("hoth.warn.defaultTenantRoleNull"),
    /**
     * 请输入手机验证码
     */
    LOGIN_MOBILE_CAPTCHA_NULL("hoth.warn.loginMobileCaptchaNull"),
    /**
     * 您没有任何角色，请联系管理员分配角色
     */
    ROLE_NONE("hoth.warn.roleNone"),
    /**
     * 解码密码错误
     */
    DECODE_PASSWORD_ERROR("hoth.warn.decodePasswordError"),
    /**
     * 解码密码错误
     */
    DECODE_ACCOUNT_ERROR("hoth.warn.decodeAccountError"),
    /**
     * 您的密码已过期，请修改密码后再登录系统
     */
    PASSWORD_EXPIRED("hoth.warn.passwordExpired"),
    /**
     * 为了您的账号安全，请先修改初始密码后再登录
     */
    PASSWORD_FORCE_MODIFY("hoth.warn.passwordForceModify"),
    /**
     * 当前用户拥有角色暂无权限访问此客户端
     */
    USER_NOT_ACCESS_CLIENT("hoth.warn.userNotAccessClient"),
    /**
     * 未找到客户端！
     */
    CLIENT_NOT_FOUND("hoth.warn.clientNotFound"),
    /**
     * 您输入的密码错误！
     */
    DUPLICATE_PASSWORD("hoth.warn.duplicatePassword"),
    /**
     * 为了您的账号安全，请进行二次校验
     */
    SECONDARY_CHECK("hoth.warn.secondaryCheck"),
    /**
     * 当前账户手机号码二次校验未开启
     */
    PHONE_SECONDARY_CHECK_NOT_OPEN("hoth.warn.secondaryCheck.phone.not.open"),
    /**
     * 当前账户邮箱二次校验未开启
     */
    EMAIL_SECONDARY_CHECK_NOT_OPEN("hoth.warn.secondaryCheck.email.not.open"),
    /**
     * 不支持的二次校验类型: {0}
     */
    UNSUPPORTED_SECONDARY_CHECK_TYPE("hoth.warn.secondaryCheck.unsupportedType"),
    /**
     * 当前用户绑定的手机号与输入的待认证的手机号不同
     */
    PHONE_BINDING_AND_INPUT_DIFF("hoth.warn.phone.bindingAndInput.diff"),
    /**
     * 当前用户绑定的邮箱与输入的待认证的邮箱不同
     */
    EMAIL_BINDING_AND_INPUT_DIFF("hoth.warn.email.bindingAndInput.diff");

    private final String value;

    LoginExceptions(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
