package org.hzero.export.constant;

/**
 * 导出类型
 *
 * @author bojiangzhou 2018/07/25
 */
public enum ExportType {
    /**
     * 导出列
     */
    COLUMN,
    /**
     * 导出模板
     */
    TEMPLATE,
    /**
     * 导出数据
     */
    DATA;

    public static boolean match(ExportType type) {
        for (ExportType value : ExportType.values()) {
            if (value.equals(type)) {
                return true;
            }
        }
        return false;
    }
}
