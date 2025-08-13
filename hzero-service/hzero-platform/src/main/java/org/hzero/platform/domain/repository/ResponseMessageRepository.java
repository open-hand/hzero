package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Message;

/**
 * 后端消息资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-08 15:04:28
 */
public interface ResponseMessageRepository extends BaseRepository<Message> {

    /**
     * 查询后端消息列表
     *
     * @param pageRequest 分页参数
     * @param message 消息实体
     * @return Page<Message>
     */
    Page<Message> selectMessageList(PageRequest pageRequest, Message message);

    /**
     * 查询后端消息明细
     *
     * @param messageId 主键
     * @return Message
     */
    Message selectMessageDetails(Long messageId);

    /**
     * 初始化所有返回信息到缓存中
     */
    void initAllReturnMessageToRedis();
}
