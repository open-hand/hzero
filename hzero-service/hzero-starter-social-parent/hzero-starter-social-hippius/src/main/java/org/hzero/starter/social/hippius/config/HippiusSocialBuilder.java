package org.hzero.starter.social.hippius.config;

import org.hzero.starter.social.core.common.configurer.SocialConnectionFactoryBuilder;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.constant.ProviderEnum;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.hippius.connect.HippiusApiAdapter;
import org.hzero.starter.social.hippius.connect.HippiusConnectionFactory;
import org.hzero.starter.social.hippius.connect.HippiusServiceProvider;
import org.hzero.starter.social.hippius.connect.HippiusTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-hippius
 * @PackageName: org.hzero.starter.social.hippius.config
 * @Date: 2020/4/5
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
@Configuration
public class HippiusSocialBuilder implements SocialConnectionFactoryBuilder {


    @Override
    public String getProviderId() {
        return ProviderEnum.hippius.name();
    }

    @Override
    public SocialConnectionFactory buildConnectionFactory(Provider provider) {

        // 获取授权码地址
        final String URL_AUTHORIZE = "https://hippiusgw.hand-china.com/oauth/token/qrconnect";
        // 获取令牌地址
        final String URL_GET_ACCESS_TOKEN = "https://hippiusgw.hand-china.com/oauth/oauth/token";
        // 获取 openId 的地址
        final String URL_GET_OPEN_ID = "https://hippiusgw.hand-china.com/oauth/api/userInfo";
        // 获取用户信息的地址
        final String URL_GET_USER_INFO = "https://hippiusgw.hand-china.com/oauth/api/userInfo";

        provider.setAuthorizeUrl(URL_AUTHORIZE);
        provider.setAccessTokenUrl(URL_GET_ACCESS_TOKEN);
        provider.setOpenIdUrl(URL_GET_OPEN_ID);
        provider.setUserInfoUrl(URL_GET_USER_INFO);

        // 创建适配器
        HippiusApiAdapter apiAdapter;
        apiAdapter = new HippiusApiAdapter();
        // 创建三方模板
        HippiusTemplate template = new HippiusTemplate(provider);
        // 创建服务提供商
        HippiusServiceProvider serviceProvider = new HippiusServiceProvider(provider, template);
        // 创建连接工厂
        return new HippiusConnectionFactory(provider, serviceProvider, apiAdapter);

    }
}
