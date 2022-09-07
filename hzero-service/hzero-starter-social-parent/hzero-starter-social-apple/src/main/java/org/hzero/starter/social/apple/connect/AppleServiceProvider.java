package org.hzero.starter.social.apple.connect;

import org.hzero.starter.social.apple.api.DefaultAppleApi;
import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-parent
 * @PackageName: org.hzero.starter.social.apple.connect
 * @Date: 2020/4/27
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class AppleServiceProvider extends SocialServiceProvider {

    private Provider provider;

    public AppleServiceProvider(Provider provider, SocialTemplate template) {
        super(provider, template);
        this.provider = provider;
    }

    @Override
    public SocialApi getSocialApi(String accessToken) {
        return new DefaultAppleApi(accessToken,provider);
    }
}
