package org.hzero.export.constant;

import org.apache.commons.lang3.StringUtils;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.base.BaseConstants;

/**
 * 导出文件格式
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/06 10:30
 */
public enum FileType {
    /**
     * excel2003
     * 最大行数65536，预留几行
     */
    EXCEL_2003("EXCEL2003", ".xls", 65530),
    /**
     * excel2007
     * 最大行数1048575，预留几行
     */
    EXCEL_2007("EXCEL2007", ".xlsx", 1048570),
    /**
     * csv
     */
    CSV("CSV", ".csv", 1000000);

    private final String name;
    private final String suffix;
    private final int maxRow;

    FileType(String name, String suffix, int maxRow) {
        this.name = name;
        this.suffix = suffix;
        this.maxRow = maxRow;
    }

    public static FileType of(String name) {
        if (StringUtils.isBlank(name)) {
            return EXCEL_2007;
        }
        switch (name.toUpperCase()) {
            case "EXCEL2003":
                return EXCEL_2003;
            case "CSV":
                return CSV;
            case "EXCEL2007":
                return EXCEL_2007;
            default:
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getSuffix() {
        return suffix;
    }

    public int getMaxRow() {
        return maxRow;
    }
}