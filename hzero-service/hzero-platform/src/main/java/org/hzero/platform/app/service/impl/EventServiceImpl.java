package org.hzero.platform.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.app.assembler.EventAssembler;
import org.hzero.platform.app.service.EventService;
import org.hzero.platform.domain.entity.Event;
import org.hzero.platform.domain.entity.EventRule;
import org.hzero.platform.domain.repository.EventRepository;
import org.hzero.platform.domain.repository.EventRuleRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 事件应用服务默认实现
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 16:54
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventRuleRepository eventRuleRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Event create(Event event) {
        event.validate(eventRepository);
        eventRepository.insertSelective(event);
        event.refreshCache(eventRuleRepository, redisHelper, objectMapper);
        return event;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Event update(Event event) {
        Event entity = eventRepository.selectByPrimaryKey(event.getEventId());
        entity.equalsEventCode(event);
        eventRepository.updateByPrimaryKeySelective(event);
        event.refreshCache(eventRuleRepository, redisHelper, objectMapper);
        return event;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long eventId) {
        Event event = eventRepository.selectByPrimaryKey(eventId);
        Assert.notNull(event, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 删除事件
        eventRepository.remove(eventId);
        // 清除缓存
        event.clearCache(redisHelper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemove(List<Event> events) {
        Long[] eventIds =
                events.stream().map(Event::getEventId).collect(Collectors.toList())
                        .toArray(ArrayUtils.EMPTY_LONG_OBJECT_ARRAY);
        if (ArrayUtils.isNotEmpty(eventIds)) {
            for (Long eventId : eventIds) {
                remove(eventId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventRule createEventRule(Long eventId, EventRule eventRule) {
        Event event = eventRepository.selectByPrimaryKey(eventId);
        Assert.notNull(event, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 创建事件规则
        this.createEventRule(eventId, event, eventRule);
        event.refreshCache(eventRuleRepository, redisHelper, objectMapper);
        return eventRule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventRule updateEventRule(Long eventId, EventRule eventRule) {
        Event event = eventRepository.selectByPrimaryKey(eventId);
        Assert.notNull(event, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 更新事件规则
        this.updateEventRule(eventId, event, eventRule);
        event.refreshCache(eventRuleRepository, redisHelper, objectMapper);
        return eventRule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveEventRule(Long eventId, List<EventRule> eventRules) {
        Event event = eventRepository.selectByPrimaryKey(eventId);
        Assert.notNull(event, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 删除事件规则
        eventRules.forEach(eventRule -> this.deleteEventRule(eventId, eventRule));
        event.refreshCache(eventRuleRepository, redisHelper, objectMapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<EventRuleDTO> batch(Long eventId, List<EventRule> eventRules) {
        // 结果对象
        List<EventRuleDTO> eventRuleDtos = Collections.emptyList();

        if (CollectionUtils.isNotEmpty(eventRules)) {
            // 查询事件对象
            Event event = this.eventRepository.selectByPrimaryKey(eventId);
            Assert.notNull(event, BaseConstants.ErrorCode.DATA_NOT_EXISTS);

            // 处理事件规则
            eventRuleDtos = eventRules.stream()
                    // 处理数据，并返回结果
                    .map(eventRule -> {
                        switch (eventRule.get_status()) {
                            // 创建
                            case create:
                                // 创建并转换
                                return this.createEventRule(eventId, event, eventRule);
                            // 更新
                            case update:
                                // 更新事件规则
                                return this.updateEventRule(eventId, event, eventRule);
                            // 删除
                            case delete:
                                this.deleteEventRule(eventId, eventRule);
                                return null;
                            default:
                                return null;
                        }
                    }).filter(Objects::nonNull)
                    // 转换成dto
                    .map(EventAssembler::eventRuleEntityToDto)
                    .collect(Collectors.toList());

            // 刷新缓存
            event.refreshCache(eventRuleRepository, redisHelper, objectMapper);
        }

        // 返回结果
        return eventRuleDtos;
    }

    /**
     * 创建事件规则
     *
     * @param eventId   事件ID
     * @param event     事件对象
     * @param eventRule 事件规则数据
     */
    private EventRule createEventRule(@Nonnull Long eventId, @Nonnull Event event, @Nonnull EventRule eventRule) {
        eventRule.setEventId(eventId);
        eventRule.setTenantId(event.getTenantId());
        eventRule.validate();
        this.eventRuleRepository.insertSelective(eventRule);
        return eventRule;
    }

    /**
     * 更新事件规则
     *
     * @param eventId   事件ID
     * @param event     事件对象
     * @param eventRule 事件规则数据
     */
    private EventRule updateEventRule(@Nonnull Long eventId, @Nonnull Event event, @Nonnull EventRule eventRule) {
        eventRule.setTenantId(event.getTenantId());
        eventRule.setEventId(eventId);
        eventRule.validate();
        this.eventRuleRepository.updateByPrimaryKeySelective(eventRule);
        return eventRule;
    }

    /**
     * 删除事件规则
     *
     * @param eventId   事件ID
     * @param eventRule 事件规则数据
     */
    private void deleteEventRule(@Nonnull Long eventId, @Nonnull EventRule eventRule) {
        // 校验事件ID与事件规则行中事件ID是否一致
        Assert.isTrue(Objects.equals(eventRule.getEventId(), eventId),
                HpfmMsgCodeConstants.ERROR_EVENT_NOT_MATCH);
        // 删除数据
        this.eventRuleRepository.deleteByPrimaryKey(eventRule);
    }
}
