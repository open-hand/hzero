package org.hzero.starter.social.sina.connection;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.api.SocialUser;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.sina.api.SinaUser;
import org.springframework.social.connect.ConnectionValues;

/**
 * SinaApi 适配器
 *
 * @author liufanghan 2019/09/18 12:23
 */
public class SinaApiAdapter extends SocialApiAdapter {

    public SinaApiAdapter() {
    }

    public SinaApiAdapter(String providerUserId) {
        super(providerUserId);
    }

    /**
     * SinaApi 与 Connection 做适配
     *
     * @param api SinaApi
     * @param values Connection
     */
    @Override
    public void setConnectionValues(SocialApi api, ConnectionValues values) {
        //调用三方接口获取用户信息
        SinaUser user = (SinaUser)api.getUser(getProviderUserId());
        //设置昵称 头像
        values.setDisplayName(user.getScreen_name());
        values.setImageUrl(user.getProfile_image_url());
        //设置UID(openId)
        values.setProviderUserId(user.getIdstr());
    }
}
