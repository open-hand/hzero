package org.hzero.starter.social.wechat.connection;

import org.springframework.social.connect.ConnectionValues;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.wechat.api.WechatUser;

/**
 * WechatApi 适配器
 *
 * @author bojiangzhou 2018/10/17
 */
public class WechatApiAdapter extends SocialApiAdapter {

    public WechatApiAdapter() {}

    public WechatApiAdapter(String providerUserId) {
        super(providerUserId);
    }

    /**
     * WechatApi 与 Connection 做适配
     * 
     * @param api WechatApi
     * @param values Connection
     */
    @Override
    public void setConnectionValues(SocialApi api, ConnectionValues values) {
        WechatUser user = (WechatUser) api.getUser(getProviderUserId());

        values.setDisplayName(user.getNickname());
        values.setImageUrl(user.getHeadimgurl());
        values.setProviderUserId(user.getOpenid());
        values.setProviderUnionId(user.getUnionid());
    }

}
