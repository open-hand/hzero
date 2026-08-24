package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.Message;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 后端消息Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-08 15:04:28
 */
public interface ResponseMessageMapper extends BaseMapper<Message> {

    /**
     * 查询返回消息列表
     *
     * @param message message实体
     * @return List<Message>
     */
    List<Message> selectMessageList(Message message);
}
