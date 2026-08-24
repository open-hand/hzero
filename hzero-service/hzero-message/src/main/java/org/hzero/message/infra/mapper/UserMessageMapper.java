package org.hzero.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;
import org.hzero.message.domain.entity.UserMessage;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 用户消息Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    /**
     * 查询未读消息数量
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 未读消息数量
     */
    Integer selectUnreadMessageCount(@Param("tenantId") long tenantId, @Param("userId") long userId);

    /**
     * 查询消息列表
     *
     * @param userMsgParamDTO 查询参数
     * @return 消息列表
     */
    List<UserMessageDTO> selectMessageList(UserMsgParamDTO userMsgParamDTO);

    /**
     * 查询未读消息列表
     *
     * @param userMsgParamDTO 查询参数
     * @return 消息列表
     */
    List<UserMessageDTO> selectNotReadMessageList(UserMsgParamDTO userMsgParamDTO);

    /**
     * 查询消息列表
     *
     * @param tenantId               租户ID
     * @param userId                 用户ID
     * @param messageTypeCode        消息类型，值集 HMSG.MESSAGE_TYPE
     * @param readFlag               已读/未读标记
     * @param messageCategoryCode    消息类别
     * @param messageSubcategoryCode 消息子类别
     * @return 消息列表
     */
    List<SimpleMessageDTO> selectSimpleMessageList(@Param("tenantId") Long tenantId,
                                                   @Param("userId") long userId,
                                                   @Param("messageTypeCode") String messageTypeCode,
                                                   @Param("readFlag") Integer readFlag,
                                                   @Param("messageCategoryCode") String messageCategoryCode,
                                                   @Param("messageSubcategoryCode") String messageSubcategoryCode);

    /**
     * 查询消息详情
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     * @param userMessageId 消息ID
     * @return 消息详情
     */
    UserMessageDTO selectMessage(@Param("tenantId") long tenantId, @Param("userId") long userId, @Param("userMessageId") long userMessageId);

    UserMessage getUserMessage(@Param("userMessageId") long userMessageId);

    List<Receiver> getAllUser(@Param("tenantId") Long tenantId);
    
    List<SimpleMessageDTO> selectSimpleMsgByIds(@Param("ids") List<Long> ids);
}
