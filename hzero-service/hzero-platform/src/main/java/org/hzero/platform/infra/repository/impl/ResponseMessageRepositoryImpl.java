package org.hzero.platform.infra.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.domain.entity.Message;
import org.hzero.platform.domain.repository.ResponseMessageRepository;
import org.hzero.platform.infra.mapper.ResponseMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 后端消息 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-08 15:04:28
 */
@Component
public class ResponseMessageRepositoryImpl extends BaseRepositoryImpl<Message> implements ResponseMessageRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseMessageRepositoryImpl.class);

    @Autowired
    private ResponseMessageMapper messageMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;

    @ProcessLovValue
    @Override
    public Page<Message> selectMessageList(PageRequest pageRequest, Message message) {
        return PageHelper.doPage(pageRequest, () -> messageMapper.selectMessageList(message));
    }

    @ProcessLovValue
    @Override
    public Message selectMessageDetails(Long messageId) {
        Message message = new Message();
        message.setMessageId(messageId);
        List<Message> messages = messageMapper.selectMessageList(message);
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        } else {
            return messages.get(0);
        }
    }

    @Override
    public void initAllReturnMessageToRedis() {
        LOGGER.info("---------------------- start init return message ------------------------------");
        // 类加载时缓存数据到redis
        int total = 0;
        Page<Message> messages;
        PageRequest pageRequest = new PageRequest(0, 1000, new Sort(Sort.Direction.ASC, Message.FIELD_MESSAGE_ID));
        do {
            SecurityTokenHelper.close();
            messages = PageHelper.doPage(pageRequest, () -> messageMapper.selectMessageList(new Message()));
            total += messages.size();
            messages.forEach((msg) -> msg.cacheMessage(redisHelper, objectMapper));
            pageRequest.setPage(pageRequest.getPage() + 1);
        } while (!messages.isEmpty());
        SecurityTokenHelper.clear();
        LOGGER.info("---------------------- success init return message : {} ------------------------------", total);
    }
}
