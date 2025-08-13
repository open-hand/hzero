package org.hzero.sso.core.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

import org.hzero.sso.core.service.SsoTokenClearService;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
public class DefaultSsoTokenClearService implements SsoTokenClearService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSsoTokenClearService.class);

    private final TokenStore tokenStore;

    public DefaultSsoTokenClearService(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public void clearTokenOnLogout(HttpServletRequest request, HttpServletResponse response, String token) {
        LOGGER.debug("sso logout clear token, token: [{}]", token);
        request.getSession().invalidate();
        if (token != null) {
            tokenStore.removeAccessToken(new DefaultOAuth2AccessToken(token));
            tokenStore.removeRefreshToken(new DefaultOAuth2RefreshToken(token));
        }
    }
}
