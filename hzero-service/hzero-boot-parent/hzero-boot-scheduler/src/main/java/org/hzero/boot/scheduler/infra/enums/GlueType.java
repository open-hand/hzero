package org.hzero.boot.scheduler.infra.enums;

/**
 * 任务类型
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/14 16:25
 */
public enum GlueType {

    /**
     * 任务类型
     */
    SIMPLE("SIMPLE"),

    DATAFLOW("DATAFLOW"),

    SCRIPT("SCRIPT");

    private final String value;

    GlueType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GlueType{" +
                "value='" + value + '\'' +
                "} " + super.toString();
    }
}
