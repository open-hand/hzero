package org.hzero.dd.service.impl;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.hzero.dd.constant.DingUrl;
import org.hzero.dd.dto.GetTicketResultDTO;
import org.hzero.dd.service.DingTicketService;
import org.springframework.web.client.RestTemplate;

public class DingTicketServiceImp implements DingTicketService {
    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public GetTicketResultDTO getTicket(String accessToken, String type) {
        return restTemplate.getForObject(DingUrl.GET_JSAPI_TICKET_URL + "?access_token=" + accessToken + "&type=" + type
                ,GetTicketResultDTO.class);
    }

    @Override
    public String signature(String ticket, String nonceStr, long timeStamp, String url) {
        String plain = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timeStamp
                + "&url=" + url;
        return DigestUtils.sha1Hex(plain);
    }
}
