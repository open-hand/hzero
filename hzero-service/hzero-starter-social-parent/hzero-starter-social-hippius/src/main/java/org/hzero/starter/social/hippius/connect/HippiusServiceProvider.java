package org.hzero.starter.social.hippius.connect;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.hippius.api.DefaultHippiusApi;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-hippius
 * @PackageName: org.hzero.starter.social.hippius.connect
 * @Date: 2020/4/5
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class HippiusServiceProvider extends SocialServiceProvider{


    private Provider provider;

    public HippiusServiceProvider(Provider provider, SocialTemplate template) {
        super(provider, template);
        this.provider = provider;
    }

    @Override
    public SocialApi getSocialApi(String accessToken) {
        return new DefaultHippiusApi(accessToken,provider);
    }
}
