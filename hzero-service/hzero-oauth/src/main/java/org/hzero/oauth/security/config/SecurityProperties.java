package org.hzero.oauth.security.config;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.hzero.core.util.DomainUtils;
import org.hzero.oauth.security.constant.LoginField;

/**
 * @author bojiangzhou
 */
@ConfigurationProperties(prefix = SecurityProperties.PREFIX)
public class SecurityProperties {

    public static final String PREFIX = "hzero.oauth";

    /**
     * 登录配置
     */
    private Login login = new Login();

    /**
     * 密码传输配置
     */
    private Password password = new Password();

    /**
     * 默认标题
     */
    private String title = "HZERO";

    /**
     * 默认语言
     */
    private String defaultLanguage = "zh_CN";
    /**
     * 是否展示切换语言
     */
    private boolean showLanguage = true;
    /**
     * 默认重定向地址
     */
    private String redirectUrl = "/";

    /**
     * if custom resource server request matcher.
     */
    private boolean customResourceMatcher = false;

    /**
     * if AuthorizationCodeTokenGranter not check clientId's consistency.
     */
    private boolean notCheckClientEquals = false;

    /**
     * if enable app captcha
     */
    private boolean enableAppCaptcha = false;

    /**
     * if always enable captcha
     */
    private boolean enableAlwaysCaptcha = false;

    /**
     * deviceId parameter name
     */
    private String deviceIdParameter = "device_id";
    /**
     * sourceType parameter name
     */
    private String sourceTypeParameter = "source_type";

    /**
     * if allow refresh token when grant_type is client_credentials
     */
    private boolean credentialsAllowRefresh = false;

    /**
     * this path will be permit to access with no authorized.
     */
    private String[] permitPaths = new String[]{};

    /**
     * 是否自动续期，如果 access_token 过期但是 refresh_token 未过期，则自动续期 access_token
     */
    private boolean accessTokenAutoRenewal = false;

    /**
     * oauth 服务基础地址，可为空
     */
    private String baseUrl;

    /**
     * 是否启用 https，配置了 baseUrl，则判断 baseUrl 是否以 https:// 开头
     */
    private boolean enableHttps;

    /**
     * 是否检查 redirect_uri 的一致性
     */
    private boolean checkRedirectUri = true;

    /**
     * 端口映射
     */
    private List<PortMapper> portMapper = Lists.newArrayList(new PortMapper(80, 80), new PortMapper(8080, 8080));

    public class Login {
        /**
         * 登录页面
         */
        private String page = "/login";
        /**
         * 密码过期页面
         */
        private String passExpiredPage = "/pass-page/expired";
        /**
         * 强制修改密码页面
         */
        private String passForceModifyPage = "/pass-page/force-modify";
        /**
         * 二次校验页面
         */
        private String passSecondaryCheckPage = "/pass-page/secondary-check";
        /**
         * 登录成功地址
         */
        private String successUrl = "/";
        /**
         * ssl
         */
        private boolean ssl = false;

        /**
         * 支持的登录字段
         */
        private Set<String> supportFields = LoginField.codes();

        /**
         * 用户名登录参数
         */
        private String usernameParameter = "username";
        /**
         * 手机短信登录参数
         */
        private String mobileParameter = "phone";
        /**
         * 二次校验账户登录参数
         */
        private String secCheckTypeParameter = "secCheckType";

        /**
         * 默认模板
         */
        private String defaultTemplate = "main";

        /**
         * 默认的客户端
         */
        private String defaultClientId = "client";

        /**
         * 手机短信登录处理地址
         */
        private String mobileLoginProcessUrl = "/login/sms";
        /**
         * 二次校验处理地址
         */
        private String secCheckLoginProcessUrl = "/login/secCheck";
        /**
         * 密码防重放缓存过期时间(天)
         */
        private Long passReplayExpire = 30L;

        /**
         * 登录账号是否加密传输
         */
        private boolean accountEncrypt = false;
        /**
         * 登录密码是否加密传输
         */
        private boolean passwordEncrypt = true;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getPassExpiredPage() {
            return passExpiredPage;
        }

        public void setPassExpiredPage(String passExpiredPage) {
            this.passExpiredPage = passExpiredPage;
        }

