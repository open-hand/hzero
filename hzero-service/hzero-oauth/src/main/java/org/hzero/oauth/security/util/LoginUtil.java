package org.hzero.oauth.security.util;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.user.UserType;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.LoginSource;
import org.hzero.oauth.security.constant.SecurityAttributes;

/**
 * login util.
 *
 * @author bojiangzhou 2019/01/10
 */
@Component
public class LoginUtil {

    public static final String FIELD_TEMPLATE = "template";
    public static final String FIELD_LOGIN_FIELD = "login_field";
    public static final String FIELD_CLIENT_ID = "client_id";
    public static final String FIELD_CLIENT_SECRET = "client_secret";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_DEVICE_ID = "device_id";
    public static final String FIELD_SOURCE_TYPE = "source_type";
    public static final String FIELD_GRANT_TYPE = "grant_type";
    public static final String FIELD_CAPTCHA = "captcha";
    public static final String FIELD_CAPTCHA_KEY = "captchaKey";
    public static final String FIELD_PUBLIC_KEY = "publicKey";
    public static final String FIELD_ACCESS_TOKEN = "access_token";
    public static final String FIELD_PASSWORD_ENCRYPT = "passwordEncrypt";
    public static final String FIELD_ACCOUNT_ENCRYPT = "accountEncrypt";

    public static final String FIELD_LOGO_URL = "logoUrl";
    public static final String FIELD_TITLE = "systemTitle";
    public static final String FIELD_COPYRIGHT = "copyright";


    /**
     * 登录来源参数字段
     */
    private final String LOGIN_SOURCE_TYPE_KEY;
    /**
     * 登录设备ID参数字段
     */
    private final String LOGIN_DEVICE_ID_KEY;

    private final BasePasswordPolicyRepository basePasswordPolicyRepository;

    @Autowired
    public LoginUtil(SecurityProperties properties, BasePasswordPolicyRepository basePasswordPolicyRepository) {
        this.LOGIN_DEVICE_ID_KEY = properties.getDeviceIdParameter();
        this.LOGIN_SOURCE_TYPE_KEY = properties.getSourceTypeParameter();
        this.basePasswordPolicyRepository = basePasswordPolicyRepository;
    }

    public String getDeviceIdParameter() {
        return LOGIN_DEVICE_ID_KEY;
    }

    public String getSourceTypeParameter() {
        return LOGIN_SOURCE_TYPE_KEY;
    }

    /**
     * 判断是否是移动设备登录
     *
     * @param authentication OAuth2Authentication
     * @return true
     */
    public boolean isMobileDeviceLogin(OAuth2Authentication authentication) {
        OAuth2Request auth2Request = authentication.getOAuth2Request();
        if (auth2Request == null || MapUtils.isEmpty(auth2Request.getRequestParameters())) {
            return false;
        }
        Map<String, String> parameters = auth2Request.getRequestParameters();

        return parameters.containsKey(LOGIN_SOURCE_TYPE_KEY)
                && StringUtils.equalsIgnoreCase(parameters.get(LOGIN_SOURCE_TYPE_KEY), LoginSource.APP.name());
    }

    public boolean isWebDeviceLogin(OAuth2Authentication authentication) {
        return !isMobileDeviceLogin(authentication);
    }

    public boolean isWebSingleLogin(OAuth2Authentication authentication) {
        if (!isWebDeviceLogin(authentication)) {
            return false;
        }
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
            BasePasswordPolicy policy = basePasswordPolicyRepository.selectPasswordPolicy(details.getOrganizationId());
            Assert.notNull(policy, details.getUsername() + "'s tenant password policy is null");
            return !computeBoolean(policy.getEnableWebMultipleLogin());
        }
        return false;
    }

    public boolean isAppSingleLogin(OAuth2Authentication authentication) {
        if (!isMobileDeviceLogin(authentication)) {
            return false;
        }
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
            BasePasswordPolicy policy = basePasswordPolicyRepository.selectPasswordPolicy(details.getOrganizationId());
            Assert.notNull(policy, details.getUsername() + "'s tenant password policy is null");
            return !computeBoolean(policy.getEnableAppMultipleLogin());
        }
        return false;
    }

    public UserType getUserType(OAuth2Authentication authentication) {
        OAuth2Request auth2Request = authentication.getOAuth2Request();
        String userType = null;
        if (auth2Request != null && !MapUtils.isEmpty(auth2Request.getRequestParameters())) {
            userType = auth2Request.getRequestParameters().get(UserType.PARAM_NAME);
        }
        if (StringUtils.isBlank(userType)) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
                userType = details.getUserType();
            }
        }
        return UserType.ofDefault(userType);
    }

    private boolean computeBoolean(Boolean pack) {
        // 默认 true
        return pack == null ? true : pack;
    }

    /**
     * 获取当前语言
     *
     * @return Locale
     */
    public static Locale getLanguageLocale() {
        return LocaleUtils.toLocale(getLanguage());
    }

    /**
     * 获取当前语言
     *
     * @return lang
     */
    public static String getLanguage() {
        return RequestUtil.getParameterValueFromRequestOrSavedRequest(SecurityAttributes.FIELD_LANG, LanguageHelper.language());
    }

}
