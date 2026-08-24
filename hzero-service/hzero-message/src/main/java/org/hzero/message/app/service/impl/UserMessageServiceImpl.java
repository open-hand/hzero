package org.hzero.message.app.service.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;
import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 站内消息客户端接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/8/1 星期三 16:42
 */
@Service
public class UserMessageServiceImpl implements UserMessageService {

    private final UserMessageRepository userMessageRepository;
    private final RedisHelper simpleMessageRedisHelper;
    private final MessageConfigProperties messageConfigProperties;
    private final MessageClient messageClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserMessageServiceImpl(UserMessageRepository userMessageRepository,
                                  RedisHelper simpleMessageRedisHelper,
                                  MessageConfigProperties messageConfigProperties,
                                  MessageClient messageClient,
                                  ObjectMapper objectMapper) {
        this.userMessageRepository = userMessageRepository;
        this.simpleMessageRedisHelper = simpleMessageRedisHelper;
        this.messageConfigProperties = messageConfigProperties;
        this.messageClient = messageClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Long countUnreadMessage(long tenantId, long userId) {
        Long cnt = simpleMessageRedisHelper.zSetSize(HmsgConstant.USER_MESSAGE_ZSET + userId);
        if (cnt == null) {
            cnt = 0L;
        }
        return cnt;
    }

    @Override
    public List<SimpleMessageDTO> listSimpleMessage(long tenantId, long userId, Integer previewMessageCount, boolean withContent) {
        //查询缓存前几条
        Set<String> keySets = simpleMessageRedisHelper.zSetReverseRange(HmsgConstant.USER_MESSAGE_ZSET + userId, 0L, previewMessageCount != null ? previewMessageCount : messageConfigProperties.getMaxUnreadMessageCount());
        if (CollectionUtils.isEmpty(keySets)) {
            return new ArrayList<>();
        }
        List<Long> ids = new ArrayList<>();
        for (String id : keySets) {
            ids.add(Long.parseLong(id));
        }
        return userMessageRepository.selectSimpleMsgByIds(ids);
    }

    @Override
    public Page<UserMessageDTO> listMessage(UserMsgParamDTO userMsgParamDTO, PageRequest pageRequest) {
        if (StringUtils.isBlank(userMsgParamDTO.getUserMessageTypeCode())) {
            userMsgParamDTO.setUserMessageTypeCode(HmsgConstant.UserMessageType.MSG);
        }
        return userMessageRepository.selectMessageList(userMsgParamDTO, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message getMessage(long tenantId, long userId, long userMessageId) {
        UserMessageDTO message = userMessageRepository.selectMessage(tenantId, userId, userMessageId);
        Assert.notNull(message, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        readMessage(tenantId, userId, Collections.singletonList(userMessageId));
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMessage(long tenantId, long userId) {
        // 清理数据库数据
        List<UserMessage> userMessageList = userMessageRepository.selectByCondition(Condition.builder(UserMessage.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(UserMessage.FIELD_USER_ID, userId)
                        .andEqualTo(UserMessage.FIELD_READ_FLAG, BaseConstants.Flag.NO)
                ).build());
        if (!CollectionUtils.isEmpty(userMessageList)) {
            userMessageList.forEach(item -> item.setReadFlag(BaseConstants.Flag.YES));
            userMessageRepository.batchUpdateOptional(userMessageList, UserMessage.FIELD_READ_FLAG);
        }
        Long size = simpleMessageRedisHelper.zSetSize(HmsgConstant.USER_MESSAGE_ZSET + userId);
        if (size > 0) {
            simpleMessageRedisHelper.delKey(HmsgConstant.USER_MESSAGE_ZSET + userId);
            senWebSocket(tenantId, userId, BaseConstants.Digital.ZERO - size.intValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readMessage(long tenantId, long userId, List<Long> userMessageIdList) {
        List<UserMessage> userMessageList = userMessageRepository.selectByCondition(Condition.builder(UserMessage.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(UserMessage.FIELD_USER_ID, userId)
                        .andEqualTo(UserMessage.FIELD_READ_FLAG, BaseConstants.Flag.NO)
                        .andIn(UserMessage.FIELD_USER_MESSAGE_ID, userMessageIdList, true)
                ).build());
        if (!CollectionUtils.isEmpty(userMessageList)) {
            userMessageList.forEach(item -> item.setReadFlag(BaseConstants.Flag.YES));
            userMessageRepository.batchUpdateOptional(userMessageList, UserMessage.FIELD_READ_FLAG);
            for (UserMessage userMessage : userMessageList) {
                simpleMessageRedisHelper.zSetRemove(HmsgConstant.USER_MESSAGE_ZSET + userId, String.valueOf(userMessage.getUserMessageId()));
            }
        }
        senWebSocket(tenantId, userId, BaseConstants.Digital.ZERO - userMessageList.size());
    }

    @Override
    public void createSimpleMessage(long tenantId, long userId, SimpleMessageDTO simpleMessage) {
        simpleMessageRedisHelper.zSetAdd(HmsgConstant.USER_MESSAGE_ZSET + userId, String.valueOf(simpleMessage.getUserMessageId()), simpleMessage.getCreationDate().getTime());
        simpleMessageRedisHelper.setExpire(HmsgConstant.USER_MESSAGE_ZSET + userId, 30, TimeUnit.DAYS);
        // 发送webSocket信息
        senWebSocket(tenantId, userId, BaseConstants.Digital.ONE);
    }

    private void senWebSocket(Long tenantId, Long userId, int number) {
        Map<String, Object> map = new HashMap<>(BaseConstants.Digital.TWO);
        map.put(HmsgConstant.WebSocket.TENANT_ID, tenantId);
        map.put(HmsgConstant.WebSocket.NUMBER, number);
        try {
            messageClient.sendByUserId(userId, HmsgConstant.WebSocket.KEY, objectMapper.writeValueAsString(map));
        } catch (JsonProcessingException e) {
            throw new CommonException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(long tenantId, Long userId, List<Long> userMessageIdList) {
        // 将缓存站内消息已读
        readMessage(tenantId, userId, userMessageIdList);

        List<UserMessage> userMessageList = userMessageRepository.selectByCondition(Condition.builder(UserMessage.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(UserMessage.FIELD_USER_ID, userId)
                        .andIn(UserMessage.FIELD_USER_MESSAGE_ID, userMessageIdList)
                ).build());
        if (CollectionUtils.isEmpty(userMessageList)) {
            return;
        }
        userMessageRepository.batchDeleteByPrimaryKey(userMessageList);
    }

    @Override
    public void clearMessage(long tenantId, long userId) {
        Long size = simpleMessageRedisHelper.zSetSize(HmsgConstant.USER_MESSAGE_ZSET + userId);
        if (size > 0) {
            simpleMessageRedisHelper.delKey(HmsgConstant.USER_MESSAGE_ZSET + userId);
            senWebSocket(tenantId, userId, BaseConstants.Digital.ZERO - size.intValue());
        }
    }
}
