package org.hzero.platform.infra.repository.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.domain.entity.EventRule;
import org.hzero.platform.domain.repository.EventRuleRepository;
import org.hzero.platform.infra.mapper.EventRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 事件规则 资源库实现
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 21:38
 */
@Component
public class EventRuleRepositoryImpl extends BaseRepositoryImpl<EventRule> implements EventRuleRepository {

    @Autowired
    private EventRuleMapper eventRuleMapper;

    @Override
    public EventRuleDTO get(EventRuleDTO eventRuleDTO) {
        List<EventRuleDTO> ruleDTOList = eventRuleMapper.selectEventRule(eventRuleDTO);
        return CollectionUtils.isNotEmpty(ruleDTOList) ? ruleDTOList.get(0) : null;
    }

    @Override
    public void removeByEventId(Long eventId) {
        eventRuleMapper.removeByEventId(eventId);
    }
}
