package org.hzero.wechat.service.impl;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.hzero.wechat.constant.WechatApi;
import org.hzero.wechat.dto.*;
import org.hzero.wechat.service.BaseWechatService;
import org.hzero.wechat.util.Sha1Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;


/**
 * @Author J
 * @Date 2019/8/28
 */
@Service
public class BaseWechatServiceImpl implements BaseWechatService {


    protected static final String WX_TOKEN_KEY = "hips:token:wechat-official";

    private static final Logger logger = LoggerFactory.getLogger(BaseWechatServiceImpl.class);

    @Resource(name = "wdRestTemplate")
    RestTemplate restTemplate;
    @Resource(name = "wdRedisTemplate")
    protected RedisTemplate redisTemplate;

    @Override
    public String wechatValid(String signature, String timestamp, String nonce, String echostr, String token) {
        // 需要计算 SHA1 的三个参数
        String[] signArgs = {timestamp, nonce, token};

        // 1. 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(signArgs);

        // 2. 将三个参数字符串拼接成一个字符串进行sha1加密
        String digest = Sha1Utils.encodeSHA1(String.join("", signArgs));

        // 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        if (digest.equalsIgnoreCase(signature)) {
            return echostr;
        } else {
            return "";
        }
    }

    @Override
    public TokenDTO getTokenWithCache(String appId, String appSecret) {
        String key = WX_TOKEN_KEY + ":" + appId + ":" + appSecret;
        ValueOperations<String, TokenDTO> valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) {
            TokenDTO tokenDTO = valueOperations.get(key);
            return tokenDTO;
        }
        TokenDTO tokenDTO = restTemplate.getForObject(WechatApi.GET_TOKEN_URL + "&appid=" + appId + "&secret=" + appSecret, TokenDTO.class);
        if (tokenDTO != null && StringUtils.isNotBlank(tokenDTO.getAccess_token())) {
            long expires_in = tokenDTO.getExpires_in();
            valueOperations.set(key, tokenDTO);
            redisTemplate.expire(key, expires_in, TimeUnit.SECONDS);
        }
        return tokenDTO;
    }


    @Override
    public TokenDTO getToken(String appId, String appSecret) {
        return restTemplate.getForObject(WechatApi.GET_TOKEN_URL + "&appid=" + appId + "&secret=" + appSecret, TokenDTO.class);
    }

    @Override
    public TokenDTO getTokenFromThirdPart(String authUrl) {
        return restTemplate.getForObject(authUrl, TokenDTO.class);
    }

    @Override
    public GetTemplateIdResultDTO getTemplateId(String templateIdShort, String accessToken) {
        return restTemplate.postForObject(WechatApi.GET_TEMPLATE + accessToken, new HttpEntity<>(JSON.toJSONString(new GetTemplateIdDTO().setTemplate_id_short(templateIdShort))), GetTemplateIdResultDTO.class);
    }

    @Override
    public AllTemplatesDTO getAllTemplate(String accessToken) {
        return restTemplate.getForObject(WechatApi.GET_ALL_TEMPLATE + accessToken, AllTemplatesDTO.class);

    }

    @Override
    public DefaultResultDTO deleteTemplateById(String templateId, String accessToken) {
        return restTemplate.postForObject(WechatApi.DEL_TEMPLATE + accessToken, new HttpEntity<>(JSON.toJSONString(new DelTemplateDTO().setTemplate_id(templateId))), DefaultResultDTO.class);
    }

    @Override
    public TemplateSendResultDTO sendTemplateMessage(TemplateSendDTO templateSendDTO, String accessToken) {
        return restTemplate.postForObject(WechatApi.SEND_TEMPLATE + accessToken, new HttpEntity<>(JSON.toJSONString(templateSendDTO)), TemplateSendResultDTO.class);

    }

    @Override
    public List<TemplateSendResultDTO> batchSendTemplateMessage(List<TemplateSendDTO> templateSendDTOs, String accessToken) {
        return templateSendDTOs.stream().map(j -> restTemplate.postForObject(WechatApi.SEND_TEMPLATE + accessToken, new HttpEntity<>(JSON.toJSONString(j)), TemplateSendResultDTO.class)).collect(Collectors.toList());
    }

    @Override
    public JsTicketDTO getjsapiTicketWithCache(String accessToken, String appId) {
        String key = WX_TOKEN_KEY+":jsapi-ticket:" + appId + ":" + accessToken;
        ValueOperations<String, JsTicketDTO> valueOperations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) {
            JsTicketDTO jsTicketDTO = valueOperations.get(key);
            return jsTicketDTO;
        }
        JsTicketDTO jsTicketDTO = restTemplate.getForObject( WechatApi.GET_JS_API_TICKET_URL+ accessToken + "&type=jsapi", JsTicketDTO.class);
        if (jsTicketDTO != null && StringUtils.isNotBlank(jsTicketDTO.getTicket())) {
            long expires_in = jsTicketDTO.getExpires_in();
            valueOperations.set(key, jsTicketDTO);
            redisTemplate.expire(key, expires_in, TimeUnit.SECONDS);
        }
        return jsTicketDTO;
    }

}
