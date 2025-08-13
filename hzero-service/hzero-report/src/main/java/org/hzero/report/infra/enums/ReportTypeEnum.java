package org.hzero.report.infra.enums;

/**
 * 报表类型
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:23:55
 */
public enum ReportTypeEnum {

    /**
     * 简单表格
     */
    SIMPLE_TABLE("ST"),
    /**
     * 复杂表格
     */
    TABLE("T"),
    /**
     * 图表报表
     */
    CHART("C"),
    /**
     * 模板报表
     */
    DOCUMENT("D"),
    /**
     * ureport报表
     */
    UREPORT("U");

    private final String value;

    ReportTypeEnum(final String value) {
        this.value = value;
    }

    public static ReportTypeEnum valueOf2(String arg) {
        switch (arg) {
            case "C":
                return CHART;
            case "D":
                return DOCUMENT;
            case "U":
                return UREPORT;
            case "ST":
                return SIMPLE_TABLE;
            case "T":
            default:
                return TABLE;
        }
    }

    public String getValue() {
        return this.value;
    }
}
