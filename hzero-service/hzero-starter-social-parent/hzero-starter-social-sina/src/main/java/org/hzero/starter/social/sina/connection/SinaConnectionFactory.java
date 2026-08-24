package org.hzero.starter.social.sina.connection;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.provider.Provider;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.oauth2.AccessGrant;

/**
 * Sina Connection 工厂
 *
 * @author liufanghan 2019/09/18 13:23
 */
public class SinaConnectionFactory extends SocialConnectionFactory {

    public SinaConnectionFactory(Provider provider, SocialServiceProvider serviceProvider, SocialApiAdapter apiAdapter) {
        super(provider, serviceProvider, apiAdapter);
    }

    @Override
    protected String extractProviderUserId(AccessGrant accessGrant) {
        if (accessGrant instanceof SinaAccessGrant) {
            SinaAccessGrant sinaAccessGrant = (SinaAccessGrant) accessGrant;
            return sinaAccessGrant.getOpenId();
        }
        return null;
    }

    @Override
    public Connection<SocialApi> createConnection(AccessGrant accessGrant) {
        return new OAuth2Connection<>(getProviderId(), extractProviderUserId(accessGrant), accessGrant.getAccessToken(),
                accessGrant.getRefreshToken(), accessGrant.getExpireTime(), getServiceProvider(), new SinaApiAdapter(extractProviderUserId(accessGrant)));
    }

    @Override
    public Connection<SocialApi> createConnection(ConnectionData data) {
        return new OAuth2Connection<>(data, getServiceProvider(), new SinaApiAdapter(data.getProviderUserId()));
    }
}
