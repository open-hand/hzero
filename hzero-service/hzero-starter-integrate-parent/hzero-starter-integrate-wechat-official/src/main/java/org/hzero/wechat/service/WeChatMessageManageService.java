package org.hzero.wechat.service;

import org.hzero.wechat.dto.*;

public interface WeChatMessageManageService {
    /**
     * 添加客服帐号
     * @param accessToken
     * @param customerServiceAccountDTO
     * @return
     */
    DefaultResultDTO addCustomerServiceAccount(String accessToken, CustomerServiceAccountDTO customerServiceAccountDTO);

    /**
     * 修改客服帐号
     * @param accessToken
     * @param customerServiceAccountDTO
     * @return
     */
    DefaultResultDTO updateCustomerServiceAccount(String accessToken, CustomerServiceAccountDTO customerServiceAccountDTO);

    /**
     * 删除客服帐号
     * @param accessToken
     * @param customerServiceAccountDTO
     * @return
     */
    DefaultResultDTO deleteCustomerServiceAccount(String accessToken, CustomerServiceAccountDTO customerServiceAccountDTO);

    /**
     * 设置客服帐号的头像
     * @param accessToken
     * @param media
     * @return
     */
    DefaultResultDTO setCustomerServiceAccountHeadImage(String accessToken, MediaDTO media);

    /**
     * 获取所有客服账号
     * @param accessToken
     * @return
     */
    GetAllCustomerServiceAccountResultDTO getAllCustomerServiceAccount(String accessToken);

    /**
     * 客服接口-发文本消息
     * @param accessToken
     * @param textMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceTextMessage(String accessToken, TextMessageDTO textMessageDTO);

    /**
     * 客服接口-发图片消息
     * @param accessToken
     * @param imageMassageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceImageMessage(String accessToken, ImageMassageDTO imageMassageDTO);

    /**
     * 客服接口-发语音消息
     * @param accessToken
     * @param voiceMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceVoiceMessage(String accessToken, VoiceMessageDTO voiceMessageDTO);

    /**
     * 客服接口-发视频消息
     * @param accessToken
     * @param videoMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceVideoMessage(String accessToken, VideoMessageDTO videoMessageDTO);

    /**
     * 客服接口-发音乐消息
     * @param accessToken
     * @param musicMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceMusicMessage(String accessToken, MusicMessageDTO musicMessageDTO);

    /**
     * 客服接口-发图文消息（点击跳转到外链）
     * @param accessToken
     * @param newsMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceNewsMessage(String accessToken, NewsMessageDTO newsMessageDTO);

    /**
     * 客服接口-发图文消息（点击跳转到图文消息页面）
     * @param accessToken
     * @param mpNewsMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceMpNewsMessage(String accessToken, MpNewsMessageDTO mpNewsMessageDTO);

    /**
     * 客服接口-发送菜单消息
     * @param accessToken
     * @param menuMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceMenuMessage(String accessToken, MenuMessageDTO menuMessageDTO);

    /**
     * 客服接口-发送卡券单消息
     * @param accessToken
     * @param wxCardMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceWxCardMessage(String accessToken, WxCardMessageDTO wxCardMessageDTO);

    /**
     * 客服接口-发送小程序卡片消息
     * @param accessToken
     * @param miniprogrampageMessageDTO
     * @return
     */
    DefaultResultDTO  sendCustomServiceMiniProgramPageMessage(String accessToken, MiniProgramPageMessageDTO miniprogrampageMessageDTO);

    /**
     * 客服输入状态
     * @param accessToken
     * @param customServiceMessageStatusDTO
     * @return
     */
    DefaultResultDTO customServiceMessageStatus(String accessToken, CustomServiceMessageStatusDTO customServiceMessageStatusDTO );

    /**
     * 上传图文消息内的图片获取URL【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param media
     * @return
     */
    UploadMediaAndGetUrlResultDTO uploadMediaAndGetUrl(String accessToken, MediaDTO media);

    /**
     * 上传图文消息素材【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param uploadNewsMaterialDTO
     * @return
     */
    UploadNewsMaterialResultDTO  uploadNewsMaterial(String accessToken, UploadNewsMaterialDTO uploadNewsMaterialDTO );

    /**
     * 根据标签进行群发-文本消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param groupTextMessageByMarkDTO
     * @return
     */
    GroupMessageResultDTO sendGroupTextMessageByMark(String accessToken, GroupTextMessageByMarkDTO groupTextMessageByMarkDTO);

    /**
     * 根据标签进行群发-图文消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param groupMpNewsMessageByMarkDTO
     * @return
     */
    GroupMessageResultDTO sendGroupMpNewsMessageByMark(String accessToken, GroupMpNewsMessageByMarkDTO groupMpNewsMessageByMarkDTO);

    /**
     * 根据标签进行群发-语音/音频消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param groupVoiceMessageByMarkDTO
     * @return
     */
    GroupMessageResultDTO sendGroupVoiceMessageByMark(String accessToken, GroupVoiceMessageByMarkDTO groupVoiceMessageByMarkDTO);

    /**
     * 根据标签进行群发-图片消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param groupImageMessageByMarkDTO
     * @return
     */
    GroupMessageResultDTO sendGroupImageMessageByMark(String accessToken, GroupImageMessageByMarkDTO groupImageMessageByMarkDTO);

    /**
     * 根据标签进行群发-视频消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param groupVideoMessageByMarkDTO
     * @return
     */
    GroupMessageResultDTO sendGroupVideoMessageByMark(String accessToken, GroupVideoMessageByMarkDTO groupVideoMessageByMarkDTO );

