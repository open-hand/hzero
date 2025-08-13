package org.hzero.wechat.enterprise.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.wechat.enterprise.constant.WechatEnterpriseUrl;
import org.hzero.wechat.enterprise.dto.*;
import org.hzero.wechat.enterprise.service.WechatCorpAgentManageService;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class WechatCorpAgentManageServiceImpl implements WechatCorpAgentManageService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;


    @Override
    public AgentDTO getAgentByID(String agentId, String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_AGENT_BY_ID_URL + accessToken + "&agentid=" + agentId, AgentDTO.class);
    }

    @Override
    public AgentListDTO getAgentList(String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_ALL_AGENTS_URL + accessToken, AgentListDTO.class);
    }

    @Override
    public DefaultResultDTO setAgent(SetAgentDTO setAgentDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SET_AGENT_URL + accessToken, new HttpEntity<>(JSON.toJSONString(setAgentDTO)), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO createMenu(String accessToken, String agentid, MenuDTO menuDTO) {
        return restTemplate.postForObject(WechatEnterpriseUrl.CREATE_MENU_URL + accessToken + "&agentid" + agentid, new HttpEntity<>(JSON.toJSONString(menuDTO)), DefaultResultDTO.class);

    }

    @Override
    public MenuDTO getMenu(String accessToken, String agentid) {
       return restTemplate.getForObject(WechatEnterpriseUrl.GET_MENU_LIST_URL + accessToken + "&agentid" + agentid , MenuDTO.class);

    }

    @Override
    public DefaultResultDTO deleteMenu(String accessToken, String agentid) {
        return restTemplate.getForObject(WechatEnterpriseUrl.DELETE_MENU_URL + accessToken + "&agentid" + agentid , DefaultResultDTO.class);

    }
}
