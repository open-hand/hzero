package org.hzero.starter.social.sina.connection;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.sina.api.DefaultSinaApi;

/**
 * 新浪 服务提供商
 *
 * @author liufanghan 2019/09/18 12:48
 */
public class SinaServiceProvider extends SocialServiceProvider {

    private Provider provider;

    public SinaServiceProvider(Provider provider, SocialTemplate template) {
        super(provider, template);
        this.provider = provider;
    }

    @Override
    public SocialApi getSocialApi(String accessToken) {
        return new DefaultSinaApi(accessToken,provider);
    }
}
