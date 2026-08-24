package org.hzero.message.infra.repository.impl;

import org.hzero.message.api.dto.UserSimpleDTO;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.infra.mapper.MessageReceiverMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息接受方 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Component
public class MessageReceiverRepositoryImpl extends BaseRepositoryImpl<MessageReceiver> implements MessageReceiverRepository {

    @Autowired
    private MessageReceiverMapper messageReceiverMapper;

    @Override
    public UserSimpleDTO getUser(Long userId) {
        return messageReceiverMapper.getUser(userId);
    }
}
