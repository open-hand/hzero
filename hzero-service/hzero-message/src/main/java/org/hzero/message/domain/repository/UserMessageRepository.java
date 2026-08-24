package org.hzero.message.domain.repository;

import java.util.List;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户消息资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface UserMessageRepository extends BaseRepository<UserMessage> {
    /**
     * 查询未读消息数量
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 未读消息数量
     */
    int selectUnreadMessageCount(long tenantId, long userId);

    /**
     * 分页查询消息列表
     *
     * @param userMsgParamDTO 查询条件
     * @param pageRequest     分页
     * @return 分页消息列表
     */
    Page<UserMessageDTO> selectMessageList(UserMsgParamDTO userMsgParamDTO, PageRequest pageRequest);

    /**
     * 分页查询消息列表
     *
     * @param tenantId               租户ID
     * @param userId                 用户ID
     * @param messageTypeCode        消息类型，值集 HMSG.MESSAGE_TYPE
     * @param readFlag               已读/未读标记
     * @param messageCategoryCode    消息类别
     * @param messageSubcategoryCode 消息子类别
     * @param pageRequest            分页
     * @return 分页消息列表
     */
    Page<SimpleMessageDTO> selectSimpleMessageList(Long tenantId, long userId, String messageTypeCode, Integer readFlag, String messageCategoryCode, String messageSubcategoryCode, PageRequest pageRequest);

    /**
     * 查询消息详情
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     * @param userMessageId 消息ID
     * @return 消息详情
     */
    UserMessageDTO selectMessage(long tenantId, long userId, long userMessageId);

    UserMessage getUserMessage(long userMessageId);

    List<Receiver> getAllUser(Long tenantId);

    List<SimpleMessageDTO> selectSimpleMsgByIds(List<Long> ids);
}
