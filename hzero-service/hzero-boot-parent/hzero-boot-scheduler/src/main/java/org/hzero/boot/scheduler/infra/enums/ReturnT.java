package org.hzero.boot.scheduler.infra.enums;

/**
 * 任务返回状态
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/16 16:22
 */
@SuppressWarnings("unused")
public enum ReturnT {

    /**
     * 任务状态
     */
    SUCCESS("success"),

    FAILURE("failure");

    private final String value;

    ReturnT(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ReturnT{" +
                "value='" + value + '\'' +
                "} " + super.toString();
    }
}
