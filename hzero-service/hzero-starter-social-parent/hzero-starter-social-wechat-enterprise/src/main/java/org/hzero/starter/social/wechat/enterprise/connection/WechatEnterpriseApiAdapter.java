package org.hzero.starter.social.wechat.enterprise.connection;

import org.springframework.social.connect.ConnectionValues;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.wechat.enterprise.api.WechatEnterpriseUser;

/**
 * WechatEnterpriseApi 适配器
 *
 * @author wanshun.zhang@hand-china.com 2019/11/12
 */
public class WechatEnterpriseApiAdapter extends SocialApiAdapter {

    public WechatEnterpriseApiAdapter(){}

    public WechatEnterpriseApiAdapter(String providerUserId) {
        super(providerUserId);
    }
    /**
     * WechatEnterpriseApi 与 Connection 做适配
     * 
     * @param api WechatEnterpriseApi
     * @param values Connection
     */
    @Override
    public void setConnectionValues(SocialApi api, ConnectionValues values) {
        WechatEnterpriseUser user = (WechatEnterpriseUser) api.getUser(getProviderUserId());
        values.setDisplayName(user.getName());
        values.setImageUrl(user.getAvatar());
        values.setProviderUserId(user.getUserid());
    }

}
