package org.hzero.starter.social.apple.config;

import org.hzero.starter.social.apple.connect.AppleApiAdapter;
import org.hzero.starter.social.apple.connect.AppleConnectionFactory;
import org.hzero.starter.social.apple.connect.AppleServiceProvider;
import org.hzero.starter.social.apple.connect.AppleTemplate;
import org.hzero.starter.social.core.common.configurer.SocialConnectionFactoryBuilder;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.constant.ProviderEnum;
import org.hzero.starter.social.core.provider.Provider;
import org.springframework.context.annotation.Configuration;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-parent
 * @PackageName: org.hzero.starter.social.apple.config
 * @Date: 2020/4/27
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
@Configuration
public class AppleSocialBuilder implements SocialConnectionFactoryBuilder {

    @Override
    public String getProviderId() {
        return ProviderEnum.apple.name();
    }

    @Override
    public SocialConnectionFactory buildConnectionFactory(Provider provider) {
        // 获取授权码地址
        final String URL_AUTHORIZE = "https://appleid.apple.com/auth/authorize";
        // 获取令牌地址
        final String URL_GET_ACCESS_TOKEN = "https://appleid.apple.com/auth/token";
        // 获取 openId 的地址
        final String URL_GET_OPEN_ID = "https://appleid.apple.com/auth/keys";
        // 获取用户信息的地址
        final String URL_GET_USER_INFO = "https://appleid.apple.com/auth/keys";

        provider.setAuthorizeUrl(URL_AUTHORIZE);
        provider.setAccessTokenUrl(URL_GET_ACCESS_TOKEN);
        provider.setOpenIdUrl(URL_GET_OPEN_ID);
        provider.setUserInfoUrl(URL_GET_USER_INFO);
        // 创建适配器
        AppleApiAdapter apiAdapter = new AppleApiAdapter();
        // 创建三方模板
        AppleTemplate template = new AppleTemplate(provider);
        // 创建服务提供商
        AppleServiceProvider serviceProvider = new AppleServiceProvider(provider, template);
        return new AppleConnectionFactory(provider,serviceProvider,apiAdapter);
    }
}
