package org.hzero.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;

import java.util.Date;
import java.util.List;

/**
 * 消息信息应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface MessageService {

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
    Page<Message> listMessage(Long tenantId,
                              String serverCode,
                              String messageTypeCode,
                              String subject,
                              String trxStatusCode,
                              Date startDate,
                              Date endDate,
                              String receiver,
                              PageRequest pageRequest);

    /**
     * 查询消息详情
     *
     * @param tenantId  租户ID
     * @param messageId 消息ID
     * @return 消息详情
     */
    Message getMessage(Long tenantId, long messageId);

    /**
     * 分页查询消息接收者列表
     *
     * @param messageId   消息ID
     * @param pageRequest 分页
     * @return 分页消息接收者列表
     */
    Page<MessageReceiver> listMessageReceiver(Long tenantId, long messageId, PageRequest pageRequest);

    /**
     * 查询消息错误信息
     *
     * @param transactionId 错误信息ID
     * @return 消息错误信息
     */
    MessageTransaction getMessageTransaction(Long tenantId, long transactionId);

    /**
     * 查询最近修改消息
     *
     * @param tenantId    租户ID
     * @param messageType 消息类型
     * @param before      多久内
     * @return 最近修改消息
     */
    List<Message> listRecentMessage(Long tenantId, String messageType, long before);

    /**
     * 消息重发
     *
     * @param transactionId 事务ID
     * @return 消息
     */
    Message resendMessage(Long tenantId, long transactionId);

    /**
     * 批量删除
     *
     * @param messageList 消息
     */
    void batchDelete(List<Message> messageList);
}
