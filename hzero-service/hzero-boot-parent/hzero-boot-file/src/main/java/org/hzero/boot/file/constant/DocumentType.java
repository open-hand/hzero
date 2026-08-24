package org.hzero.boot.file.constant;

/**
 * onlyOffice文档类型
 *
 * @author fanghan.liu 2020/05/26 16:35
 */
public enum DocumentType {

    /**
     * 文本类型
     */
    TEXT("text"),
    /**
     * 电子表格类型
     */
    SPREADSHEET("spreadsheet"),
    /**
     * 展示类型
     */
    PRESENTATION("presentation");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public static String getDocumentType(String fileType) {
        switch (fileType) {
            case "doc":
            case "docm":
            case "docx":
            case "dot":
            case "dotm":
            case "dotx":
            case "epub":
            case "fodt":
            case "html":
            case "mht":
            case "odt":
            case "ott":
            case "pdf":
            case "rtf":
            case "txt":
            case "xps":
                return TEXT.value;
            case "csv":
            case "fods":
            case "ods":
            case "ots":
            case "xls":
            case "xlsm":
            case "xlsx":
            case "xlt":
            case "xltm":
            case "xltx":
                return SPREADSHEET.value;
            case "fodp":
            case "odp":
            case "otp":
            case "pot":
            case "potm":
            case "potx":
            case "pps":
            case "ppsm":
            case "ppsx":
            case "ppt":
            case "pptm":
            case "pptx":
                return PRESENTATION.value;
            default:
                return TEXT.value;
        }
    }

}
