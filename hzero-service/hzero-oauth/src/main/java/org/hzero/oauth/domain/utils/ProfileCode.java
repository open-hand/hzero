package org.hzero.oauth.domain.utils;

/**
 * 用户相关 配置常量编码
 *
 * @author bojiangzhou 2019/10/22
 */
public enum ProfileCode {
    /**
     * 消息代码：短信登录
     */
    MSG_CODE_MOBILE_LOGIN(null, "HOTH.MOBILE_LOGIN", "hzero.send-message.mobile-login"),
    /**
     * 消息代码：邮箱登录
     */
    MSG_CODE_EMAIL_LOGIN(null, "HOTH.EMAIL_LOGIN", "hzero.send-message.email-login"),
    /**
     * 消息代码：修改登录密码
     */
    MSG_CODE_MODIFY_PASSWORD(null, "HOTH.MODIFY_PASSWORD", "hzero.send-message.modify-login-password"),

    /**
     * Title
     */
    OAUTH_TITLE("HOTH.TITLE", "HZERO", "hzero.oauth.title"),
    /**
     * Copyright
     */
    OAUTH_COPYRIGHT("HOTH.COPYRIGHT", "Copyright © The HZERO Author®. All rights reserved.", "hzero.oauth.copyright"),
    /**
     * Logo URL
     */
    OAUTH_LOGO_URL("HOTH.LOGO_URL", "/oauth/static/main/img/logo.svg", "hzero.oauth.logo-url"),
    /**
     * 默认语言
     */
    OAUTH_DEFAULT_LANGUAGE("HOTH.DEFAULT_LANGUAGE", "zh_CN", "hzero.oauth.default-language"),
    /**
     * 是否显示语言
     */
    OAUTH_SHOW_LANGUAGE("HOTH.SHOW_LANGUAGE", "1", "hzero.oauth.show-language"),
    /**
     * 默认模板
     */
    OAUTH_DEFAULT_TEMPLATE(null, "main", "hzero.oauth.login.default-template"),

    ;

    private String profileKey;
    private String defaultValue;
    private String configKey;

    ProfileCode(String profileKey, String defaultValue, String configKey) {
        this.profileKey = profileKey;
        this.defaultValue = defaultValue;
        this.configKey = configKey;
    }

    public String profileKey() {
        return profileKey;
    }

    public String defaultValue() {
        return defaultValue;
    }

    public String configKey() {
        return configKey;
    }

}
