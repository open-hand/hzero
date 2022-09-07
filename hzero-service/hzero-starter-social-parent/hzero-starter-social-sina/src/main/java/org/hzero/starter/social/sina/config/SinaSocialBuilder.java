package org.hzero.starter.social.sina.config;

import org.springframework.context.annotation.Configuration;

import org.hzero.starter.social.core.common.configurer.SocialConnectionFactoryBuilder;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.constant.ProviderEnum;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.sina.connection.SinaApiAdapter;
import org.hzero.starter.social.sina.connection.SinaConnectionFactory;
import org.hzero.starter.social.sina.connection.SinaServiceProvider;
import org.hzero.starter.social.sina.connection.SinaTemplate;

/**
 * social 配置
 *
 * @author liufanghan 2019/09/18 13:27
 */
@Configuration
public class SinaSocialBuilder implements SocialConnectionFactoryBuilder {

    @Override
    public String getProviderId() {
        return ProviderEnum.sina.name();
    }

    @Override
    public SocialConnectionFactory buildConnectionFactory(Provider provider) {
        // 获取授权码地址 需要参数: client_id->AppKey redirect_uri->回调地址
        final String URL_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";
        // 获取令牌地址 需要参数：client_id->AppKey client_secret->AppSecret grant_type->请求的类型，填写authorization_code
        final String URL_GET_ACCESS_TOKEN = "https://api.weibo.com/oauth2/access_token";
        // 获取 openId 的地址 需要参数：access_token
        final String URL_GET_OPEN_ID = "https://api.weibo.com/oauth2/get_token_info";
        // 获取用户信息的地址 需要参数：access_token uid/screen_name
        final String URL_GET_USER_INFO = "https://api.weibo.com/2/users/show.json";

        provider.setAuthorizeUrl(URL_AUTHORIZE);
        provider.setAccessTokenUrl(URL_GET_ACCESS_TOKEN);
        provider.setOpenIdUrl(URL_GET_OPEN_ID);
        provider.setUserInfoUrl(URL_GET_USER_INFO);
        // 创建适配器
        SinaApiAdapter apiAdapter = new SinaApiAdapter();
        // 创建三方模板
        SinaTemplate template = new SinaTemplate(provider);
        // 创建服务提供商
        SinaServiceProvider serviceProvider = new SinaServiceProvider(provider, template);
        // 创建连接工厂
        return new SinaConnectionFactory(provider, serviceProvider, apiAdapter);
    }
}
