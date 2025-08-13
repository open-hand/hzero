package org.hzero.boot.oauth.policy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;

/**
 * @author wuguokai
 */
public class PasswordPolicyMap {
    private Map<String, Object> passwordConfig;
    private Map<String, Object> loginConfig;

    private Boolean enablePassword;
    private Boolean enableSecurity;

    public PasswordPolicyMap(Map<String, Object> passwordConfig, Map<String, Object> loginConfig, Boolean enablePassword, Boolean enableSecurity) {
        this.passwordConfig = passwordConfig;
        this.loginConfig = loginConfig;
        this.enablePassword = enablePassword;
        this.enableSecurity = enableSecurity;
    }

    public Map<String, Object> getPasswordConfig() {
        return passwordConfig;
    }

    public Map<String, Object> getLoginConfig() {
        return loginConfig;
    }

    public Set<String> getPasswordPolicies() {
        return passwordConfig.keySet();
    }

    public Set<String> getLoginPolicies() {
        return loginConfig.keySet();
    }

    public Boolean isEnablePassword() {
        return enablePassword;
    }

    public Boolean isEnableSecurity() {
        return enableSecurity;
    }

    public static PasswordPolicyMap parse(BasePasswordPolicy policy) {
        boolean enablePassword = policy.getEnablePassword();
        boolean enableSecurity = policy.getEnableSecurity();
        Map<String, Object> passwordMap = new LinkedHashMap<>();
        Map<String, Object> loginMap = new LinkedHashMap<>();
        passwordMap.put(PasswordPolicyType.MIN_LENGTH.getValue(), policy.getMinLength());
        passwordMap.put(PasswordPolicyType.MAX_LENGTH.getValue(), policy.getMaxLength());
        passwordMap.put(PasswordPolicyType.DIGITS_COUNT.getValue(), policy.getDigitsCount());
        passwordMap.put(PasswordPolicyType.LOWERCASE_COUNT.getValue(), policy.getLowercaseCount());
        passwordMap.put(PasswordPolicyType.UPPERCASE_COUNT.getValue(), policy.getUppercaseCount());
        passwordMap.put(PasswordPolicyType.SPECIALCHAR_COUNT.getValue(), policy.getSpecialCharCount());
        passwordMap.put(PasswordPolicyType.NOT_USERNAME.getValue(), policy.getNotUsername());
        passwordMap.put(PasswordPolicyType.NOT_RECENT.getValue(), policy.getNotRecentCount());
        passwordMap.put(PasswordPolicyType.REGULAR.getValue(), policy.getRegularExpression());

        loginMap.put(PasswordPolicyType.MAX_ERROR_TIME.getValue(), policy.getMaxErrorTime());
        loginMap.put(PasswordPolicyType.ENABLE_CAPTCHA.getValue(), policy.getEnableCaptcha());
        loginMap.put(PasswordPolicyType.MAX_CHECK_CAPTCHA.getValue(), policy.getMaxCheckCaptcha());
        loginMap.put(PasswordPolicyType.ENABLE_LOCK.getValue(), policy.getEnableLock());
        loginMap.put(PasswordPolicyType.LOCK_EXPIRE_TIME.getValue(), policy.getLockedExpireTime());

        Map<String, Object> passwordConfig = new LinkedHashMap<>();
        Map<String, Object> loginConfig = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : passwordMap.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            passwordConfig.put(e.getKey(), e.getValue());
        }
        for (Map.Entry<String, Object> e : loginMap.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            loginConfig.put(e.getKey(), e.getValue());
        }
        return new PasswordPolicyMap(passwordConfig, loginConfig, enablePassword, enableSecurity);
    }
}
