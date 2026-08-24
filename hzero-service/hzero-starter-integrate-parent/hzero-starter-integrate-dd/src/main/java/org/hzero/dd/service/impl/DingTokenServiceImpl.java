package org.hzero.dd.service.impl;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import org.hzero.dd.constant.DingErrorCode;
import org.hzero.dd.constant.DingUrl;
import org.hzero.dd.dto.TokenDTO;
import org.hzero.dd.service.DingTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author zifeng.ding@hand-china.com 2020/01/13 14:29
 */
@Service
public class DingTokenServiceImpl implements DingTokenService {

    protected static final String DD_TOKEN_KEY = "hips:token:dd";

    @Resource(name = "wdRedisTemplate")
    protected RedisTemplate redisTemplate;
    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public TokenDTO getTokenWithCache(String appId, String appSecret) {
        String key = DD_TOKEN_KEY + ":" + appId + ":" + appSecret;
        ValueOperations<String, TokenDTO> valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) {
            TokenDTO tokenDTO = valueOperations.get(key);
            return tokenDTO;
        }
        TokenDTO tokenDTO = restTemplate.getForObject(
                DingUrl.GET_TOKEN_URL + "?appkey=" + appId + "&appsecret=" + appSecret, TokenDTO.class);
        if (DingErrorCode.SUCCESS == tokenDTO.getErrcode()) {
            int expires_in = tokenDTO.getExpires_in();
            valueOperations.set(key, tokenDTO);
            redisTemplate.expire(key, expires_in, TimeUnit.SECONDS);
        }
        return tokenDTO;
    }

    @Override
    public TokenDTO getToken(String appId, String appSecret) {
        return restTemplate.getForObject(DingUrl.GET_TOKEN_URL + "?appkey=" + appId + "&appsecret=" + appSecret,
                TokenDTO.class);
    }

    @Override
    public TokenDTO getTokenFromThirdPart(String authUrl) {
        return restTemplate.getForObject(authUrl, TokenDTO.class);
    }

}
