package org.hzero.dd.service;

import org.hzero.dd.dto.*;



public interface DingCorpMessageService {

    /**
     * 发送工作通知消息-文本消息
     * @param accessToken
     * @param sendWorkTextMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkTextMessage(String accessToken , SendWorkTextMessageDTO sendWorkTextMessageDTO);


    /**
     * 发送工作通知消息-图片消息
     * @param accessToken
     * @param sendWorkImageMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkImageMessage(String accessToken , SendWorkImageMessageDTO sendWorkImageMessageDTO);

    /**
     * 发送工作通知消息-语音消息
     * @param accessToken
     * @param sendWorkVoiceMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkVoiceMessage(String accessToken , SendWorkVoiceMessageDTO sendWorkVoiceMessageDTO);

    /**
     * 发送工作通知消息-文件消息
     * @param accessToken
     * @param sendWorkFileMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkFileMessage(String accessToken , SendWorkFileMessageDTO sendWorkFileMessageDTO);

    /**
     * 发送工作通知消息-链接消息
     * @param accessToken
     * @param sendWorkLinkMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkLinkMessage(String accessToken , SendWorkLinkMessageDTO sendWorkLinkMessageDTO);

    /**
     * 发送工作通知消息-OA消息
     * @param accessToken
     * @param sendWorkOAMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkOAMessage(String accessToken , SendWorkOAMessageDTO sendWorkOAMessageDTO);

    /**
     * 发送工作通知消息-markdown消息
     * @param accessToken
     * @param sendWorkMarkDownMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkMarkDownMessage(String accessToken ,SendWorkMarkDownMessageDTO sendWorkMarkDownMessageDTO);

    /**
     * 发送工作通知消息-整体跳转卡片消息
     * @param accessToken
     * @param sendWorkWholeCardMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkWholeCardMessage(String accessToken, SendWorkWholeCardMessageDTO sendWorkWholeCardMessageDTO);

    /**
     * 发送工作通知消息-独立跳转卡片消息
     * @param accessToken
     * @param sendWorkIndependentCardMessageDTO
     * @return
     */
    SendWorkMessageResultDTO sendWorkIndependentCardMessage(String accessToken ,SendWorkIndependentCardMessageDTO sendWorkIndependentCardMessageDTO);




    /**
     *  查询工作通知消息的发送进度
     * @param accessToken
     * @param workProgressAndResultMessageDTO
     * @return
     */
     WorkProgressMessageResultDTO getWorkProgressMessage(String accessToken, WorkProgressAndResultMessageDTO workProgressAndResultMessageDTO);


    /**
     * 查询工作通知消息的发送结果
     * @param accessToken
     * @param workProgressAndResultMessageDTO
     * @return
     */
    WorkResultMessageDTO getWorkResultMessage(String accessToken, WorkProgressAndResultMessageDTO workProgressAndResultMessageDTO );


    /**
     * 工作通知消息撤回
     * @param accessToken
     * @param workMessageRecallDTO
     * @return
     */
   DefaultResultDTO getWorkMessageRecall(String accessToken, WorkMessageRecallDTO workMessageRecallDTO);

    /**
     * 发送群消息-文本消息
     * @param accessToken
     * @param sendGroupTextMessageDTO
     * @return
     */
    SendGroupMessageResultDTO sendGroupTextMessage(String accessToken, SendGroupTextMessageDTO sendGroupTextMessageDTO);

    /**
     * 发送群消息-图片消息
     * @param accessToken
     * @param sendGroupImageMessageDTO
     * @return
     */

    SendGroupMessageResultDTO sendGroupImageMessage(String accessToken, SendGroupImageMessageDTO sendGroupImageMessageDTO);

    /**
     * 发送群消息-语音消息
     * @param accessToken
     * @param sendGroupVoiceMessageDTO
     * @return
     */
    SendGroupMessageResultDTO sendGroupVoiceMessage(String accessToken, SendGroupVoiceMessageDTO sendGroupVoiceMessageDTO);

    /**
     * 发送群消息-文件消息
     * @param accessToken
     * @param sendGroupFileMessageDTO
     * @return
     */
    SendGroupMessageResultDTO sendGroupFileMessage(String accessToken, SendGroupFileMessageDTO sendGroupFileMessageDTO);

    /**
     * 发送群消息-链接消息
     * @param accessToken
     * @param sendGroupLinkMessageDTO
     * @return
     */
    SendGroupMessageResultDTO sendGroupLinkMessage(String accessToken, SendGroupLinkMessageDTO sendGroupLinkMessageDTO);

