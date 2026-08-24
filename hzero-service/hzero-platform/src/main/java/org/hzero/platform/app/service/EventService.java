package org.hzero.platform.app.service;

import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.domain.entity.Event;
import org.hzero.platform.domain.entity.EventRule;

import java.util.List;

/**
 * 事件应用服务
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 16:53
 */
public interface EventService {

    /**
     * 创建事件
     *
     * @param event 事件
     * @return
     */
    Event create(Event event);

    /**
     * 更新事件
     *
     * @param event 事件
     * @return
     */
    Event update(Event event);

    /**
     * 删除事件
     *
     * @param eventId 事件ID not null.
     */
    void remove(Long eventId);

    /**
     * 批量删除事件
     *
     * @param events 事件
     */
    void batchRemove(List<Event> events);

    /**
     * 创建事件规则
     *
     * @param eventId   事件ID
     * @param eventRule 事件规则
     * @return 事件规则
     */
    EventRule createEventRule(Long eventId, EventRule eventRule);

    /**
     * 修改事件规则
     *
     * @param eventId   事件ID
     * @param eventRule 事件规则
     * @return 事件规则
     */
    EventRule updateEventRule(Long eventId, EventRule eventRule);

    /**
     * 批量删除事件规则
     *
     * @param eventId    事件ID
     * @param eventRules 事件规则ID集合
     */
    void batchRemoveEventRule(Long eventId, List<EventRule> eventRules);

    /**
     * 批量操作
     *
     * @param eventId    事件ID
     * @param eventRules 待操作的事件规则
     * @return 操作结果数据
     */
    List<EventRuleDTO> batch(Long eventId, List<EventRule> eventRules);
}
