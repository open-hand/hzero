package org.hzero.dd.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.dd.constant.DingUrl;
import org.hzero.dd.dto.*;
import org.hzero.dd.service.DingCorpMessageService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class DingCorpMessageServiceImp implements DingCorpMessageService {


    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public SendWorkMessageResultDTO sendWorkTextMessage(String accessToken, SendWorkTextMessageDTO sendWorkTextMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token=" + accessToken, new HttpEntity<>(JSON.toJSONString(sendWorkTextMessageDTO), buildHttpHeaders()),  SendWorkMessageResultDTO.class);
    }

    @Override
    public SendWorkMessageResultDTO sendWorkImageMessage(String accessToken, SendWorkImageMessageDTO sendWorkImageMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkImageMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);
    }

    @Override
    public SendWorkMessageResultDTO sendWorkVoiceMessage(String accessToken, SendWorkVoiceMessageDTO sendWorkVoiceMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkVoiceMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);
    }

    @Override
    public SendWorkMessageResultDTO sendWorkFileMessage(String accessToken, SendWorkFileMessageDTO sendWorkFileMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkFileMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);
    }

    @Override
    public SendWorkMessageResultDTO sendWorkLinkMessage(String accessToken, SendWorkLinkMessageDTO sendWorkLinkMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token"+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkLinkMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);
    }

    @Override
    public SendWorkMessageResultDTO sendWorkOAMessage(String accessToken, SendWorkOAMessageDTO sendWorkOAMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkOAMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);
    }

    @Override
    public SendWorkMessageResultDTO sendWorkMarkDownMessage(String accessToken, SendWorkMarkDownMessageDTO sendWorkMarkDownMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkMarkDownMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);
    }

    @Override
    public SendWorkMessageResultDTO sendWorkWholeCardMessage(String accessToken, SendWorkWholeCardMessageDTO sendWorkWholeCardMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkWholeCardMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);

    }

    @Override
    public SendWorkMessageResultDTO sendWorkIndependentCardMessage(String accessToken, SendWorkIndependentCardMessageDTO sendWorkIndependentCardMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_WORK_MESSAGE_URL+"?="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWorkIndependentCardMessageDTO), buildHttpHeaders()), SendWorkMessageResultDTO.class);
    }

    @Override
    public WorkProgressMessageResultDTO getWorkProgressMessage(String accessToken, WorkProgressAndResultMessageDTO workProgressAndResultMessageDTO) {
        return  restTemplate.postForObject(DingUrl.GET_WORK_PROGRESS_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(workProgressAndResultMessageDTO), buildHttpHeaders()), WorkProgressMessageResultDTO.class);
    }

    @Override
    public WorkResultMessageDTO getWorkResultMessage(String accessToken, WorkProgressAndResultMessageDTO workProgressAndResultMessageDTO) {
        return  restTemplate.postForObject(DingUrl.GET_WORK_RESULT_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(workProgressAndResultMessageDTO), buildHttpHeaders()), WorkResultMessageDTO.class);
    }

    @Override
    public DefaultResultDTO getWorkMessageRecall(String accessToken, WorkMessageRecallDTO workMessageRecallDTO) {
        return  restTemplate.postForObject(DingUrl.WORK_MESSAGE_RECALL_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(workMessageRecallDTO), buildHttpHeaders()), WorkResultMessageDTO.class);

    }

    @Override
    public SendGroupMessageResultDTO sendGroupTextMessage(String accessToken, SendGroupTextMessageDTO sendGroupTextMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupTextMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);
    }

    @Override
    public SendGroupMessageResultDTO sendGroupImageMessage(String accessToken, SendGroupImageMessageDTO sendGroupImageMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupImageMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);

    }

    @Override
    public SendGroupMessageResultDTO sendGroupVoiceMessage(String accessToken, SendGroupVoiceMessageDTO sendGroupVoiceMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupVoiceMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);

    }

    @Override
    public SendGroupMessageResultDTO sendGroupFileMessage(String accessToken, SendGroupFileMessageDTO sendGroupFileMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupFileMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);

    }

    @Override
    public SendGroupMessageResultDTO sendGroupLinkMessage(String accessToken, SendGroupLinkMessageDTO sendGroupLinkMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupLinkMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);

    }

    @Override
    public SendGroupMessageResultDTO sendGroupOAMessage(String accessToken, SendGroupOAMessageDTO sendGroupOAMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupOAMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);

    }

    @Override
    public SendGroupMessageResultDTO sendGroupMarkDownMessage(String accessToken, SendGroupMarkDownMessageDTO sendGroupMarkDownMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupMarkDownMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);

    }

    @Override
    public SendGroupMessageResultDTO sendGroupWholeCardMessage(String accessToken, SendGroupWholeCardMessageDTO sendGroupWholeCardMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupWholeCardMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);
    }

    @Override
    public SendGroupMessageResultDTO sendGroupIndependentCardMessage(String accessToken, SendGroupIndependentCardMessageDTO sendGroupIndependentCardMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_GROUP_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendGroupIndependentCardMessageDTO), buildHttpHeaders()), SendGroupMessageResultDTO.class);
    }

    @Override
    public GetGroupMessageReadListResultDTO getGroupMessageReadList(String accessToken, String messageId, Long cursor, Integer size) {
        return  restTemplate.getForObject(DingUrl.GET_GROUP_MESSAGE_READ_LIST_URL+"?access_token="+accessToken + "&messageId=" + messageId +"&cursor=" + cursor +"&size=" +size, GetGroupMessageReadListResultDTO.class);

    }

    @Override
    public CreateChatResultDTO createChat(String accessToken, CreateChatDTO createChatDTO) {
        return  restTemplate.postForObject(DingUrl.CREATE_CHAT_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(createChatDTO), buildHttpHeaders()), CreateChatResultDTO.class);
    }

    @Override
    public DefaultResultDTO updateChat(String accessToken, UpdateChatDTO updateChatDTO) {
        return  restTemplate.postForObject(DingUrl.UPDATE_CHAT_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(updateChatDTO), buildHttpHeaders()), DefaultResultDTO.class);

    }

    @Override
    public GetChatResultDTO getChat(String accessToken, String chatid) {
        return  restTemplate.getForObject(DingUrl.GET_CHAT_URL+"?access_token="+accessToken + "&chatid=" + chatid, GetChatResultDTO.class);

    }


    @Override
    public SendMessageResultDTO sendTextMessage(String accessToken, SendTextMessageDTO sendTextMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendTextMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    @Override
    public SendMessageResultDTO sendImageMessage(String accessToken, SendImageMessageDTO sendImageMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendImageMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    @Override
    public SendMessageResultDTO sendVoiceMessage(String accessToken, SendVoiceMessageDTO sendVoiceMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendVoiceMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    @Override
    public SendMessageResultDTO sendFileMessage(String accessToken, SendFileMessageDTO sendFileMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendFileMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    @Override
    public SendMessageResultDTO sendLinkMessage(String accessToken, SendLinkMessageDTO sendLinkMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendLinkMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    @Override
    public SendMessageResultDTO sendOAMessage(String accessToken, SendOAMessageDTO sendOAMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendOAMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    @Override
    public SendMessageResultDTO sendMarkDownMessage(String accessToken, SendMarkDownMessageDTO sendMarkDownMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendMarkDownMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    @Override
    public SendMessageResultDTO sendWholeCardMessage(String accessToken, SendWholeCardMessageDTO sendWholeCardMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendWholeCardMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);
    }

    @Override
    public SendMessageResultDTO sendIndependentCardMessage(String accessToken, SendIndependentCardMessageDTO sendIndependentCardMessageDTO) {
        return  restTemplate.postForObject(DingUrl.SEND_MESSAGE_URL+"?access_token="+accessToken,new HttpEntity<>(JSON.toJSONString(sendIndependentCardMessageDTO), buildHttpHeaders()), SendMessageResultDTO.class);

    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }
}
