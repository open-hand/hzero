package org.hzero.boot.platform.event.provider;

import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;

import java.util.Map;

/**
 * 事件处理提供器
 *
 * @author bergturing 2020/08/11 10:56
 */
public interface ScheduleProvider {
    /**
     * 当前提供器支持的类型
     *
     * @return 提供器支持的类型
     */
    String supportType();

    /**
     * 处理逻辑
     *
     * @param eventRuleVO 事件规则对象
     * @param condition   执行的条件
     * @param eventParam  事件参数
     * @return 处理结果
     */
    ScheduleResult schedule(EventRuleVO eventRuleVO, Map<String, Object> condition, EventParam eventParam);
}
