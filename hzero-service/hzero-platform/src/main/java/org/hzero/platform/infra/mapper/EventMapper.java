package org.hzero.platform.infra.mapper;

import java.util.List;

import org.hzero.platform.api.dto.EventDTO;
import org.hzero.platform.domain.entity.Event;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 事件Mapper
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 17:01
 */
public interface EventMapper extends BaseMapper<Event> {

    /**
     * 查询事件列表
     * 
     * @param event EventDTO
     * @return List<EventDTO>
     */
    List<EventDTO> selectEvent(EventDTO event);

}
