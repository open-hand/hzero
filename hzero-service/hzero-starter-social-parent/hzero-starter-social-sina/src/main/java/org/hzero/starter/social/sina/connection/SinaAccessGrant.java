package org.hzero.starter.social.sina.connection;

import org.springframework.social.oauth2.AccessGrant;

/**
 * 获取Sina令牌时，同时将用户 uid(openId) 返回来了，所以需要定制化 AccessGrant，封装 uid(openId)
 *
 * @author liufanghan 2018/10/28
 */
public class SinaAccessGrant extends AccessGrant {
    private static final long serialVersionUID = 6188192404582815582L;

    /**
     * 新浪 openId
     */
    private String openId;

    public SinaAccessGrant(String accessToken, String openId) {
        super(accessToken);
        this.openId = openId;
    }

    public SinaAccessGrant(String accessToken, Long expiresIn, String openId) {
        super(accessToken,null,null,expiresIn);
        this.openId = openId;
    }

    public String getOpenId() {
        return openId;
    }

}
