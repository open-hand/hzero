package org.hzero.iam.infra.constant;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/9/7
 */
public enum CheckedFlag {
    /**
     * 选中
     */
    CHECKED("Y"),
    /**
     * 未选中
     */
    UNCHECKED("N"),
    /**
     * 部分选中
     */
    PARTIAL_CHECKED("P");

    private final String value;
    private CheckedFlag(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
