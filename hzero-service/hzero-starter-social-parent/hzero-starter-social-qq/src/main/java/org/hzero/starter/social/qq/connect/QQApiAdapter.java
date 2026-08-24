package org.hzero.starter.social.qq.connect;

import org.springframework.social.connect.ConnectionValues;

import org.hzero.starter.social.core.common.api.SocialApi;
import org.hzero.starter.social.core.common.connect.SocialApiAdapter;
import org.hzero.starter.social.qq.api.QQUser;

/**
 * QQApi 适配器
 *
 * @author bojiangzhou 2018/10/17
 */
public class QQApiAdapter extends SocialApiAdapter {

    /**
     * QQApi 与 Connection 做适配
     * @param api QQApi
     * @param values Connection
     */
    @Override
    public void setConnectionValues(SocialApi api, ConnectionValues values) {
        QQUser user = (QQUser) api.getUser();

        values.setDisplayName(user.getNickname());
        values.setImageUrl(user.getFigureurl());
        values.setProviderUserId(user.getOpenId());
        values.setProviderUnionId(user.getUnionId());
    }

}
