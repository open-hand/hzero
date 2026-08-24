package org.hzero.boot.platform.event.provider;

import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;

import java.util.Map;

/**
 * 事件调用提供器管理器
 *
 * @author bergturing 2020/08/11 11:54
 */
public interface ScheduleProviderManager {
    /**
     * 处理逻辑
     *
     * @param callType    调用方式
     * @param eventRuleVO 事件规则对象
     * @param condition   执行的条件
     * @param eventParam  方法调用或API调用的参数，如果调用方法没有参数，则为空。
     *                    如果是方法调用，使用 {@link org.hzero.boot.platform.event.vo.MethodParam} 封装参数，保持key和参数名称一致即可。
     *                    如果是API调用，使用 {@link org.hzero.boot.platform.event.vo.ApiParam} 封装参数。
     *                    如果是WebHook调用，使用{@link org.hzero.boot.platform.event.vo.WebHookParam} 封装参数。
     * @return 处理结果
     */
    ScheduleResult schedule(String callType, EventRuleVO eventRuleVO, Map<String, Object> condition, EventParam eventParam);
}
