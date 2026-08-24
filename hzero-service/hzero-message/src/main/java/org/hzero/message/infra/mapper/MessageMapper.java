package org.hzero.message.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 消息信息Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface MessageMapper extends BaseMapper<Message> {
    /**
     * 查询消息列表
     *
     * @param tenantId        租户ID
     * @param serverCode      账户代码
     * @param messageTypeCode 消息模板类型
     * @param subject         消息主题
     * @param trxStatusCode   消息状态
     * @param startDate       消息发送时间筛选，起始时间
     * @param endDate         消息发送时间筛选，结束时间
     * @param receiver        接收人
     * @return 消息列表
     */
    List<Message> selectMessage(@Param("tenantId") Long tenantId,
                                @Param("serverCode") String serverCode,
                                @Param("messageTypeCode") String messageTypeCode,
                                @Param("subject") String subject,
                                @Param("trxStatusCode") String trxStatusCode,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate,
                                @Param("receiver") String receiver);

    /**
     * 查询消息详细信息
     *
     * @param transactionId 事务ID
     * @return 消息详细信息
     */
    UserMessageDTO selectMessageDetails(@Param("transactionId") long transactionId);
}
