package org.hzero.dd.service;

import org.hzero.dd.dto.GetTicketResultDTO;

public interface DingTicketService {
    /**
     * 获取jsapi_ticket
     * @param accessToken
     * @param type
     * @return
     */
    GetTicketResultDTO getTicket(String accessToken, String type);

    /**
     * 计算签名信息
     * @param ticket
     * @param nonceStr
     * @param timeStamp
     * @param url
     * @return
     */
    String signature(String ticket, String nonceStr, long timeStamp, String url);
}
