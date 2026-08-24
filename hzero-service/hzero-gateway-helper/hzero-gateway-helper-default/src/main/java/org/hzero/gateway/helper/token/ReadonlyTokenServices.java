/*
 * Copyright 2008 Web Cohesion
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.hzero.gateway.helper.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Base implementation for token services using random UUID values for the access token and refresh token values. The
 * main extension point for customizations is the {@link TokenEnhancer} which will be called after the access and
 * refresh tokens have been generated but before they are stored.
 * <p>
 * Persistence is delegated to a {@code TokenStore} implementation and customization of the access token to a
 * {@link TokenEnhancer}.
 *
 * @author Ryan Heaton
 * @author Luke Taylor
 * @author Dave Syer
 */
public class ReadonlyTokenServices implements AuthorizationServerTokenServices, ResourceServerTokenServices,
        ConsumerTokenServices, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ReadonlyTokenServices.class);
    private TokenStore tokenStore;

    public ReadonlyTokenServices(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    /**
     * Initialize these token services. If no random generator is set, one will be created.
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(tokenStore, "tokenStore must be set");
    }

    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        throw new UnsupportedOperationException("It is not allowed to create an access token here.");
    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = {InvalidTokenException.class, InvalidGrantException.class})
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
            throws AuthenticationException {
        throw new UnsupportedOperationException("Refreshing the access token is not allowed here.");
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return tokenStore.getAccessToken(authentication);
    }

    public OAuth2AccessToken readAccessToken(String accessToken) {
        return tokenStore.readAccessToken(accessToken);
    }

    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException,
            InvalidTokenException {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            logger.debug("[AccessToken] Expired or does not exist : {}", accessTokenValue);
            return null;
        } else if (accessToken.isExpired()) {
            logger.debug("[AccessToken] Expired : {}", accessTokenValue);
            throw new IllegalStateException("[AccessToken] Expired");
        }

        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
        if (result == null) {
            // in case of race condition
            logger.debug("[AccessToken] Expired : {}", accessTokenValue);
            return null;
        }
        return result;
    }

    public boolean revokeToken(String tokenValue) {
        throw new UnsupportedOperationException("It is not allowed to revoke the access token.");
    }
}
