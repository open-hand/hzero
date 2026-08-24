package org.hzero.dd.service;

import org.hzero.dd.dto.TokenDTO;

/**
 * 获取token
 *
 * @author zifeng.ding@hand-china.com 2020/01/13 14:28
 */
public interface DingTokenService {
    /**
     * 获取token且redis缓存
     *
     * @param appId     钉钉应用id
     * @param appSecret 钉钉应用secret
     * @return
     */
    TokenDTO getTokenWithCache(String appId, String appSecret);


    /**
     * 获取token
     *
     * @param appId     钉钉应用id
     * @param appSecret 钉钉应用secret
     * @return
     */
    TokenDTO getToken(String appId, String appSecret);



    /**
     * 从第三方 获取企业微信的token
     *
     * @param authUrl 第三方地址，参数需自己拼接
     * @return
     */
    TokenDTO getTokenFromThirdPart(String authUrl);
}