    /**
     * 发送群消息-OA消息
     * @param accessToken
     * @param sendGroupOAMessageDTO
     * @return
     */
    SendGroupMessageResultDTO sendGroupOAMessage(String accessToken, SendGroupOAMessageDTO sendGroupOAMessageDTO);

    /**
     * 发送群消息-markdown消息
     * @param accessToken
     * @param sendGroupMarkDownMessageDTO
     * @return
     */

    SendGroupMessageResultDTO sendGroupMarkDownMessage(String accessToken, SendGroupMarkDownMessageDTO sendGroupMarkDownMessageDTO);

    /**
     * 发送群消息-整体跳转卡片消息
     * @param accessToken
     * @param sendGroupWholeCardMessageDTO
     * @return
     */
    SendGroupMessageResultDTO sendGroupWholeCardMessage(String accessToken, SendGroupWholeCardMessageDTO sendGroupWholeCardMessageDTO);

    /**
     * 发送群消息-独立跳转卡片消息
     * @param accessToken
     * @param sendGroupIndependentCardMessageDTO
     * @return
     */
    SendGroupMessageResultDTO sendGroupIndependentCardMessage(String accessToken, SendGroupIndependentCardMessageDTO sendGroupIndependentCardMessageDTO);


    /**
     * 查询群消息已读人员列表
     * @param accessToken
     * @return
     */
    GetGroupMessageReadListResultDTO getGroupMessageReadList (String accessToken, String messageId, Long cursor, Integer size);


    /**
     * 创建会话
     * @param accessToken
     * @param createChatDTO
     * @return
     */
    CreateChatResultDTO createChat(String accessToken, CreateChatDTO createChatDTO);

    /**
     * 修改会话
     * @param accessToken
     * @return
     */
    DefaultResultDTO updateChat(String accessToken, UpdateChatDTO updateChatDTO);


    /**
     * 获取会话
     * @param accessToken
     * @param chatid
     * @return
     */
    GetChatResultDTO getChat(String accessToken, String chatid);



    /**
     * 发送普通消息-文本消息
     * @param accessToken
     * @param sendTextMessageDTO
     * @return
     */
    SendMessageResultDTO sendTextMessage(String accessToken, SendTextMessageDTO sendTextMessageDTO);

    /**
     * 发送普通消息-图片消息
     * @param accessToken
     * @param sendImageMessageDTO
     * @return
     */

    SendMessageResultDTO sendImageMessage(String accessToken,SendImageMessageDTO sendImageMessageDTO);

    /**
     * 发送普通消息-语音消息
     * @param accessToken
     * @param sendVoiceMessageDTO
     * @return
     */

    SendMessageResultDTO sendVoiceMessage(String accessToken, SendVoiceMessageDTO sendVoiceMessageDTO);

    /**
     * 发送普通消息-文件消息
     * @param accessToken
     * @param sendFileMessageDTO
     * @return
     */

    SendMessageResultDTO sendFileMessage(String accessToken, SendFileMessageDTO sendFileMessageDTO);

    /**
     * 发送普通消息-链接消息
     * @param accessToken
     * @param sendLinkMessageDTO
     * @return
     */

    SendMessageResultDTO sendLinkMessage(String accessToken, SendLinkMessageDTO sendLinkMessageDTO);

    /**
     * 发送普通消息-OA消息
     * @param accessToken
     * @param sendOAMessageDTO
     * @return
     */

    SendMessageResultDTO sendOAMessage(String accessToken, SendOAMessageDTO sendOAMessageDTO);

    /**
     * 发送普通消息-markdown消息
     * @param accessToken
     * @param sendMarkDownMessageDTO
     * @return
     */

    SendMessageResultDTO sendMarkDownMessage(String accessToken, SendMarkDownMessageDTO sendMarkDownMessageDTO);

    /**
     * 发送普通消息-整体跳转卡片消息
     * @param accessToken
     * @param sendWholeCardMessageDTO
     * @return
     */

    SendMessageResultDTO sendWholeCardMessage(String accessToken,SendWholeCardMessageDTO sendWholeCardMessageDTO);

    /**
     * 发送普通消息-独立跳转卡片消息
     * @param accessToken
     * @param sendIndependentCardMessageDTO
     * @return
     */
    SendMessageResultDTO sendIndependentCardMessage(String accessToken, SendIndependentCardMessageDTO sendIndependentCardMessageDTO);

}
