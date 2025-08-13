package org.hzero.starter.social.apple.connect;

import org.hzero.starter.social.apple.api.AppleUser;
import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.springframework.social.connect.ConnectionValues;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-parent
 * @PackageName: org.hzero.starter.social.apple.connect
 * @Date: 2020/4/27
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class AppleApiAdapter extends SocialApiAdapter {

    public AppleApiAdapter(){}


    @Override
    public void setConnectionValues(SocialApi api, ConnectionValues values) {

        AppleUser user = (AppleUser) api.getUser();
        values.setDisplayName(user.getUserName());
        values.setProviderUserId(user.getOpenId());
    }
}
