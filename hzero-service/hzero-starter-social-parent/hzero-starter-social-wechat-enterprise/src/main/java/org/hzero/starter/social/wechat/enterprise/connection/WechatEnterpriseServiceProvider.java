package org.hzero.starter.social.wechat.enterprise.connection;

import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.wechat.enterprise.api.DefaultWechatEnterpriseApi;
import org.hzero.starter.social.wechat.enterprise.api.WechatEnterpriseApi;

/**
 * 企业微信服务提供商
 *
 * @author wanshun.zhang@hand-china.com 2019/11/12
 */
public class WechatEnterpriseServiceProvider extends SocialServiceProvider {

    private Provider provider;

    public WechatEnterpriseServiceProvider(Provider provider, SocialTemplate template) {
        super(provider, template);
        this.provider = provider;
    }

    @Override
    public WechatEnterpriseApi getSocialApi(String accessToken) {
        return new DefaultWechatEnterpriseApi(accessToken, provider);
    }
}
