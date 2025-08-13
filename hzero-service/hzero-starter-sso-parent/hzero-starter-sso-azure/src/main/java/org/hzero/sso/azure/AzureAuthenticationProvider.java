package org.hzero.sso.azure;

import static org.hzero.sso.core.constant.SsoAttributes.UNKNOWN;

import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.hzero.core.util.CommonExecutor;
import org.hzero.sso.core.common.SsoAuthenticationProviderAdapter;
import org.hzero.sso.core.constant.OAuthParameters;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.exception.SsoServerException;
import org.hzero.sso.core.exception.SsoUserNotFoundException;
import org.hzero.sso.core.security.SsoAuthenticationToken;
import org.hzero.sso.core.service.SsoUserDetailsService;

/**
 * @author bojiangzhou 2020/08/19
 */
public class AzureAuthenticationProvider extends SsoAuthenticationProviderAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureAuthenticationProvider.class);

    private final ThreadPoolExecutor executor;


    public AzureAuthenticationProvider(SsoUserDetailsService userDetailsService) {
        super(userDetailsService);
        executor = CommonExecutor.buildThreadFirstExecutor(8, 8 * 8, 10, TimeUnit.MINUTES, 1 << 16, "SsoAzurePool");
    }

    @Override
    protected Authentication extractAuthentication(HttpServletRequest request, HttpServletResponse response, Domain domain) throws AuthenticationException {
        // 授权码
        String code = request.getParameter(OAuthParameters.CODE);

        String redirectUrl = domain.getClientHostUrl();
        String authority = domain.getSsoServerUrl();

        IAuthenticationResult result = null;
        try {
            ConfidentialClientApplication app = ConfidentialClientApplication
                    .builder(domain.getSsoClientId(), ClientCredentialFactory.createFromSecret(domain.getSsoClientPwd()))
                    .authority(authority)
                    .executorService(executor)
                    .build();

            AuthorizationCodeParameters parameters = AuthorizationCodeParameters
                    .builder(code, new URI(redirectUrl))
                    .build();

            Future<IAuthenticationResult> future = app.acquireToken(parameters);
            result = future.get();
        } catch (Exception e) {
            LOGGER.warn("azure sso request error", e);
            throw new SsoServerException(e);
        }

        if (result == null || result.account() == null) {
            throw new SsoUserNotFoundException();
        }

        String username = result.account().username();

        return new SsoAuthenticationToken(username, UNKNOWN);
    }


}
