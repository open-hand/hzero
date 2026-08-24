package org.hzero.starter.social.hippius.connect;

import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.core.common.connect.SocialConnectionFactory;
import org.hzero.starter.social.core.common.connect.SocialServiceProvider;
import org.hzero.starter.social.core.provider.Provider;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-hippius
 * @PackageName: org.hzero.starter.social.hippius.connect
 * @Date: 2020/4/5
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class HippiusConnectionFactory extends SocialConnectionFactory {

    public HippiusConnectionFactory(Provider provider, SocialServiceProvider serviceProvider, SocialApiAdapter apiAdapter) {
        super(provider, serviceProvider, apiAdapter);
        setScope(provider.getScope());
    }
}
