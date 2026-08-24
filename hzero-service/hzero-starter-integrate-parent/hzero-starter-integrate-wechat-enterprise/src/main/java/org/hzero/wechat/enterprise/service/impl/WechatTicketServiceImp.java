package org.hzero.wechat.enterprise.service.impl;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.hzero.wechat.enterprise.constant.WechatEnterpriseUrl;
import org.hzero.wechat.enterprise.dto.GetTicketResultDTO;
import org.hzero.wechat.enterprise.service.WechatTicketService;
import org.springframework.web.client.RestTemplate;

public class WechatTicketServiceImp implements WechatTicketService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public GetTicketResultDTO getTicket(String accessToken, String agent_config) {
       return restTemplate.getForObject(WechatEnterpriseUrl.GET_TICKET_URL +accessToken+ "&type"+ agent_config, GetTicketResultDTO.class);
    }

    @Override
    public GetTicketResultDTO getEnterpriseTicket(String accessToken) {
       return restTemplate.getForObject(WechatEnterpriseUrl.GET_JS_API_TICKET_URL +accessToken, GetTicketResultDTO.class);
    }

    @Override
    public String algorithmSignature(String noncestr, String jsapi_ticket, String timestamp, String url) {
       String string =  "jsapi_ticket=" + jsapi_ticket+ "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url ;
       return DigestUtils.sha1Hex(string);
    }
}
