package org.hzero.starter.social.wechat.connection;

import org.springframework.social.oauth2.AccessGrant;

/**
 * 获取微信令牌时，同时将用户 openId 返回来了，所以需要定制化 AccessGrant，封装 openId
 *
 * @author bojiangzhou 2018/10/28
 */
public class WechatAccessGrant extends AccessGrant {
    private static final long serialVersionUID = 6188192404582815582L;

    /**
     * 微信 openId
     */
    private String openId;
    private String unionId;

    public WechatAccessGrant(String accessToken, String openId) {
        super(accessToken);
        this.openId = openId;
    }

    public WechatAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn, String openId, String unionId) {
        super(accessToken, scope, refreshToken, expiresIn);
        this.openId = openId;
        this.unionId = unionId;
    }

    public String getOpenId() {
        return openId;
    }

    public String getUnionId() {
        return unionId;
    }
}
