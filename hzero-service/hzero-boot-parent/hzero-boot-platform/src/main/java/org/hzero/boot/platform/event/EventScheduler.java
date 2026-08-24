package org.hzero.boot.platform.event;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hzero.boot.platform.event.provider.ScheduleProviderManager;
import org.hzero.boot.platform.event.repository.EventRuleRepository;
import org.hzero.boot.platform.event.vo.ApiParam;
import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;
import org.hzero.boot.platform.event.vo.MethodParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 事件调度核心类，事件调度入口。<br/>
 * 被调度的方法自己保证事务一致性，调度器不处理事务。<br/>
 * 根据事件规则顺序，匹配条件的规则会被执行，支持方法调用和API调用，支持同步和异步调用。<br/>
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 10:29
 */
public class EventScheduler {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventScheduler.class);

    /**
     * 事件规则仓库对象
     */
    private final EventRuleRepository eventRuleRepository;

    /**
     * 事件处理提供器管理器对象
     */
    private final ScheduleProviderManager scheduleProviderManager;

    public EventScheduler(EventRuleRepository eventRuleRepository,
                          ScheduleProviderManager scheduleProviderManager) {
        this.eventRuleRepository = eventRuleRepository;
        this.scheduleProviderManager = scheduleProviderManager;
    }

    /**
     * 根据事件编码查询事件规则，按规则顺序，根据 condition 判断规则是否通过，通过则调用规则的方法或API。
     *
     * @param eventCode 事件编码
     * @param condition 规则匹配条件
     * @param params    方法调用或API调用的参数，如果调用方法没有参数，则为空。<br/>
     *
     *                  <pre>
     *                                                                                                                                                                                                                      如果是方法调用，使用 {@link MethodParam} 封装参数，保持key和参数名称一致即可。<br/>
     *                                                                                                                                                                                                                      如果是API调用，使用 {@link ApiParam} 封装参数。<br/>
     *                                                                                                                                                                                                                    </pre>
     * @return 如果某个同步调用需要返回调用结果则返回，异步调用不会返回。如果有多个同步调用需要返回结果，则按顺序返回最后一个调用结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Object scheduler(String eventCode, Map<String, Object> condition, EventParam... params) {
        Assert.notNull(eventCode, "eventCode must not be null.");
        // 根据事件编码查找事件规则
        List<EventRuleVO> eventRuleList = eventRuleRepository.findByEventCode(eventCode);
        if (CollectionUtils.isEmpty(eventRuleList)) {
            LOGGER.warn(">>>>> 事件编码[{}]没有查询到事件规则", eventCode);
            return null;
        }
        LOGGER.info(">>>>> 事件调度开始. 事件编码[{}]", eventCode);

        // 按序号从小到大排序
        eventRuleList.sort(Comparator.comparingInt(EventRuleVO::getOrderSeq));

        Object result = null;
        String callType;
        Map<String, EventParam> eventParamMap = this.initEventParam(params);
        for (EventRuleVO eventRule : eventRuleList) {
            // 调度类型
            callType = eventRule.getCallType();
            // 处理规则
            result = Optional.ofNullable(this.scheduleProviderManager
                    .schedule(callType, eventRule, condition, eventParamMap.get(callType))
                    .result(obj -> LOGGER.debug(obj.toString()), LOGGER::debug))
                    .orElse(result);
        }

        LOGGER.info(">>>>> 事件调度结束. 事件编码[{}]", eventCode);
        return result;
    }

    /**
     * 初始化事件参数(进行同类别的参数合并，靠后的同类别同名参数会覆盖靠前的同类别同名参数)
     *
     * @param eventParams 传入的事件参数
     * @return 初始化后的事件参数
     */
    private Map<String, EventParam> initEventParam(EventParam[] eventParams) {
        // 事件参数map
        Map<String, EventParam> eventParamMap = new HashMap<>(8);

        if (ArrayUtils.isNotEmpty(eventParams)) {
            Arrays.stream(eventParams)
                    .collect(Collectors.groupingBy(EventParam::getType,
                            Collectors.reducing((baseParam, newParam) -> {
                                baseParam.putAll(newParam);
                                return baseParam;
                            })))
                    .forEach((key, value) -> {
                        value.ifPresent(eventParam -> eventParamMap.put(key, eventParam));
                    });
        }

        // 返回结果
        return eventParamMap;
    }
}
