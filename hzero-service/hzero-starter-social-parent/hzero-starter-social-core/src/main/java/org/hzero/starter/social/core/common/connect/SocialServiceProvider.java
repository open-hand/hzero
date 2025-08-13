package org.hzero.starter.social.core.common.connect;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.provider.Provider;

/**
 * ServiceProvider
 *
 * @author bojiangzhou 2019/08/29
 */
public abstract class SocialServiceProvider extends AbstractOAuth2ServiceProvider<SocialApi> {

    public SocialServiceProvider(Provider provider, SocialTemplate template) {
        super(template);
    }

    @Override
    public SocialApi getApi(String accessToken) {
        return getSocialApi(accessToken);
    }

    public abstract SocialApi getSocialApi(String accessToken);

}
