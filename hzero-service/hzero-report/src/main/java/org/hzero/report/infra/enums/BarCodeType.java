package org.hzero.report.infra.enums;

/**
 * 条码类型枚举
 *
 * @author fanghan.liu 2019/12/06 10:06
 */
public enum BarCodeType {

    /**
     * code39
     */
    CODE_39("code39"),
    /**
     * code93
     */
    CODE_93("code93"),
    /**
     * code128
     */
    CODE_128("code128");

    private final String value;

    BarCodeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static BarCodeType value(String arg) {
        if (arg == null) {
            return CODE_128;
        }
        switch (arg) {
            case "code39":
                return CODE_39;
            case "code93":
                return CODE_93;
            case "code128":
            default:
                return CODE_128;
        }
    }
}
