package org.hzero.message.domain.repository;

import org.hzero.message.api.dto.UserSimpleDTO;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 消息接受方资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface MessageReceiverRepository extends BaseRepository<MessageReceiver> {

    /**
     * 查询用户信息
     *
     * @param userId 用户Id
     * @return 用户信息
     */
    UserSimpleDTO getUser(Long userId);
}
