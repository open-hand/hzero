package org.hzero.starter.social.apple.connect;

import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.provider.Provider;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-parent
 * @PackageName: org.hzero.starter.social.apple.connect
 * @Date: 2020/4/27
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class AppleConnectionFactory extends SocialConnectionFactory {

    public AppleConnectionFactory(Provider provider, SocialServiceProvider serviceProvider, SocialApiAdapter apiAdapter) {
        super(provider, serviceProvider, apiAdapter);
        setScope(provider.getScope());
    }
}
