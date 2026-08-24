package org.hzero.starter.social.wechat.connection;

import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.wechat.api.DefaultWechatApi;
import org.hzero.starter.social.wechat.api.WechatApi;

/**
 * 微信服务提供商
 *
 * @author bojiangzhou 2018/10/17
 */
public class WechatServiceProvider extends SocialServiceProvider {

    private Provider provider;

    public WechatServiceProvider(Provider provider, SocialTemplate template) {
        super(provider, template);
        this.provider = provider;
    }

    @Override
    public WechatApi getSocialApi(String accessToken) {
        return new DefaultWechatApi(accessToken, provider);
    }
}
