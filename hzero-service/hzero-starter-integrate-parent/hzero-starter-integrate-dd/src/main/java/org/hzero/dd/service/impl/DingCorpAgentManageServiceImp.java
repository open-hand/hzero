package org.hzero.dd.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.dd.constant.DingUrl;
import org.hzero.dd.dto.*;
import org.hzero.dd.service.DingCorpAgentManageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
@Service
public class DingCorpAgentManageServiceImp  implements DingCorpAgentManageService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;


    @Override
    public GetMicroappListDTO getAllMicroappList(String accessToken) {
        return restTemplate.postForObject(DingUrl.GET_ALL_MICROAPP_LIST_URL + "?access_token=" + accessToken ,new HttpEntity<>(buildHttpHeaders()), GetMicroappListDTO.class);

    }

    @Override
    public GetMicroappListDTO getMicroappListByUserId(String accessToken, String userid) {
        return restTemplate.getForObject(DingUrl.GET_MICROAPP_LIST_URL + "?access_token=" + accessToken +"&userid" + userid, GetMicroappListDTO.class);
    }

    @Override
    public GetMicroappVisibleDTO getMicroappVisible(String accessToken, Long agentId) {
        return restTemplate.postForObject(DingUrl.GET_MICROAPP_VISIBLE_URL + "?access_token=" + accessToken ,new HttpEntity<>("{ \"agentId\": " + JSON.toJSONString(agentId) + " }", buildHttpHeaders()), GetMicroappVisibleDTO.class);
    }

    @Override
    public DefaultResultDTO setMicroappVisible(String accessToken, SetMicroappVisibleDTO setMicroappVisibleDTO) {
        return restTemplate.postForObject(DingUrl.SET_MICROAPP_VISIBLE_URL + "?access_token=" + accessToken ,new HttpEntity<>(setMicroappVisibleDTO, buildHttpHeaders()), DefaultResultDTO.class);

    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
