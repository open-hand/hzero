package org.hzero.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.wechat.constant.WechatApi;
import org.hzero.wechat.dto.*;
import org.hzero.wechat.service.WeChatMessageManageService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;

import javax.annotation.Resource;

public class WeChatMessageManageServiceImp implements WeChatMessageManageService {
    @Resource(name = "wdRestTemplate")
    protected RestTemplate restTemplate;

    @Override
    public DefaultResultDTO addCustomerServiceAccount(String accessToken, CustomerServiceAccountDTO customerServiceAccountDTO) {
        return restTemplate.postForObject(WechatApi.ADD_KF_ACCOUNT_URL + accessToken, new HttpEntity<>(JSON.toJSONString(customerServiceAccountDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO updateCustomerServiceAccount(String accessToken, CustomerServiceAccountDTO customerServiceAccountDTO) {
        return restTemplate.postForObject(WechatApi.UPDATE_KF_ACCOUNT_URL + accessToken, new HttpEntity<>(JSON.toJSONString(customerServiceAccountDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteCustomerServiceAccount(String accessToken, CustomerServiceAccountDTO customerServiceAccountDTO) {
        return restTemplate.exchange(WechatApi.DELETE_KF_ACCOUNT_URL + accessToken, HttpMethod.GET, new HttpEntity<>(JSON.toJSONString(customerServiceAccountDTO), buildHttpHeaders()),DefaultResultDTO.class).getBody();
    }

    @Override
    public DefaultResultDTO setCustomerServiceAccountHeadImage(String accessToken, MediaDTO media)
    {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("filename",media.getFilename());
        body.add("Content-Type",media.getContentType());
        body.add("contentLength",media.getFileLength());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, buildFormDateHttpHeaders());
        return restTemplate.postForObject(WechatApi.SET_KF_ACCOUNT_HEAD_IMAGE_URL +"?access_token=" + accessToken ,entity,DefaultResultDTO.class );

    }

    @Override
    public GetAllCustomerServiceAccountResultDTO getAllCustomerServiceAccount(String accessToken) {
        return restTemplate.getForObject(WechatApi.GET_ALL_ACCOUNT_URL + accessToken, GetAllCustomerServiceAccountResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceTextMessage(String accessToken, TextMessageDTO textMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(textMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceImageMessage(String accessToken, ImageMassageDTO imageMassageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(imageMassageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceVoiceMessage(String accessToken, VoiceMessageDTO voiceMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(voiceMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceVideoMessage(String accessToken, VideoMessageDTO videoMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(videoMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceMusicMessage(String accessToken, MusicMessageDTO musicMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(musicMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceNewsMessage(String accessToken, NewsMessageDTO newsMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(newsMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceMpNewsMessage(String accessToken, MpNewsMessageDTO mpNewsMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(mpNewsMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceMenuMessage(String accessToken, MenuMessageDTO menuMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(menuMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);
    }

    @Override
    public DefaultResultDTO sendCustomServiceWxCardMessage(String accessToken, WxCardMessageDTO wxCardMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(wxCardMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);

    }

    @Override
    public DefaultResultDTO sendCustomServiceMiniProgramPageMessage(String accessToken, MiniProgramPageMessageDTO miniprogrampageMessageDTO) {
        return restTemplate.postForObject(WechatApi.SEND_CUSTOM_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(miniprogrampageMessageDTO), buildHttpHeaders()), DefaultResultDTO.class);

    }

    @Override
    public DefaultResultDTO customServiceMessageStatus(String accessToken, CustomServiceMessageStatusDTO customServiceMessageStatusDTO) {
        return restTemplate.postForObject(WechatApi.CUSTOM_MESSAGE_STATUS_URL + accessToken, new HttpEntity<>(JSON.toJSONString(customServiceMessageStatusDTO), buildHttpHeaders()), DefaultResultDTO.class);

    }

    @Override
    public UploadMediaAndGetUrlResultDTO uploadMediaAndGetUrl(String accessToken, MediaDTO media) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("filename",media.getFilename());
        body.add("Content-Type",media.getContentType());
        body.add("contentLength",media.getFileLength());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, buildFormDateHttpHeaders());
        return restTemplate.postForObject(WechatApi.UPLOADING_MEDIA_URL +"?access_token=" + accessToken ,entity,UploadMediaAndGetUrlResultDTO.class );

    }

    @Override
    public UploadNewsMaterialResultDTO uploadNewsMaterial(String accessToken, UploadNewsMaterialDTO uploadNewsMaterialDTO) {
        return restTemplate.postForObject(WechatApi.UPLOAD_MATERIAL_URL + accessToken, new HttpEntity<>(JSON.toJSONString(uploadNewsMaterialDTO), buildHttpHeaders()), UploadNewsMaterialResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupTextMessageByMark(String accessToken, GroupTextMessageByMarkDTO groupTextMessageByMarkDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_ALL_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupTextMessageByMarkDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupMpNewsMessageByMark(String accessToken, GroupMpNewsMessageByMarkDTO groupMpNewsMessageByMarkDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_ALL_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupMpNewsMessageByMarkDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupVoiceMessageByMark(String accessToken, GroupVoiceMessageByMarkDTO groupVoiceMessageByMarkDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_ALL_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupVoiceMessageByMarkDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupImageMessageByMark(String accessToken, GroupImageMessageByMarkDTO groupImageMessageByMarkDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_ALL_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupImageMessageByMarkDTO), buildHttpHeaders()), GroupMessageResultDTO.class);
    }

    @Override
    public GroupMessageResultDTO sendGroupVideoMessageByMark(String accessToken, GroupVideoMessageByMarkDTO groupVideoMessageByMarkDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_ALL_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupVideoMessageByMarkDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupWxCardMessageByMark(String accessToken, GroupWxCardMessageByMarkDTO groupWxCardMessageByMarkDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_ALL_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupWxCardMessageByMarkDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupTextMessageByOpenId(String accessToken, GroupTextMessageByOpenIdDTO groupTextMessageByOpenIdDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupTextMessageByOpenIdDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupMpNewsMessageByOpenId(String accessToken, GroupMpNewsMessageByOpenIdDTO groupMpNewsMessageByOpenIdDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupMpNewsMessageByOpenIdDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupVoiceMessageByOpenId(String accessToken, GroupVoiceMessageByOpenIdDTO groupVoiceMessageByOpenIdDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupVoiceMessageByOpenIdDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupImageMessageByOpenId(String accessToken, GroupImageMessageByOpenIdDTO groupImageMessageByOpenIdDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupImageMessageByOpenIdDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupVideoMessageByOpenId(String accessToken, GroupVideoMessageByOpenIdDTO groupVideoMessageByOpenIdDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupVideoMessageByOpenIdDTO), buildHttpHeaders()), GroupMessageResultDTO.class);

    }

    @Override
    public GroupMessageResultDTO sendGroupWxCardMessageByOpenId(String accessToken, GroupWxCardMessageByOpenIdDTO groupWxCardMessageByOpenIdDTO) {
        return restTemplate.postForObject(WechatApi.SEND_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(groupWxCardMessageByOpenIdDTO), buildHttpHeaders()), GroupMessageResultDTO.class);
    }

    @Override
    public DefaultResultDTO deleteGroupMessage(String accessToken, DeleteGroupMessageDTO deleteGroupMessageDTO) {
        return restTemplate.postForObject(WechatApi.DELETE_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(deleteGroupMessageDTO), buildHttpHeaders()),  DefaultResultDTO.class);
    }

    @Override
    public PreviewMessageResultDTO sendPreviewMpNewsMessage(String accessToken, PreviewMpNewsMessageDTO previewMpNewsMessageDTO) {
        return restTemplate.postForObject(WechatApi.PREVIEW_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(previewMpNewsMessageDTO), buildHttpHeaders()),  PreviewMessageResultDTO.class);

    }

    @Override
    public PreviewMessageResultDTO sendPreviewTextMessage(String accessToken, PreviewTextMessageDTO previewTextMessageDTO) {
        return restTemplate.postForObject(WechatApi.PREVIEW_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString( previewTextMessageDTO), buildHttpHeaders()),  PreviewMessageResultDTO.class);

    }

    @Override
    public PreviewMessageResultDTO sendPreviewImageMessage(String accessToken, PreviewImageMessageDTO previewImageMessageDTO) {
        return restTemplate.postForObject(WechatApi.PREVIEW_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(previewImageMessageDTO), buildHttpHeaders()),  PreviewMessageResultDTO.class);

    }

    @Override
    public PreviewMessageResultDTO sendPreviewVideoMessage(String accessToken, PreviewVideoMessageDTO previewVideoMessageDTO) {
        return restTemplate.postForObject(WechatApi.PREVIEW_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(previewVideoMessageDTO), buildHttpHeaders()),  PreviewMessageResultDTO.class);

    }

    @Override
    public PreviewMessageResultDTO sendPreviewVoiceMessage(String accessToken, PreviewVoiceMessageDTO previewVoiceMessageDTO) {
        return restTemplate.postForObject(WechatApi.PREVIEW_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(previewVoiceMessageDTO), buildHttpHeaders()),  PreviewMessageResultDTO.class);

    }

    @Override
    public PreviewMessageResultDTO sendPreviewWxCardMessage(String accessToken, PreviewWxCardMessageDTO previewWxCardMessageDTO) {
        return restTemplate.postForObject(WechatApi.PREVIEW_MASS_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(previewWxCardMessageDTO), buildHttpHeaders()),  PreviewMessageResultDTO.class);

    }

    @Override
    public GetGroupMessageStatusResultDTO getGroupMessageStatus(String accessToken, String msg_id) {
        return restTemplate.postForObject(WechatApi.GET_MASS_MESSAGE_STATUS_URL + accessToken, new HttpEntity<>(JSON.toJSONString(msg_id), buildHttpHeaders()),  GetGroupMessageStatusResultDTO.class);
    }

    @Override
    public GetGroupSendMessageSpeedResultDTO getGroupSendMessageSpeed(String accessToken) {
        return restTemplate.postForObject(WechatApi.GET_MASS_MESSAGE_SPEED_URL + accessToken, null,  GetGroupSendMessageSpeedResultDTO.class);
    }

    @Override
    public DefaultResultDTO setGroupSendMessageSpeed(String accessToken, Long speed) {
        return restTemplate.postForObject(WechatApi.set_mass_message_speed_url + accessToken, new HttpEntity<>(JSON.toJSONString( speed), buildHttpHeaders()),  DefaultResultDTO.class);

    }

    @Override
    public DefaultResultDTO subscribeTemplateMessageToUser(String accessToken, SubscribeTemplateMessageDTO subscribeTemplateMessageDTO) {
        return restTemplate.postForObject(WechatApi. SUBSCRIBE_TEMPLATE_MESSAGE_URL + accessToken, new HttpEntity<>(JSON.toJSONString(subscribeTemplateMessageDTO), buildHttpHeaders()),  DefaultResultDTO.class);

    }

    @Override
    public DefaultResultDTO reset(String accessToken, String appid) {
        return restTemplate.postForObject(WechatApi.CLEAR_QUOTA_URL + accessToken, new HttpEntity<>(JSON.toJSONString(appid), buildHttpHeaders()),  DefaultResultDTO.class);

    }

    @Override
    public GetCurrentAutoReplyInfoResultDTO getCurrentAutoReplyInfo(String accessToken) {
        return restTemplate.getForObject(WechatApi.GET_CURRENT_AUTO_REPLY_INFO_URL + accessToken,   GetCurrentAutoReplyInfoResultDTO.class);

    }

    protected HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        return httpHeaders;
    }

    protected HttpHeaders buildFormDateHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "multipart/form-data");
        return httpHeaders;
    }
}
