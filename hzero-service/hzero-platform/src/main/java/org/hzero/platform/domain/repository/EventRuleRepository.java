package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.domain.entity.EventRule;

/**
 * 事件规则资源库
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 16:55
 */
public interface EventRuleRepository extends BaseRepository<EventRule> {

    /**
     * 查询事件规则
     * @param eventRuleDTO
     * @return
     */
    EventRuleDTO get(EventRuleDTO eventRuleDTO);

    /**
     * 根据时间ID删除事件规则
     * 
     * @param eventId 事件ID
     */
    void removeByEventId(Long eventId);
}
