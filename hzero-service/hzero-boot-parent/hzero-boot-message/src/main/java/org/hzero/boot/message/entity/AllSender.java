package org.hzero.boot.message.entity;

/**
 * 关联消息发送对象
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/28 13:35
 */
public class AllSender {

    private MessageSender messageSender;
    private WeChatSender weChatSender;
    private DingTalkSender dingTalkSender;

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public AllSender setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
        return this;
    }

    public WeChatSender getWeChatSender() {
        return weChatSender;
    }

    public AllSender setWeChatSender(WeChatSender weChatSender) {
        this.weChatSender = weChatSender;
        return this;
    }

    public DingTalkSender getDingTalkSender() {
        return dingTalkSender;
    }

    public AllSender setDingTalkSender(DingTalkSender dingTalkSender) {
        this.dingTalkSender = dingTalkSender;
        return this;
    }
}
