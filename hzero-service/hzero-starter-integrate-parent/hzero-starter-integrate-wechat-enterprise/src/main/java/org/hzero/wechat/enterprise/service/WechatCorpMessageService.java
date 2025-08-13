package org.hzero.wechat.enterprise.service;

import org.hzero.wechat.enterprise.dto.*;

/**
 * 企业微信 消息推送api
 *
 * @Author J
 * @Date 2019/10/21
 */
public interface WechatCorpMessageService {


    /**
     * 发送应用消息-文本消息
     * @param messageDTO 文本消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendTextMsg(TextMessageDTO messageDTO, String accessToken);

    /**
     * 发送应用消息-图片消息
     * @param messageDTO 图片消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendImageMsg(ImageMessageDTO messageDTO, String accessToken);


    /**
     * 发送应用消息-语音消息
     * @param messageDTO 语音消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendVoiceMsg(VoiceMessageDTO messageDTO, String accessToken);

    /**
     * 发送应用消息-视频消息
     * @param messageDTO 视频消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendVideoMsg(VideoMessageDTO messageDTO, String accessToken);


    /**
     * 发送应用消息-文件消息
     * @param messageDTO 文件消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendFileMsg(FileMessageDTO messageDTO, String accessToken);

    /**
     * 发送应用消息-文本卡片消息
     * @param messageDTO 文本卡片消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendTextCardMsg(TextCardMessageDTO messageDTO, String accessToken);


    /**
     * 发送应用消息-图文消息
     * @param messageDTO 图文消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendNewsMsg(NewsMessageDTO messageDTO, String accessToken);

    /**
     * 发送应用消息-markdown消息
     * @param messageDTO 图片消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendMarkdownMsg(MarkdownMessageDTO messageDTO, String accessToken);


    /**
     * 发送应用消息-小程序通知消息
     * @param messageDTO 小程序通知消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendMiniProgramNoticeMsg(MiniProgramNoticeMessageDTO messageDTO, String accessToken);

    /**
     * 发送应用消息-任务卡片消息
     * @param messageDTO 任务卡片消息
     * @param accessToken
     * @return
     */
    MessageSendResultDTO sendTaskCardMsg(TaskCardMessageDTO messageDTO, String accessToken);




    /**
     * 创建群聊会话
     * @param appChatCreateResultDTO
     * @param accessToken
     * @return
     */
    AppChatCreateResultDTO createAppChat(AppChatCreateResultDTO appChatCreateResultDTO,String accessToken);

    /**
     * 修改群聊会话
     * @param appChatCreateResultDTO
     * @param accessToken
     * @return
     */
    DefaultResultDTO updateAppChat(AppChatUpdateDTO appChatCreateResultDTO,String accessToken);

    /**
     * 获取群聊会话
     * @param chatid 群聊id
     * @param accessToken
     * @return
     */
    AppChatDTO getAppChat(String chatid,String accessToken);

    /**
     * 应用推送消息
     * @param object 根据企业微信自己构造吧 <url>https://open.work.weixin.qq.com/api/doc#90000/90135/90248</url>
     * @param accessToken
     * @return
     */
    DefaultResultDTO sendAppChat(Object object,String accessToken);



}
