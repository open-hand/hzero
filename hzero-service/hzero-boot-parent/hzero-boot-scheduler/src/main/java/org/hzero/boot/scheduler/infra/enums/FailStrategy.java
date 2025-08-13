package org.hzero.boot.scheduler.infra.enums;

/**
 * 失败处理策略
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/14 16:15
 */
public enum FailStrategy {

    /**
     * 重试
     */
    RETRY("RETRY"),
    /**
     * 停止
     */
    IGNORE("IGNORE"),
    /**
     * 转移
     */
    TRANSFER("TRANSFER");

    private final String value;

    FailStrategy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FailStrategy{" +
                "value='" + value + '\'' +
                "} " + super.toString();
    }
}
