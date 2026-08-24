package org.hzero.boot.platform.event.provider;

import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 事件处理提供器管理器默认实现
 *
 * @author bergturing 2020/08/11 11:55
 */
public class DefaultScheduleProviderManager implements ScheduleProviderManager {
    /**
     * 事件处理提供器
     */
    private final Map<String, ScheduleProvider> providers;

    public DefaultScheduleProviderManager(List<ScheduleProvider> scheduleProviderList) {
        this.providers = Optional.ofNullable(scheduleProviderList).orElse(Collections.emptyList())
                .stream().collect(Collectors.toMap(ScheduleProvider::supportType, p -> p));
    }


    @Override
    public ScheduleResult schedule(String callType, EventRuleVO eventRuleVO, Map<String, Object> condition, EventParam eventParam) {
        if (this.providers.containsKey(callType)) {
            // 调度
            return this.providers.get(callType).schedule(eventRuleVO, condition, eventParam);
        }

        return ScheduleResult.failure("未找到事件规则对应调用方法的处理方式: %s", callType);
    }
}
