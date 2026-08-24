package org.hzero.message.app.service;

import java.util.Map;

import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.message.domain.entity.Message;

/**
 * <p>
 * 消息生成
 * </p>
 *
 * @author qingsheng.chen 2018/7/31 星期二 10:12
 */
public interface MessageGeneratorService {

    /**
     * [平台]生成消息内容和标题
     *
     * @param templateCode 消息模板编码
     * @param args         消息替换参数
     * @param lang         语言
     * @return 生成消息内容和标题
     */
    Message generateMessage(String templateCode, String lang, Map<String, Object> args);

    /**
     * [租户]生成消息内容和标题
     *
     * @param tenantId     租户Id
     * @param templateCode 消息模板编码
     * @param lang         语言
     * @param objectArgs   消息替换参数
     * @return 生成消息内容和标题
     */
    Message generateMessage(Long tenantId, String templateCode, String lang, Map<String, Object> objectArgs);

    /**
     * [租户]生成消息内容和标题
     *
     * @param tenantId     租户Id
     * @param templateCode 消息模板编码
     * @param lang         语言
     * @param templateArgs 消息模板需要的参数
     * @param map          微信钉钉的真实参数
     * @return 生成消息内容和标题
     */
    Message generateMessage(Long tenantId, String templateCode, String lang, Map<String, String> templateArgs, Map<String, String> map);

    /**
     * 获取/生成消息内容
     *
     * @param messageSender 消息发送内容
     * @param message       消息内容
     * @return 消息内容
     */
    Message generateMessage(MessageSender messageSender, Message message);

    /**
     * 获取/生成消息内容
     *
     * @param weChatSender 消息发送内容
     * @param message      消息内容
     * @return 消息内容
     */
    Message generateMessage(WeChatSender weChatSender, Message message);

    /**
     * 获取/生成消息内容
     *
     * @param dingTalkSender 消息发送内容
     * @param message        消息内容
     * @return 消息内容
     */
    Message generateMessage(DingTalkSender dingTalkSender, Message message);

    /**
     * 获取/生成消息内容
     *
     * @param webHookSender 消息发送内容
     * @param message       消息内容
     * @return 消息内容
     */
    Message generateMessage(WebHookSender webHookSender, Message message);
}
