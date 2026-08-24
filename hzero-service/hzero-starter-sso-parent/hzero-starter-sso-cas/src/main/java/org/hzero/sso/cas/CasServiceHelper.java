package org.hzero.sso.cas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.CommonUtils;
import org.springframework.security.cas.ServiceProperties;

import org.hzero.sso.core.constant.OAuthParameters;
import org.hzero.sso.core.domain.Domain;

/**
 *
 * @author bojiangzhou 2020/08/25
 */
public class CasServiceHelper {

    private final ServiceProperties serviceProperties = new ServiceProperties();

    public String createRedirectUrl(final HttpServletRequest request, final HttpServletResponse response, final Domain domain) {
        String serviceUrl = createServiceUrl(request, response, domain);

        return CommonUtils.constructRedirectUrl(domain.getSsoLoginUrl(), serviceProperties.getServiceParameter(),
                serviceUrl, serviceProperties.isSendRenew(), false);
    }

    public String createServiceUrl(final HttpServletRequest request, final HttpServletResponse response, final Domain domain) {
        String service = domain.getClientHostUrl() + "?" + OAuthParameters.STATE + "=" + domain.getHost();

        return CommonUtils.constructServiceUrl(request, response, service, null,
                serviceProperties.getServiceParameter(), serviceProperties.getArtifactParameter(), true);
    }

    protected String createLogoutRedirectUrl(final HttpServletRequest request, final HttpServletResponse response, final Domain domain) {
        String service = domain.getDomainUrl();

        String serviceUrl = CommonUtils.constructServiceUrl(request, response, service, null,
                serviceProperties.getServiceParameter(), serviceProperties.getArtifactParameter(), true);

        return CommonUtils.constructRedirectUrl(domain.getSsoLogoutUrl(), serviceProperties.getServiceParameter(),
                serviceUrl, serviceProperties.isSendRenew(), false);
    }

}
