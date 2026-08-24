package org.hzero.starter.social.wechat.enterprise.config;

import org.hzero.starter.social.wechat.enterprise.connection.WechatEnterpriseApiAdapter;
import org.hzero.starter.social.wechat.enterprise.connection.WechatEnterpriseTemplate;
import org.springframework.context.annotation.Configuration;

import org.hzero.starter.social.core.common.configurer.SocialConnectionFactoryBuilder;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.constant.ProviderEnum;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.wechat.enterprise.connection.WechatEnterpriseConnectionFactory;
import org.hzero.starter.social.wechat.enterprise.connection.WechatEnterpriseServiceProvider;

/**
 * social 配置
 *
 * @author wanshun.zhang@hand-china.com 2019/11/12
 */
@Configuration
public class WechatEnterpriseSocialBuilder implements SocialConnectionFactoryBuilder {

    @Override
    public String getProviderId() {
        return ProviderEnum.wechat_enterprise.name();
    }

    @Override
    public SocialConnectionFactory buildConnectionFactory(Provider provider) {
        // 获取授权码地址
        final String URL_AUTHORIZE = "https://open.work.weixin.qq.com/wwopen/sso/qrConnect";
        // 获取令牌地址
        final String URL_GET_ACCESS_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        // 获取 openId 的地址
        final String URL_GET_OPEN_ID = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";
        // 获取用户信息的地址
        final String URL_GET_USER_INFO = "https://qyapi.weixin.qq.com/cgi-bin/user/get";

        provider.setAuthorizeUrl(URL_AUTHORIZE);
        provider.setAccessTokenUrl(URL_GET_ACCESS_TOKEN);
        provider.setOpenIdUrl(URL_GET_OPEN_ID);
        provider.setUserInfoUrl(URL_GET_USER_INFO);

        WechatEnterpriseApiAdapter apiAdapter = new WechatEnterpriseApiAdapter();
        WechatEnterpriseTemplate template = new WechatEnterpriseTemplate(provider);
        WechatEnterpriseServiceProvider serviceProvider = new WechatEnterpriseServiceProvider(provider, template);
        return new WechatEnterpriseConnectionFactory(provider, serviceProvider, apiAdapter);
    }

}
