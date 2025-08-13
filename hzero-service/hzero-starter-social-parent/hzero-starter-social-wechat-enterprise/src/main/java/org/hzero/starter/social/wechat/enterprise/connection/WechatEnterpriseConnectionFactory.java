package org.hzero.starter.social.wechat.enterprise.connection;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.provider.Provider;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;

/**
 * 企业微信 Connection 工厂
 *
 * @author wanshun.zhang@hand-china.com 2019/11/12
 */
public class WechatEnterpriseConnectionFactory extends SocialConnectionFactory {

    public WechatEnterpriseConnectionFactory(Provider provider, SocialServiceProvider serviceProvider, SocialApiAdapter apiAdapter) {
        super(provider, serviceProvider, apiAdapter);
    }

    @Override
    public Connection<SocialApi> createConnection(ConnectionData data) {
        return new OAuth2Connection<>(data, getServiceProvider(), new WechatEnterpriseApiAdapter(data.getProviderUserId()));
    }
}
