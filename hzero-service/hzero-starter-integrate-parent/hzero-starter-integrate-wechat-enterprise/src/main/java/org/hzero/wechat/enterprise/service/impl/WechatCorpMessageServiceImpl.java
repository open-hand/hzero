package org.hzero.wechat.enterprise.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.wechat.enterprise.constant.WechatEnterpriseUrl;
import org.hzero.wechat.enterprise.dto.*;
import org.hzero.wechat.enterprise.service.WechatCorpMessageService;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class WechatCorpMessageServiceImpl implements WechatCorpMessageService {

    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public MessageSendResultDTO sendTextMsg(TextMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);
    }

    @Override
    public MessageSendResultDTO sendImageMsg(ImageMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);
    }

    @Override
    public MessageSendResultDTO sendVoiceMsg(VoiceMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);

    }

    @Override
    public MessageSendResultDTO sendVideoMsg(VideoMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);

    }

    @Override
    public MessageSendResultDTO sendFileMsg(FileMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);

    }

    @Override
    public MessageSendResultDTO sendTextCardMsg(TextCardMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);

    }

    @Override
    public MessageSendResultDTO sendNewsMsg(NewsMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);

    }

    @Override
    public MessageSendResultDTO sendMarkdownMsg(MarkdownMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);

    }

    @Override
    public MessageSendResultDTO sendMiniProgramNoticeMsg(MiniProgramNoticeMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);

    }

    @Override
    public MessageSendResultDTO sendTaskCardMsg(TaskCardMessageDTO messageDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(messageDTO)), MessageSendResultDTO.class);
    }

    @Override
    public AppChatCreateResultDTO createAppChat(AppChatCreateResultDTO appChatCreateResultDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.CREARE_APP_CHAT_URL + accessToken, new HttpEntity<>(JSON.toJSONString(appChatCreateResultDTO)), AppChatCreateResultDTO.class);
    }

    @Override
    public DefaultResultDTO updateAppChat(AppChatUpdateDTO appChatUpdateDTO, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.UPDATE_APP_CHAT_URL + accessToken, new HttpEntity<>(JSON.toJSONString(appChatUpdateDTO)), DefaultResultDTO.class);

    }

    @Override
    public AppChatDTO getAppChat(String chatid, String accessToken) {
        return restTemplate.getForObject(WechatEnterpriseUrl.GET_APP_CHAT_URL +accessToken+"&chatid="+chatid, AppChatDTO.class);
    }

    @Override
    public DefaultResultDTO sendAppChat(Object object, String accessToken) {
        return restTemplate.postForObject(WechatEnterpriseUrl.SEND_APP_CHAT_URL + accessToken, new HttpEntity<>(JSON.toJSONString(object)), DefaultResultDTO.class);

    }
}
