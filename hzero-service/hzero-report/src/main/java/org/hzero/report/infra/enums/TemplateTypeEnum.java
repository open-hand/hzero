package org.hzero.report.infra.enums;

/**
 * 报表列类型
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:23:55
 */
public enum TemplateTypeEnum {
    /**
     * RTF
     */
    RTF("rtf"),

    /**
     * DOC
     */
    DOC("doc"),

    /**
     * EXCEL
     */
    EXCEL("xls"),

    /**
     * HTML
     */
    HTML("html"),

    /**
     * NONE
     */
    NONE("none");

    private final String value;

    TemplateTypeEnum(final String value) {
        this.value = value;
    }

    public static TemplateTypeEnum valueOf2(String arg) {
        switch (arg) {
            case "rtf":
                return RTF;
            case "doc":
                return DOC;
            case "xls":
                return EXCEL;
            case "html":
                return HTML;
            default:
                return NONE;
        }
    }

    public static boolean isInEnum(String value) {
        for (TemplateTypeEnum columnType : TemplateTypeEnum.values()) {
            if (columnType.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.value;
    }
}
