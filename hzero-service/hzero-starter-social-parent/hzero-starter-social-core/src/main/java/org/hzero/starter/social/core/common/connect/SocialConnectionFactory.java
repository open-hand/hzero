package org.hzero.starter.social.core.common.connect;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.provider.Provider;

/**
 * OAuth2ConnectionFactory
 *
 * @author bojiangzhou 2019/08/29
 */
public class SocialConnectionFactory extends OAuth2ConnectionFactory<SocialApi> {

    private SocialServiceProvider serviceProvider;
    private SocialApiAdapter apiAdapter;

    public SocialConnectionFactory(Provider provider, SocialServiceProvider serviceProvider, SocialApiAdapter apiAdapter) {
        super(provider.getProviderId(), serviceProvider, apiAdapter);
        this.serviceProvider = serviceProvider;
        this.apiAdapter = apiAdapter;
    }

    @Override
    public SocialTemplate getOAuthOperations() {
        return (SocialTemplate) serviceProvider.getOAuthOperations();
    }

    @Override
    protected SocialServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    @Override
    protected SocialApiAdapter getApiAdapter() {
        return apiAdapter;
    }

    @Override
    public Connection<SocialApi> createConnection(AccessGrant accessGrant) {
        return super.createConnection(accessGrant);
    }

    @Override
    public Connection<SocialApi> createConnection(ConnectionData data) {
        return super.createConnection(data);
    }

}
