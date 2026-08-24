package org.hzero.boot.platform.event.provider;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.boot.platform.event.helper.RuleMatcher;
import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

/**
 * 事件处理提供器的抽象实现
 *
 * @author bergturing 2020/08/11 11:02
 */
public abstract class AbstractScheduleProvider implements ScheduleProvider {
    /**
     * 规则匹配器
     */
    private final RuleMatcher ruleMatcher;

    protected AbstractScheduleProvider(RuleMatcher ruleMatcher) {
        this.ruleMatcher = ruleMatcher;
    }

    @Override
    public ScheduleResult schedule(EventRuleVO eventRuleVO, Map<String, Object> condition, EventParam eventParam) {
        if (Objects.isNull(eventRuleVO)) {
            return ScheduleResult.failure("事件规则对象为空！！！");
        }

        // 判断是否启用
        if (!eventRuleVO.enabled()) {
            return ScheduleResult.failure("事件规则[%s]已禁用", eventRuleVO);
        }

        // 判断是否匹配当前
        if (!eventRuleVO.checkRulePass(this.ruleMatcher, condition)) {
            return ScheduleResult.failure("事件规则[%s]不匹配条件[%s]", eventRuleVO, condition);
        }

        try {
            // 调用并返回结果
            return ScheduleResult.success(this.doSchedule(eventRuleVO, eventParam));
        } catch (Exception e) {
            return ScheduleResult.failure(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * 执行处理
     *
     * @param eventRuleVO 事件规则值对象
     * @param eventParam  事件参数
     * @return 处理结果
     * @throws Exception 处理异常
     */
    protected abstract Object doSchedule(@Nonnull EventRuleVO eventRuleVO, EventParam eventParam) throws Exception;
}