        public String getPassForceModifyPage() {
            return passForceModifyPage;
        }

        public void setPassForceModifyPage(String passForceModifyPage) {
            this.passForceModifyPage = passForceModifyPage;
        }

        public String getPassSecondaryCheckPage() {
            return passSecondaryCheckPage;
        }

        public void setPassSecondaryCheckPage(String passSecondaryCheckPage) {
            this.passSecondaryCheckPage = passSecondaryCheckPage;
        }

        public String getSuccessUrl() {
            return successUrl;
        }

        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }

        public boolean isSsl() {
            return ssl;
        }

        public void setSsl(boolean ssl) {
            this.ssl = ssl;
        }

        public Set<String> getSupportFields() {
            return supportFields;
        }

        public void setSupportFields(Set<String> supportFields) {
            this.supportFields = supportFields;
        }

        public String getUsernameParameter() {
            return usernameParameter;
        }

        public void setUsernameParameter(String usernameParameter) {
            this.usernameParameter = usernameParameter;
        }

        public String getMobileParameter() {
            return mobileParameter;
        }

        public void setMobileParameter(String mobileParameter) {
            this.mobileParameter = mobileParameter;
        }

        public String getSecCheckTypeParameter() {
            return secCheckTypeParameter;
        }

        public void setSecCheckTypeParameter(String secCheckTypeParameter) {
            this.secCheckTypeParameter = secCheckTypeParameter;
        }

        public String getDefaultTemplate() {
            return defaultTemplate;
        }

        public void setDefaultTemplate(String defaultTemplate) {
            this.defaultTemplate = defaultTemplate;
        }

        public String getDefaultClientId() {
            return defaultClientId;
        }

        public void setDefaultClientId(String defaultClientId) {
            this.defaultClientId = defaultClientId;
        }

        public String getMobileLoginProcessUrl() {
            return mobileLoginProcessUrl;
        }

        public void setMobileLoginProcessUrl(String mobileLoginProcessUrl) {
            this.mobileLoginProcessUrl = mobileLoginProcessUrl;
        }

        public String getSecCheckLoginProcessUrl() {
            return secCheckLoginProcessUrl;
        }

        public void setSecCheckLoginProcessUrl(String secCheckLoginProcessUrl) {
            this.secCheckLoginProcessUrl = secCheckLoginProcessUrl;
        }

        public Long getPassReplayExpire() {
            return passReplayExpire;
        }

        public Login setPassReplayExpire(Long passReplayExpire) {
            this.passReplayExpire = passReplayExpire;
            return this;
        }

        public boolean isAccountEncrypt() {
            return accountEncrypt;
        }

        public void setAccountEncrypt(boolean accountEncrypt) {
            this.accountEncrypt = accountEncrypt;
        }

        public boolean isPasswordEncrypt() {
            return passwordEncrypt;
        }

