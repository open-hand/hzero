package org.hzero.message.domain.repository;

import java.util.Date;
import java.util.List;

import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息信息资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface MessageRepository extends BaseRepository<Message> {
    /**
     * 分页查询消息列表
     *
     * @param tenantId        租户ID
     * @param serverCode      账户代码
     * @param messageTypeCode 消息模板类型
     * @param subject         消息主题
     * @param trxStatusCode   消息状态
     * @param startDate       消息发送时间筛选，起始时间
     * @param endDate         消息发送时间筛选，结束时间
     * @param receiver        接收人
     * @param pageRequest     分页
     * @return 分页消息列表
     */
    Page<Message> selectMessage(Long tenantId,
                                String serverCode,
                                String messageTypeCode,
                                String subject,
                                String trxStatusCode,
                                Date startDate,
                                Date endDate,
                                String receiver,
                                PageRequest pageRequest);

    /**
     * 查询消息详细信息
     *
     * @param transactionId 事务ID
     * @return 消息详细信息
     */
    UserMessageDTO selectMessageDetails(long transactionId);

    /**
     * 查询最近的消息
     *
     * @param tenantId    租户ID
     * @param messageType 消息类型
     * @param after       多久之后
     * @return 最近的消息
     */
    List<Message> listRecentMessage(Long tenantId, String messageType, Date after);
}
