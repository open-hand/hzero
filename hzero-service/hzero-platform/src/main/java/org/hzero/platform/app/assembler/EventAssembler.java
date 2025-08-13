package org.hzero.platform.app.assembler;

import org.hzero.platform.api.dto.EventDTO;
import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.domain.entity.Event;
import org.hzero.platform.domain.entity.EventRule;
import org.springframework.beans.BeanUtils;

/**
 * 事件DTO组装器
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/20 20:20
 */
public class EventAssembler {

    private EventAssembler(){}

    public static EventDTO eventEntityToDto(Event event) {
        EventDTO dto = new EventDTO();
        BeanUtils.copyProperties(event, dto);
        return dto;
    }

    public static EventRuleDTO eventRuleEntityToDto(EventRule eventRule) {
        EventRuleDTO dto = new EventRuleDTO();
        BeanUtils.copyProperties(eventRule, dto);
        return dto;
    }

}
