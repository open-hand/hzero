package org.hzero.wechat.enterprise.service;

import org.hzero.wechat.enterprise.dto.TokenDTO;
import org.hzero.wechat.enterprise.enums.SecretTypeEnum;

/**
 * 获取token
 *
 * @author zifeng.ding@hand-china.com 2020/01/13 14:37
 */
public interface WechatTokenService {
    /**
     * 获取token且redis缓存
     * @param corpId 微信corpId
     * @param corpSecret 微信corpSecret
     * @param secretTypeEnum 微信授权类型
     * @return
     */
    TokenDTO getTokenWithCache(String corpId, String corpSecret, SecretTypeEnum secretTypeEnum);


    /**
     * 获取token
     * @param corpId 微信corpId
     * @param corpSecret 微信corpSecret
     * @return
     */
    TokenDTO getToken(String corpId, String corpSecret);


    /**
     * 从第三方 获取企业微信的token
     * @param authUrl 第三方地址，参数需自己拼接
     * @return
     */
    TokenDTO getTokenFromThirdPart(String authUrl);
}
