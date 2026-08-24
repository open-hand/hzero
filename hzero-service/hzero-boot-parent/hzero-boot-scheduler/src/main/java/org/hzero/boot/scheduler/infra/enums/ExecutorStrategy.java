package org.hzero.boot.scheduler.infra.enums;

/**
 * 执行器策略
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/14 16:15
 */
public enum ExecutorStrategy {

    /**
     * 轮询
     */
    POLLING("POLLING"),
    /**
     * 执行器权重
     */
    WEIGHT("WEIGHT"),
    /**
     * 任务-执行器权重
     */
    JOB_WEIGHT("JOB_WEIGHT");

    private final String value;

    ExecutorStrategy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ExecutorStrategy{" +
                "value='" + value + '\'' +
                "} " + super.toString();
    }
}
