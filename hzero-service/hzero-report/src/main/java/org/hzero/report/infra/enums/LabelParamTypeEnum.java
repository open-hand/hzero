package org.hzero.report.infra.enums;

/**
 * 标签参数类型
 *
 * @author fanghan.liu 2019/12/04 17:45
 */
public enum LabelParamTypeEnum {

    /**
     * 文本
     */
    TEXT("TEXT"),
    /**
     * 图片
     */
    IMG("IMG"),
    /**
     * 条码
     */
    BAR_CODE("BARCODE"),
    /**
     * 二维码
     */
    QR_CODE("QRCODE");

    private final String value;

    LabelParamTypeEnum(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LabelParamTypeEnum valueOf2(String arg) {
        switch (arg) {
            case "IMG":
                return IMG;
            case "BARCODE":
                return BAR_CODE;
            case "QRCODE":
                return QR_CODE;
            case "TEXT":
            default:
                return TEXT;
        }
    }

}
