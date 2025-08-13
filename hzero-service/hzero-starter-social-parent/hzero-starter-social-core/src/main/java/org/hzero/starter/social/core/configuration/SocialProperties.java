package org.hzero.starter.social.core.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description
 *
 * @author bojiangzhou 2019/09/01
 */
@ConfigurationProperties(prefix = SocialProperties.PREFIX)
public class SocialProperties {

    public static final String PREFIX = "hzero.oauth.social";

    /**
     * bind user page
     */
    private String bindUrl = "/open-bind";

    /**
     * if user not bind, whether redirect to bind user page.
     */
    private boolean attemptBind = true;

    /**
     * open login process url
     */
    private String processUrl = "/open/**";

    private Qq qq = new Qq();


    public static class Qq {

        /**
         * 是否获取 unionId，需申请 getUnionId 权限
         */
        private boolean getUnionId = false;

        public boolean isGetUnionId() {
            return getUnionId;
        }

        public void setGetUnionId(boolean getUnionId) {
            this.getUnionId = getUnionId;
        }
    }


    public String getBindUrl() {
        return bindUrl;
    }

    public void setBindUrl(String bindUrl) {
        this.bindUrl = bindUrl;
    }

    public boolean isAttemptBind() {
        return attemptBind;
    }

    public void setAttemptBind(boolean attemptBind) {
        this.attemptBind = attemptBind;
    }

    public String getProcessUrl() {
        return processUrl;
    }

    public void setProcessUrl(String processUrl) {
        this.processUrl = processUrl;
    }

    public Qq getQq() {
        return qq;
    }

    public void setQq(Qq qq) {
        this.qq = qq;
    }
}
