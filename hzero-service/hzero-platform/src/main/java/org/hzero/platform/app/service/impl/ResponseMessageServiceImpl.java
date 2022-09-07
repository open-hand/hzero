package org.hzero.platform.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.app.service.ResponseMessageService;
import org.hzero.platform.domain.entity.Message;
import org.hzero.platform.domain.repository.ResponseMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 后端消息应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-08 15:04:28
 */
@Service
public class ResponseMessageServiceImpl implements ResponseMessageService {

    @Autowired
    private ResponseMessageRepository messageRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message createMessage(Message message) {
        // 判断新建的消息是否已经存在
        message.validate(messageRepository);
        messageRepository.insertSelective(message);
        message.cacheMessage(redisHelper, objectMapper);
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message updateMessage(Message message) {
        message.clearCache(redisHelper);
        messageRepository.updateOptional(message,
                Message.FIELD_TYPE,
                Message.FIELD_DESCRIPTION
        );
        message.cacheMessage(redisHelper, objectMapper);
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Message message) {
        message.clearCache(redisHelper);
        messageRepository.deleteByPrimaryKey(message.getMessageId());
    }
}
