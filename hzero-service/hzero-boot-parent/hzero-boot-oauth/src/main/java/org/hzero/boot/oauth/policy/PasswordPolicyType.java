package org.hzero.boot.oauth.policy;

/**
 * 密码策略验证的类型
 *
 * @author wuguokai
 */
public enum PasswordPolicyType {

    MIN_LENGTH("minLength"),
    MAX_LENGTH("maxLength"),
    DIGITS_COUNT("digitsCount"),
    LOWERCASE_COUNT("lowercaseCount"),
    UPPERCASE_COUNT("uppercaseCount"),
    SPECIALCHAR_COUNT("specialCharCount"),
    NOT_USERNAME("notUsername"),
    NOT_RECENT("notRecent"),
    REGULAR("regular"),
    MAX_ERROR_TIME("maxErrorTime"),
    ENABLE_CAPTCHA("enableCaptcha"),
    MAX_CHECK_CAPTCHA("maxCheckCaptcha"),
    ENABLE_LOCK("enableLock"),
    LOCK_EXPIRE_TIME("lockExpireTime");

    private String value;

    PasswordPolicyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
