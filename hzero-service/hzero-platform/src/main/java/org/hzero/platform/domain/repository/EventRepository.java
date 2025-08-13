package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.EventDTO;
import org.hzero.platform.domain.entity.Event;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 事件资源库
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 16:55
 */
public interface EventRepository extends BaseRepository<Event> {

    /**
     * 分页查询
     * @param event
     * @param page
     * @param size
     * @return
     */
    Page<EventDTO> page(EventDTO event, int page, int size);

    /**
     * 获取事件规则
     * 
     * @param eventId 事件ID
     * @return Event detail
     */
    EventDTO get(Long eventId);

    /**
     * 删除事件
     * 
     * @param eventId 事件ID
     */
    void remove(Long eventId);

    /**
     * 导出数据
     */
    List<EventDTO> export(EventDTO event, ExportParam exportParam, PageRequest pageRequest);
}
