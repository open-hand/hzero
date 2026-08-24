package org.hzero.boot.imported.infra.enums;

/**
 * 数据状态
 *
 * @author chunqiang.bai@hand-china.com
 */
public enum DataStatus {
    /**
     * Excel导入
     */
    NEW("Excel导入"),
    /**
     * 数据异常
     */
    ERROR("数据异常"),
    /**
     * 验证成功
     */
    VALID_SUCCESS("验证成功"),
    /**
     * 验证失败
     */
    VALID_FAILED("验证失败"),
    /**
     * 导入成功
     */
    IMPORT_SUCCESS("导入成功"),
    /**
     * 导入失败
     */
    IMPORT_FAILED("导入失败");

    private String value;

    DataStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
