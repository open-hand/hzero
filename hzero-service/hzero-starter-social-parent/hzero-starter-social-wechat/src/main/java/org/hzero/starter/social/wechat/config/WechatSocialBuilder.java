package org.hzero.starter.social.wechat.config;

import org.springframework.context.annotation.Configuration;

import org.hzero.starter.social.core.common.configurer.SocialConnectionFactoryBuilder;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.constant.ProviderEnum;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.wechat.connection.WechatApiAdapter;
import org.hzero.starter.social.wechat.connection.WechatConnectionFactory;
import org.hzero.starter.social.wechat.connection.WechatServiceProvider;
import org.hzero.starter.social.wechat.connection.WechatTemplate;

/**
 * social 配置
 *
 * @author bojiangzhou
 */
@Configuration
public class WechatSocialBuilder implements SocialConnectionFactoryBuilder {

    @Override
    public String getProviderId() {
        return ProviderEnum.wechat.name();
    }

    @Override
    public SocialConnectionFactory buildConnectionFactory(Provider provider) {
        // 获取授权码地址
        final String URL_AUTHORIZE = "https://open.weixin.qq.com/connect/qrconnect";
        // 获取令牌地址
        final String URL_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
        // 获取用户信息的地址
        final String URL_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";

        provider.setAuthorizeUrl(URL_AUTHORIZE);
        provider.setAccessTokenUrl(URL_GET_ACCESS_TOKEN);
        provider.setUserInfoUrl(URL_GET_USER_INFO);

        WechatApiAdapter apiAdapter = new WechatApiAdapter();
        WechatTemplate template = new WechatTemplate(provider);
        WechatServiceProvider serviceProvider = new WechatServiceProvider(provider, template);
        return new WechatConnectionFactory(provider, serviceProvider, apiAdapter);
    }

}
