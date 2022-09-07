package org.hzero.message.app.service;

import java.util.List;

import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;
import org.hzero.message.domain.entity.Message;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 消息客户端接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/1 星期三 16:20
 */
public interface UserMessageService {
    /**
     * 未读消息
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 未读消息的数量
     */
    Long countUnreadMessage(long tenantId, long userId);

    /**
     * 未读消息
     *
     * @param tenantId            租户ID
     * @param userId              用户ID
     * @param previewMessageCount 预览消息数量，不传取默认数量
     * @param withContent         是否返回消息内容
     * @return 分页消息列表
     */
    List<SimpleMessageDTO> listSimpleMessage(long tenantId, long userId, Integer previewMessageCount, boolean withContent);

    /**
     * 分页查询消息列表
     *
     * @param userMsgParamDTO 查询条件
     * @param pageRequest     分页
     * @return 分页消息列表
     */
    Page<UserMessageDTO> listMessage(UserMsgParamDTO userMsgParamDTO, PageRequest pageRequest);

    /**
     * 查询消息详情
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     * @param userMessageId 消息ID
     * @return 消息详情
     */
    Message getMessage(long tenantId, long userId, long userMessageId);

    /**
     * 更新全部消息已读
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     */
    void readMessage(long tenantId, long userId);

    /**
     * 更新指定消息已读
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     * @param messageIdList 指定消息ID
     */
    void readMessage(long tenantId, long userId, List<Long> messageIdList);

    /**
     * 添加缓存
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     * @param simpleMessage 消息
     */
    void createSimpleMessage(long tenantId, long userId, SimpleMessageDTO simpleMessage);

    /**
     * 删除指定消息
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     * @param messageIdList 指定消息ID
     */
    void deleteMessage(long tenantId, Long userId, List<Long> messageIdList);
    
    /**
     * 清除消息缓存
     *
     * @param tenantId      租户ID
     * @param userId        用户ID
     */
    void clearMessage(long tenantId, long userId);
}
