package org.hzero.iam.infra.constant;

/**
 * 处理状态
 *
 * @author allen.liu
 * @date 2018/12/5
 */
public enum ProcessStatus {
    PENDING("PENDING", "待定"),
    RUNNING("RUNNING", "运行中"),
    COMPLETE("COMPLETE", "完成"),
    ERROR("ERROR", "错误")
    ;

    private final String value;
    private final String desc;

    public String value() {
        return value;
    }

    public String desc() {
        return desc;
    }

    ProcessStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
