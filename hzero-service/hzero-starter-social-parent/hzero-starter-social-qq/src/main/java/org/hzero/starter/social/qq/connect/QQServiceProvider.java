package org.hzero.starter.social.qq.connect;

import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.qq.api.DefaultQQApi;
import org.hzero.starter.social.qq.api.QQApi;

/**
 * QQ 服务提供商
 *
 * @author bojiangzhou 2018/10/17
 */
public class QQServiceProvider extends SocialServiceProvider {

    private Provider provider;

    public QQServiceProvider(Provider provider, SocialTemplate template) {
        super(provider, template);
        this.provider = provider;
    }

    @Override
    public QQApi getSocialApi(String accessToken) {
        return new DefaultQQApi(accessToken, provider);
    }
}
