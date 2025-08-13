package org.hzero.message.infra.repository.impl;

import java.util.List;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.hzero.message.infra.mapper.UserMessageMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户消息 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Component
public class UserMessageRepositoryImpl extends BaseRepositoryImpl<UserMessage> implements UserMessageRepository {

    private UserMessageMapper userMessageMapper;

    @Autowired
    public UserMessageRepositoryImpl(UserMessageMapper userMessageMapper) {
        this.userMessageMapper = userMessageMapper;
    }

    @Override
    public int selectUnreadMessageCount(long tenantId, long userId) {
        Integer cnt = userMessageMapper.selectUnreadMessageCount(tenantId, userId);
        return cnt == null ? 0 : cnt;
    }

    @Override
    public Page<UserMessageDTO> selectMessageList(UserMsgParamDTO userMsgParamDTO, PageRequest pageRequest) {
        Integer readFlag = userMsgParamDTO.getReadFlag();
        if (readFlag != null && readFlag == 0) {
            return PageHelper.doPageAndSort(pageRequest, () -> userMessageMapper.selectNotReadMessageList(userMsgParamDTO));
        }
        return PageHelper.doPageAndSort(pageRequest, () -> userMessageMapper.selectMessageList(userMsgParamDTO));
    }

    @Override
    public Page<SimpleMessageDTO> selectSimpleMessageList(Long tenantId, long userId, String messageTypeCode, Integer readFlag, String messageCategoryCode, String messageSubcategoryCode, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> userMessageMapper.selectSimpleMessageList(tenantId, userId, messageTypeCode, readFlag, messageCategoryCode, messageSubcategoryCode));
    }

    @Override
    public UserMessageDTO selectMessage(long tenantId, long userId, long userMessageId) {
        return userMessageMapper.selectMessage(tenantId, userId, userMessageId);
    }

    @Override
    @ProcessLovValue
    public UserMessage getUserMessage(long userMessageId) {
        return userMessageMapper.getUserMessage(userMessageId);
    }

    @Override
    public List<Receiver> getAllUser(Long tenantId) {
        return userMessageMapper.getAllUser(tenantId);
    }

	@Override
	public List<SimpleMessageDTO> selectSimpleMsgByIds(List<Long> ids) {
		return userMessageMapper.selectSimpleMsgByIds(ids);
	}
}
