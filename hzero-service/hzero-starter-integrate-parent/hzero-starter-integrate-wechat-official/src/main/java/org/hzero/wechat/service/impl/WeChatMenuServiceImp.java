package org.hzero.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.wechat.constant.WechatApi;
import org.hzero.wechat.dto.*;
import org.hzero.wechat.service.WeChatMenuService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

public class WeChatMenuServiceImp implements WeChatMenuService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public DefaultResultDTO createMenu(String accessToken, CreateMenuDTO createMenuDTO) {
        return restTemplate.postForObject(WechatApi.CREATE_MENU_URL + accessToken, new HttpEntity<>(JSON.toJSONString(createMenuDTO), buildHttpHeaders()), DefaultResultDTO.class);

    }

    @Override
    public GetMenuResultDTO getMenu(String accessToken) {
        return restTemplate.getForObject(WechatApi.GET_MENU + accessToken,GetMenuResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteMenu(String accessToken) {
        return restTemplate.getForObject(WechatApi.DELETE_MENU + accessToken, DefaultResultDTO.class);
    }

    @Override
    public CreateAddConditionalMenuResultDTO createAddConditionalMenu(String accessToken,  CreateAddConditionalMenuDTO createAddConditionalMenuDTO) {
        return restTemplate.postForObject(WechatApi.CREATE_ADDCONDITIONAL_MENU_URL + accessToken,new HttpEntity<>(JSON.toJSONString(createAddConditionalMenuDTO),buildHttpHeaders()), CreateAddConditionalMenuResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteAddConditionalMenu(String accessToken, String menuid) {
        return restTemplate.postForObject(WechatApi.DELETE_ADDCONDITIONAL_MENU_URL + accessToken,new HttpEntity<>(JSON.toJSONString(menuid),buildHttpHeaders()), DefaultResultDTO.class);

    }

    @Override
    public TestMatchMenuResultDTO testMatchMenu(String accessToken, String user_id) {
        return restTemplate.postForObject(WechatApi.TEST_MATCH_MENU_URL + accessToken,new HttpEntity<>(JSON.toJSONString(user_id),buildHttpHeaders()), TestMatchMenuResultDTO.class);

    }

    @Override
    public GetMenuConfigurationResultDTO getMenuConfiguration(String accessToken) {
        return restTemplate.getForObject(WechatApi.GET_MENU_CONFIGURATION + accessToken,GetMenuConfigurationResultDTO.class);

    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }


}