    /**
     * 根据标签进行群发-卡券消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param groupWxCardMessageByMarkDTO
     * @return
     */
    GroupMessageResultDTO sendGroupWxCardMessageByMark(String accessToken, GroupWxCardMessageByMarkDTO groupWxCardMessageByMarkDTO);

    /**
     * 根据OpenID列表群发-文本消息【订阅号不可用，服务号认证后可用】
     * @param accessToken
     * @param groupTextMessageByOpenIdDTO
     * @return
     */
    GroupMessageResultDTO sendGroupTextMessageByOpenId(String accessToken, GroupTextMessageByOpenIdDTO groupTextMessageByOpenIdDTO);

    /**
     * 根据OpenID列表群发-图文消息【订阅号不可用，服务号认证后可用】
     * @param accessToken
     * @param groupMpNewsMessageByOpenIdDTO
     * @return
     */
    GroupMessageResultDTO sendGroupMpNewsMessageByOpenId(String accessToken, GroupMpNewsMessageByOpenIdDTO groupMpNewsMessageByOpenIdDTO);

    /**
     * 根据OpenID列表群发-语音消息【订阅号不可用，服务号认证后可用】
     * @param accessToken
     * @param groupVoiceMessageByOpenIdDTO
     * @return
     */
    GroupMessageResultDTO sendGroupVoiceMessageByOpenId(String accessToken, GroupVoiceMessageByOpenIdDTO groupVoiceMessageByOpenIdDTO);

    /**
     * 根据OpenID列表群发-图片消息【订阅号不可用，服务号认证后可用】
     * @param accessToken
     * @param groupImageMessageByOpenIdDTO
     * @return
     */
    GroupMessageResultDTO sendGroupImageMessageByOpenId(String accessToken, GroupImageMessageByOpenIdDTO groupImageMessageByOpenIdDTO);

    /**
     * 根据OpenID列表群发-视频消息【订阅号不可用，服务号认证后可用】
     * @param accessToken
     * @param groupVideoMessageByOpenIdDTO
     * @return
     */
    GroupMessageResultDTO sendGroupVideoMessageByOpenId(String accessToken, GroupVideoMessageByOpenIdDTO groupVideoMessageByOpenIdDTO );

    /**
     *  根据OpenID列表群发-卡券消息【订阅号不可用，服务号认证后可用】
     * @param accessToken
     * @param groupWxCardMessageByOpenIdDTO
     * @return
     */
    GroupMessageResultDTO sendGroupWxCardMessageByOpenId(String accessToken, GroupWxCardMessageByOpenIdDTO groupWxCardMessageByOpenIdDTO);

    /**
     *  删除群发【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param deleteGroupMessageDTO
     * @return
     */
    DefaultResultDTO deleteGroupMessage(String accessToken, DeleteGroupMessageDTO deleteGroupMessageDTO);

    /**
     *  预览接口——图文消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param previewMpNewsMessageDTO
     * @return
     */
    PreviewMessageResultDTO sendPreviewMpNewsMessage(String accessToken, PreviewMpNewsMessageDTO previewMpNewsMessageDTO);

    /**
     * 预览接口——文本消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param previewTextMessageDTO
     * @return
     */
    PreviewMessageResultDTO sendPreviewTextMessage(String accessToken, PreviewTextMessageDTO previewTextMessageDTO);

    /**
     * 预览接口——图片消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param previewImageMessageDTO
     * @return
     */
    PreviewMessageResultDTO sendPreviewImageMessage(String accessToken, PreviewImageMessageDTO previewImageMessageDTO);

    /**
     * 预览接口——视频消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param previewVideoMessageDTO
     * @return
     */
    PreviewMessageResultDTO sendPreviewVideoMessage(String accessToken, PreviewVideoMessageDTO previewVideoMessageDTO);

    /**
     *  预览接口——语音消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param previewVoiceMessageDTO
     * @return
     */
    PreviewMessageResultDTO sendPreviewVoiceMessage(String accessToken, PreviewVoiceMessageDTO previewVoiceMessageDTO);

    /**
     * 预览接口——卡券消息【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param previewWxCardMessageDTO
     * @return
     */
    PreviewMessageResultDTO sendPreviewWxCardMessage(String accessToken, PreviewWxCardMessageDTO previewWxCardMessageDTO);

    /**
     * 查询群发消息发送状态【订阅号与服务号认证后均可用】
     * @param accessToken
     * @param msg_id
     * @return
     */
    GetGroupMessageStatusResultDTO getGroupMessageStatus(String accessToken, String  msg_id);

    /**
     * 获取群发速度
     * @param accessToken
     * @return
     */
    GetGroupSendMessageSpeedResultDTO  getGroupSendMessageSpeed(String accessToken);

    /**
     * 设置群发速度
     * @param accessToken
     * @param speed
     * @return
     */
    DefaultResultDTO setGroupSendMessageSpeed(String accessToken, Long speed);

    /**
     * 通过API推送订阅模板消息给到授权微信用户
     * @param accessToken
     * @param subscribeTemplateMessageDTO
     * @return
     */
    DefaultResultDTO subscribeTemplateMessageToUser(String accessToken, SubscribeTemplateMessageDTO subscribeTemplateMessageDTO);

    /**
     * 公众号调用或第三方平台帮公众号调用对公众号的所有api调用（包括第三方帮其调用）次数进行清零：
     * @param accessToken
     * @param appid
     * @return
     */
    DefaultResultDTO reset(String accessToken, String appid);

    /**
     * 获取公众号的自动回复规则
     * @param accessToken
     * @return
     */
    GetCurrentAutoReplyInfoResultDTO  getCurrentAutoReplyInfo(String accessToken);



}
