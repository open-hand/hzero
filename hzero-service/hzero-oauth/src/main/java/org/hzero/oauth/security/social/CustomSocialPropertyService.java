package org.hzero.oauth.security.social;

import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.util.RequestUtil;
import org.hzero.starter.social.core.configuration.SocialPropertyService;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
public class CustomSocialPropertyService implements SocialPropertyService {

    private final SecurityProperties securityProperties;

    public CustomSocialPropertyService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public String getBaseUrl() {
        return securityProperties.getBaseUrl();
    }

    @Override
    public Function<HttpServletRequest, String> getDynamicBaseUrlFunction() {
        return RequestUtil::getBaseURL;
    }

    @Override
    public boolean isEnableHttps() {
        return securityProperties.isEnableHttps();
    }

    @Override
    public String getLoginPage() {
        return securityProperties.getLogin().getPage();
    }
}
