package org.hzero.starter.social.core.common.api;

/**
 * 用户API接口
 *
 * @author bojiangzhou 2019/08/29
 */
public interface SocialApi {

    /**
     * 获取三方用户信息API
     * 
     * @return SocialUser
     */
    default SocialUser getUser() {
        return null;
    }

    /**
     * 获取三方用户信息API
     * 
     * @param providerUserId 三方用户ID(openId)
     */
    default SocialUser getUser(String providerUserId) {
        return null;
    }

}
