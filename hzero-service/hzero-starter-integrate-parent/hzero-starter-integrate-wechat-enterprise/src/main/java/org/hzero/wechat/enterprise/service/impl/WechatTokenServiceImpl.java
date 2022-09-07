package org.hzero.wechat.enterprise.service.impl;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.hzero.wechat.enterprise.constant.WechatEnterpriseUrl;
import org.hzero.wechat.enterprise.dto.TokenDTO;
import org.hzero.wechat.enterprise.enums.SecretTypeEnum;
import org.hzero.wechat.enterprise.service.WechatTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author zifeng.ding@hand-china.com 2020/01/13 14:40
 */
public class WechatTokenServiceImpl implements WechatTokenService {

    protected static final String WX_TOKEN_KEY = "hips:token:wechat-enterprise";

    @Resource(name = "wdRedisTemplate")
    protected RedisTemplate redisTemplate;
    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public TokenDTO getTokenWithCache(String corpId, String corpSecret, SecretTypeEnum secretTypeEnum) {
        String key = WX_TOKEN_KEY + ":" + corpId + ":" + corpSecret;
        if (secretTypeEnum.equals(SecretTypeEnum.APP)) {
            key = key + ":" + SecretTypeEnum.APP.getCode();
        } else if (secretTypeEnum.equals(SecretTypeEnum.CORP)) {
            key = key + ":" + SecretTypeEnum.CORP.getCode();
        }
        ValueOperations<String, TokenDTO> valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) {
            TokenDTO tokenDTO = valueOperations.get(key);
            return tokenDTO;
        }
        TokenDTO tokenDTO = restTemplate.getForObject(
                WechatEnterpriseUrl.GET_TOKEN_URL + "?corpid=" + corpId + "&corpsecret=" + corpSecret,
                TokenDTO.class);
        if (tokenDTO != null && StringUtils.isNotBlank(tokenDTO.getAccess_token())) {
            int expires_in = tokenDTO.getExpires_in();
            valueOperations.set(key, tokenDTO);
            redisTemplate.expire(key, expires_in, TimeUnit.SECONDS);
        }
        return tokenDTO;
    }

    @Override
    public TokenDTO getToken(String corpId, String corpSecret) {
        return restTemplate.getForObject(
                WechatEnterpriseUrl.GET_TOKEN_URL + "?corpid=" + corpId + "&corpsecret=" + corpSecret,
                TokenDTO.class);
    }

    @Override
    public TokenDTO getTokenFromThirdPart(String authUrl) {
        return restTemplate.getForObject(authUrl, TokenDTO.class);
    }
}
