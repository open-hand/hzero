package org.hzero.sso.cas;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * @author bojiangzhou 2020/08/18
 */
public class CasAuthenticationRouter implements SsoAuthenticationRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasAuthenticationRouter.class);

    private final CasServiceHelper casServiceHelper;

    public CasAuthenticationRouter(CasServiceHelper casServiceHelper) {
        this.casServiceHelper = casServiceHelper;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException)
            throws IOException, ServletException {
        Domain domain = SsoContextHolder.getNonNullDomain();

        String redirectUrl = casServiceHelper.createRedirectUrl(request, response, domain);

        LOGGER.debug("cas redirect to sso server, redirectUrl: [{}]", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

}
