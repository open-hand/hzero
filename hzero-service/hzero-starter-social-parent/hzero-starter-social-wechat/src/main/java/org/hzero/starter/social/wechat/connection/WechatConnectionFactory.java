package org.hzero.starter.social.wechat.connection;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.oauth2.AccessGrant;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.provider.Provider;

/**
 * 微信 Connection 工厂
 *
 * @author bojiangzhou 2018/10/17
 */
public class WechatConnectionFactory extends SocialConnectionFactory {

    public WechatConnectionFactory(Provider provider, SocialServiceProvider serviceProvider, SocialApiAdapter apiAdapter) {
        super(provider, serviceProvider, apiAdapter);
        // 微信默认 scope=snsapi_login
        setScope("snsapi_login");
    }

    @Override
    protected String extractProviderUserId(AccessGrant accessGrant) {
        if (accessGrant instanceof WechatAccessGrant) {
            WechatAccessGrant wechatAccessGrant = (WechatAccessGrant) accessGrant;
            return wechatAccessGrant.getOpenId();
        }
        return null;
    }

    @Override
    public Connection<SocialApi> createConnection(AccessGrant accessGrant) {
        String providerUserId = extractProviderUserId(accessGrant);
        return new OAuth2Connection<>(getProviderId(), providerUserId, accessGrant.getAccessToken(),
                accessGrant.getRefreshToken(), accessGrant.getExpireTime(), getServiceProvider(), new WechatApiAdapter(providerUserId));
    }

    @Override
    public Connection<SocialApi> createConnection(ConnectionData data) {
        return new OAuth2Connection<>(data, getServiceProvider(), new WechatApiAdapter(data.getProviderUserId()));
    }

}
