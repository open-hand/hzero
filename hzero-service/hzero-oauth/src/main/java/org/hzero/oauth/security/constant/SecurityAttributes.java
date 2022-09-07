package org.hzero.oauth.security.constant;

import javax.servlet.http.HttpSession;

/**
 * Spring Security 相关常量
 *
 * @author bojiangzhou 2019/02/25
 */
public class SecurityAttributes {

    public static final LoginType DEFAULT_LOGIN_TYPE = LoginType.ACCOUNT;
    //
    // Security 变量
    // ------------------------------------------------------------------------------

    public static final String SECURITY_LAST_EXCEPTION = "SPRING_SECURITY_LAST_EXCEPTION";
    public static final String SECURITY_LAST_EXCEPTION_PARAMS = "SPRING_SECURITY_LAST_EXCEPTION_PARAMS";
    public static final String SECURITY_SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";
    public static final String SECURITY_LOGIN_USER = "SPRING_SECURITY_LOGIN_USER";
    public static final String SECURITY_PARAMS = "SPRING_SECURITY_PARAMS";
    public static final String SECURITY_LOGIN_USERNAME = "username";
    public static final String SECURITY_LOGIN_USER_ID = "userId";

    public static final String SECURITY_LOGIN_MOBILE = "phone";
    public static final String SECURITY_ERROR_CODE = "errorCode";
    public static final String SECURITY_FORCE_CODE_VERIFY = "forceCodeVerify";

    /**
     * 清除session中security相关变量
     * 
     * @param session HttpSession
     */
    public static void removeSecuritySessionAttribute(HttpSession session) {
        session.removeAttribute(SECURITY_LAST_EXCEPTION);
        session.removeAttribute(SECURITY_LAST_EXCEPTION_PARAMS);
        session.removeAttribute(SECURITY_LOGIN_USER);
        session.removeAttribute(SECURITY_LOGIN_USERNAME);
        session.removeAttribute(SECURITY_PARAMS);
    }

    //
    // 前端显示字段
    // ------------------------------------------------------------------------------

    public static final String FIELD_OPEN_LOGIN_WAYS = "openLoginWays";
    public static final String FIELD_OPEN_LOGIN_WAYS_JSON = "openLoginWaysJson";
    public static final String FIELD_LANGUAGES = "languages";
    public static final String FIELD_LANGUAGES_JSON = "languagesJson";
    public static final String FIELD_LOGIN_TYPE = "type";
    public static final String FIELD_LOGIN_ERROR_MSG = "loginErrorMsg";
    public static final String FIELD_IS_NEED_CAPTCHA = "isNeedCaptcha";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_INIT_LANG_FLAG = "initLangFlag";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";

}
