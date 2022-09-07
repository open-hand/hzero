package org.hzero.oauth.security.sso;

import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.util.RequestUtil;
import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.configuration.SsoPropertyService;
import org.hzero.sso.core.standard.StandardSsoPropertyService;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
public class CustomSsoPropertyService extends StandardSsoPropertyService implements SsoPropertyService {

    private final SecurityProperties securityProperties;

    public CustomSsoPropertyService(SecurityProperties securityProperties, SsoProperties ssoProperties) {
        super(ssoProperties);
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
