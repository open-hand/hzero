package org.hzero.wechat.enterprise.service;

import org.apache.commons.collections4.Get;
import org.hzero.wechat.enterprise.dto.GetTicketResultDTO;

public interface WechatTicketService {

    /**
     * 获取电子发票ticket
     * 获取应用的jsapi_ticket
     */
    GetTicketResultDTO getTicket(String accessToken, String agent_config);

    /**
     * 获取企业的jsapi_ticket
     */
   GetTicketResultDTO getEnterpriseTicket(String accessToken);


    /**
     * 签名算法
     */
    String algorithmSignature(String noncestr, String jsapi_ticket, String timestamp, String url);


}
