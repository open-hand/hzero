package org.hzero.boot.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 *
 * @author bojiangzhou 2019/08/07
 */
@RefreshScope
@ConfigurationProperties(prefix = BootOauthProperties.PREFIX)
public class BootOauthProperties {

    public static final String PREFIX = "hzero";

    private Captcha captcha = new Captcha();

    private User user = new User();

    public Captcha getCaptcha() {
        return captcha;
    }

    public void setCaptcha(Captcha captcha) {
        this.captcha = captcha;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class Captcha {
        /**
         * enable image captcha first
         */
        private boolean alwaysEnable = false;

        public boolean isAlwaysEnable() {
            return alwaysEnable;
        }

        public void setAlwaysEnable(boolean alwaysEnable) {
            this.alwaysEnable = alwaysEnable;
        }
    }


    public static class User {
        /**
         * 是否启用 Root 用户功能，启用 Root 用户功能后，超级用户可以切换到任意租户下
         */
        private boolean enableRoot = false;

        public boolean isEnableRoot() {
            return enableRoot;
        }

        public void setEnableRoot(boolean enableRoot) {
            this.enableRoot = enableRoot;
        }
    }


}
