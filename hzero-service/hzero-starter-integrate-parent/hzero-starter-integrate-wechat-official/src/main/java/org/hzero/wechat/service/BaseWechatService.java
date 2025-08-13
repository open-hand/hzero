package org.hzero.wechat.service;


import org.apache.commons.lang3.StringUtils;
import org.hzero.wechat.dto.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author J
 * @Date 2019/8/28
 */

public interface BaseWechatService {


    /**
     * 响应微信的服务器配置
     *
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @param token     公众号配置的token
     * @return 根据情况返回 echostr
     */
    String wechatValid(String signature, String timestamp, String nonce, String echostr, String token);


    /**
     * 获取token且redis缓存
     *
     * @param appId     微信appId
     * @param appSecret 微信appSecret
     * @return
     */
    TokenDTO getTokenWithCache(String appId, String appSecret);

    /**
     * 获取微信token
     *
     * @param appId     微信appId
     * @param appSecret 微信appSecret
     * @return
     */
    TokenDTO getToken(String appId, String appSecret);


    /**
     * 从第三方 获取微信的token
     * @param authUrl 第三方地址，参数需自己拼接
     * @return
     */
    TokenDTO getTokenFromThirdPart(String authUrl);



    /**
     * 通过模版编号获得模板ID
     *
     * @param templateIdShort 模版编号
     * @param accessToken  微信接口调用凭证
     * @return
     */
    GetTemplateIdResultDTO getTemplateId(String templateIdShort,String accessToken);


    /**
     * 获取所有模版
     *
     * @param accessToken  微信接口调用凭证
     * @return
     */
    AllTemplatesDTO getAllTemplate(String accessToken);


    /**
     * 通过模版id删除模版
     *
     * @param templateId 模版id
     * @param accessToken  微信接口调用凭证
     * @return
     */
    DefaultResultDTO deleteTemplateById(String templateId,String accessToken);


    /**
     * 推送模版消息
     *
     * @param templateSendDTO 模版消息推送体
     * @param accessToken  微信接口调用凭证
     * @return
     */
    TemplateSendResultDTO sendTemplateMessage(TemplateSendDTO templateSendDTO,String accessToken);


    /**
     * 批量发送模版消息
     *
     * @param templateSendDTOs 模版消息推送体
     * @param accessToken  微信接口调用凭证
     * @return
     */
    List<TemplateSendResultDTO> batchSendTemplateMessage(List<TemplateSendDTO> templateSendDTOs, String accessToken);


    /**
     * 获取 微信 jsApiTicket
     * @param accessToken
     * @param appId
     * @return
     */
    JsTicketDTO getjsapiTicketWithCache(String accessToken, String appId);
}
