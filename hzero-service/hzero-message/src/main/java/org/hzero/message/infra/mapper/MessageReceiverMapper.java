package org.hzero.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.api.dto.UserSimpleDTO;
import org.hzero.message.domain.entity.MessageReceiver;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 消息接受方Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface MessageReceiverMapper extends BaseMapper<MessageReceiver> {

    /**
     * 查询用户信息
     *
     * @param userId 用户Id
     * @return 用户信息
     */
    UserSimpleDTO getUser(@Param("userId") Long userId);

    /**
     * 通过消息查询接收人
     *
     * @param messageIds 消息ID列表
     * @return 接收人
     */
    List<MessageReceiver> listReceiver(@Param("messageIds") List<Long> messageIds);
}