        public void setPasswordEncrypt(boolean passwordEncrypt) {
            this.passwordEncrypt = passwordEncrypt;
        }
    }

    public static class Password {
        /**
         * 密码加密公钥
         */
        private String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJL0JkqsUoK6kt3JyogsgqNp9VDGDp+t3ZAGMbVoMPdHNT2nfiIVh9ZMNHF7g2XiAa8O8AQWyh2PjMR0NiUSVQMCAwEAAQ==";
        /**
         * 密码加密私钥
         */
        private String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAkvQmSqxSgrqS3cnKiCyCo2n1UMYOn63dkAYxtWgw90c1Pad+IhWH1kw0cXuDZeIBrw7wBBbKHY+MxHQ2JRJVAwIDAQABAkB3TKXZcVP6tSSN0UgOjLPxng99Z4xvrWJ1jdHFB7TYyUDcnOtE6GkeeMGxtszYtuZ0m5rN9r8eRvIdVUciXbQhAiEA7WGcLd+cvHHA8xNFrbT78Aq4iYm0TVTS6m05ZEOS328CIQCeetcFV8Kdb+P2Y+yWvMtbyFGhROQIFizvxM3S3TiZrQIhAJLrYPB7f9SaSyOm/+89Htk4qXJmyjM6lrgFFgpaUGL9AiB+pkCr/mSDGOYfA+AQ3rPNl5rUvI9XfxFOVAMAntYayQIhAO0hrnwtng6Ubs+58BN69vAl+sd/xugf0jsHcNgZppAb";
        /**
         * 是否启用加密传输
         */
        @Deprecated
        private boolean enableEncrypt = true;

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        @Deprecated
        public boolean isEnableEncrypt() {
            return enableEncrypt;
        }

        @Deprecated
        public void setEnableEncrypt(boolean enableEncrypt) {
            this.enableEncrypt = enableEncrypt;
        }
    }

    public static class PortMapper {
        /**
         * 源端口
         */
        private int sourcePort;
        /**
         * 映射端口
         */
        private int mappingPort;

        public PortMapper() {
        }

        public PortMapper(int sourcePort, int mappingPort) {
            this.sourcePort = sourcePort;
            this.mappingPort = mappingPort;
        }

        public int getSourcePort() {
            return sourcePort;
        }

        public void setSourcePort(int sourcePort) {
            this.sourcePort = sourcePort;
        }

        public int getMappingPort() {
            return mappingPort;
        }

        public void setMappingPort(int mappingPort) {
            this.mappingPort = mappingPort;
        }
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isCustomResourceMatcher() {
        return customResourceMatcher;
    }

    public void setCustomResourceMatcher(boolean customResourceMatcher) {
        this.customResourceMatcher = customResourceMatcher;
    }

    public boolean isNotCheckClientEquals() {
        return notCheckClientEquals;
    }

    public void setNotCheckClientEquals(boolean notCheckClientEquals) {
        this.notCheckClientEquals = notCheckClientEquals;
    }

    public boolean isEnableAppCaptcha() {
        return enableAppCaptcha;
    }

    public void setEnableAppCaptcha(boolean enableAppCaptcha) {
        this.enableAppCaptcha = enableAppCaptcha;
    }

    public boolean isEnableAlwaysCaptcha() {
        return enableAlwaysCaptcha;
    }

    public void setEnableAlwaysCaptcha(boolean enableAlwaysCaptcha) {
        this.enableAlwaysCaptcha = enableAlwaysCaptcha;
    }

    public String getDeviceIdParameter() {
        return deviceIdParameter;
    }

    public void setDeviceIdParameter(String deviceIdParameter) {
        this.deviceIdParameter = deviceIdParameter;
    }

    public String getSourceTypeParameter() {
        return sourceTypeParameter;
    }

    public void setSourceTypeParameter(String sourceTypeParameter) {
        this.sourceTypeParameter = sourceTypeParameter;
    }

    public boolean isCredentialsAllowRefresh() {
        return credentialsAllowRefresh;
    }

    public void setCredentialsAllowRefresh(boolean credentialsAllowRefresh) {
        this.credentialsAllowRefresh = credentialsAllowRefresh;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public boolean isShowLanguage() {
        return showLanguage;
    }

    public void setShowLanguage(boolean showLanguage) {
        this.showLanguage = showLanguage;
    }

    public String[] getPermitPaths() {
        return permitPaths;
    }

    public void setPermitPaths(String[] permitPaths) {
        this.permitPaths = permitPaths;
    }

    public boolean isAccessTokenAutoRenewal() {
        return accessTokenAutoRenewal;
    }

    public SecurityProperties setAccessTokenAutoRenewal(boolean accessTokenAutoRenewal) {
        this.accessTokenAutoRenewal = accessTokenAutoRenewal;
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isEnableHttps() {
        if (StringUtils.isNotBlank(baseUrl)) {
            return baseUrl.startsWith(DomainUtils.HTTPS);
        }
        return enableHttps;
    }

    public void setEnableHttps(boolean enableHttps) {
        this.enableHttps = enableHttps;
    }

    public List<PortMapper> getPortMapper() {
        return portMapper;
    }

    public void setPortMapper(List<PortMapper> portMapper) {
        this.portMapper = portMapper;
    }

    public boolean isCheckRedirectUri() {
        return checkRedirectUri;
    }

    public void setCheckRedirectUri(boolean checkRedirectUri) {
        this.checkRedirectUri = checkRedirectUri;
    }
}
