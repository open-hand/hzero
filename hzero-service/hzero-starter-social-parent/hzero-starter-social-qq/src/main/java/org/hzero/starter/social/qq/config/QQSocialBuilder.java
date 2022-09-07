package org.hzero.starter.social.qq.config;

import org.springframework.context.annotation.Configuration;

import org.hzero.starter.social.core.common.configurer.SocialConnectionFactoryBuilder;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.constant.ProviderEnum;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.qq.connect.QQApiAdapter;
import org.hzero.starter.social.qq.connect.QQConnectionFactory;
import org.hzero.starter.social.qq.connect.QQServiceProvider;
import org.hzero.starter.social.qq.connect.QQTemplate;

/**
 * social 配置
 *
 * @author bojiangzhou 2018/10/17
 */
@Configuration
public class QQSocialBuilder implements SocialConnectionFactoryBuilder {

    @Override
    public String getProviderId() {
        return ProviderEnum.qq.name();
    }

    @Override
    public SocialConnectionFactory buildConnectionFactory(Provider provider) {
        // 获取授权码地址
        final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";
        // 获取令牌地址
        final String URL_GET_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";
        // 获取 openId 的地址
        final String URL_GET_OPEN_ID = "https://graph.qq.com/oauth2.0/me";
        // 获取用户信息的地址
        final String URL_GET_USER_INFO = "https://graph.qq.com/user/get_user_info";

        provider.setAuthorizeUrl(URL_AUTHORIZE);
        provider.setAccessTokenUrl(URL_GET_ACCESS_TOKEN);
        provider.setOpenIdUrl(URL_GET_OPEN_ID);
        provider.setUserInfoUrl(URL_GET_USER_INFO);

        QQApiAdapter apiAdapter = new QQApiAdapter();
        QQTemplate template = new QQTemplate(provider);
        QQServiceProvider serviceProvider = new QQServiceProvider(provider, template);
        return new QQConnectionFactory(provider, serviceProvider, apiAdapter);
    }

}
