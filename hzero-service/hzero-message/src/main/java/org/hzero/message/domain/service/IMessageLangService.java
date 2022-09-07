package org.hzero.message.domain.service;

import java.util.List;

import org.hzero.boot.message.entity.MessageSender;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/27 17:27
 */
public interface IMessageLangService {

    /**
     * 获取模板语言
     *
     * @param messageSender 消息发送对象
     * @return 最终使用的语言
     */
    List<MessageSender> getLang(MessageSender messageSender);
}
