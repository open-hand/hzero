package org.hzero.message.domain.service.impl;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.domain.service.IMessageLangService;
import org.hzero.message.domain.vo.UserCacheVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 处理消息语言
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/27 17:28
 */
@Service
public class MessageLangServiceImpl implements IMessageLangService {

    private final RedisHelper redisHelper;
    private final MessageClientProperties messageClientProperties;

    @Autowired
    public MessageLangServiceImpl(RedisHelper redisHelper,
                                  MessageClientProperties messageClientProperties) {
        this.redisHelper = redisHelper;
        this.messageClientProperties = messageClientProperties;
    }

    @Override
    public List<MessageSender> getLang(MessageSender sourceSender) {
        // 发送时指定了语言，不做处理
        if (StringUtils.isNotBlank(sourceSender.getLang())) {
            return Collections.singletonList(sourceSender);
        }
        List<Receiver> receiverList = sourceSender.getReceiverAddressList();
        Map<String, MessageSender> map = new HashMap<>(16);
        // 遍历所有接收人，按照语言分组
        for (Receiver receiver : receiverList) {
            String lang = getLang(receiver.getUserId());
            MessageSender langSender = getSender(map, lang, sourceSender);
            List<Receiver> receivers = langSender.getReceiverAddressList();
            receivers.add(receiver);
        }
        return new ArrayList<>(map.values());
    }

    private MessageSender getSender(Map<String, MessageSender> map, String lang, MessageSender sourceSender) {
        if (map.containsKey(lang)) {
            return map.get(lang);
        }
        MessageSender langSender = new MessageSender();
        BeanUtils.copyProperties(sourceSender, langSender);
        langSender.setLang(lang).setReceiverAddressList(new ArrayList<>());
        map.put(lang, langSender);
        return langSender;
    }

    private String getLang(Long userId) {
        // 未指定用户ID，无法查询，返回默认语言
        if (userId == null) {
            return messageClientProperties.getDefaultLang();
        }
        String str = redisHelper.hshGet(HZeroCacheKey.USER, String.valueOf(userId));
        if (StringUtils.isBlank(str)) {
            // 缓存未找到返回默认语言
            return messageClientProperties.getDefaultLang();
        }
        UserCacheVO cacheVO = redisHelper.fromJson(str, UserCacheVO.class);
        return cacheVO.getLanguage() == null || "".equals(cacheVO.getLanguage()) || "null".equals(cacheVO.getLanguage()) ?
                messageClientProperties.getDefaultLang() : cacheVO.getLanguage();
    }
}
