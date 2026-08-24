package org.hzero.starter.social.hippius.connect;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.hippius.api.HippiusUser;
import org.springframework.social.connect.ConnectionValues;

/**
 * API 适配器
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-hippius
 * @PackageName: org.hzero.starter.social.hippius.connect
 * @Date: 2020/4/5
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class HippiusApiAdapter extends SocialApiAdapter {

    @Override
    public void setConnectionValues(SocialApi api, ConnectionValues values) {

        HippiusUser user = (HippiusUser) api.getUser();
        values.setDisplayName(user.getNickname());
        values.setImageUrl(user.getHeadimgurl());
        values.setProviderUserId(user.getOpenId());
    }
}
