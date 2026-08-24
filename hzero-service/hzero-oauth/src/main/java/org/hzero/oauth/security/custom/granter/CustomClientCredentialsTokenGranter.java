package org.hzero.oauth.security.custom.granter;

import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import io.choerodon.core.oauth.CustomClientDetails;

import org.hzero.oauth.security.service.ClientDetailsWrapper;

/**
 *
 * @author bojiangzhou 2020/09/03
 */
public class CustomClientCredentialsTokenGranter extends ClientCredentialsTokenGranter {

    private final ClientDetailsWrapper clientDetailsWrapper;

    public CustomClientCredentialsTokenGranter(AuthorizationServerTokenServices tokenServices,
                                               ClientDetailsService clientDetailsService,
                                               OAuth2RequestFactory requestFactory,
                                               ClientDetailsWrapper clientDetailsWrapper) {
        super(tokenServices, clientDetailsService, requestFactory);
        this.clientDetailsWrapper = clientDetailsWrapper;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        if (client instanceof CustomClientDetails) {
            CustomClientDetails clientDetails = (CustomClientDetails) client;
            clientDetailsWrapper.warp(clientDetails, clientDetails.getId(), clientDetails.getOrganizationId());
        }
        return new OAuth2Authentication(storedOAuth2Request, null, client);
    }
}
