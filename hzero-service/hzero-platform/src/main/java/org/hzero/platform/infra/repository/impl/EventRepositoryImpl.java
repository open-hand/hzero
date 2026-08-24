package org.hzero.platform.infra.repository.impl;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.EventDTO;
import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.domain.entity.Event;
import org.hzero.platform.domain.repository.EventRepository;
import org.hzero.platform.domain.repository.EventRuleRepository;
import org.hzero.platform.infra.mapper.EventMapper;
import org.hzero.platform.infra.mapper.EventRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.mybatis.pagehelper.PageHelper;

/**
 * 事件 资源库实现
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 21:11
 */
@Component
public class EventRepositoryImpl extends BaseRepositoryImpl<Event> implements EventRepository  {

    @Autowired
    private EventRuleRepository eventRuleRepository;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private EventRuleMapper eventRuleMapper;

    @Override
    public Page<EventDTO> page(EventDTO event, int page, int size) {
        return PageHelper.doPage(page, size, () -> eventMapper.selectEvent(event));
    }

    @Override
    public EventDTO get(Long eventId) {
        EventDTO dto = new EventDTO();
        dto.setEventId(eventId);
        List<EventDTO> events = eventMapper.selectEvent(dto);
        if (CollectionUtils.isNotEmpty(events)) {
            dto = events.get(0);
            EventRuleDTO eventRuleDTO = new EventRuleDTO();
            eventRuleDTO.setEventId(eventId);
            dto.setRuleList(eventRuleMapper.selectEventRule(eventRuleDTO));
            return dto;
        }
        return null;
    }

    @Override
    public void remove(Long eventId) {
        // 删除事件规则
        eventRuleRepository.removeByEventId(eventId);
        // 删除事件
        this.deleteByPrimaryKey(eventId);
    }

    @Override
    @ProcessLovValue
    public List<EventDTO> export(EventDTO event, ExportParam exportParam, PageRequest pageRequest) {
        List<EventDTO> list = PageHelper.doPageAndSort(pageRequest, () -> eventMapper.selectEvent(event));
        if (exportParam.getSelection().contains(EventDTO.EVENT_EVENT_RULE_LIST)) {
            EventRuleDTO eventRuleDTO = new EventRuleDTO();
            for (EventDTO eventDTO : list) {
                eventRuleDTO.setEventId(eventDTO.getEventId());
                eventDTO.setRuleList(eventRuleMapper.selectEventRule(eventRuleDTO));
            }
        }
        return list;
    }
}
