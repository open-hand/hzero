package org.hzero.export.vo;

import java.util.Objects;

import org.hzero.export.annotation.ExcelSheet;
import org.hzero.export.constant.ExportConstants;

/**
 * ExcelSheet 注解属性
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/30 16:21
 */
public class ExcelSheetProperty {

    public ExcelSheetProperty() {
    }

    public ExcelSheetProperty(ExcelSheet excelSheet) {
        this.rowOffset = excelSheet.rowOffset();
        this.colOffset = excelSheet.colOffset();
        this.pageSize = excelSheet.pageSize();
        this.readOnly = excelSheet.readOnly();
    }

    /**
     * 行偏移量 从第几行开始显示数据 大于等于0
     */
    private int rowOffset = 0;
    /**
     * 列偏移量 从第几列开始显示数据 大于等于0
     */
    private int colOffset = 0;
    /**
     * 分页大小 每次查询的数量
     */
    private int pageSize = ExportConstants.PAGE_SIZE;
    /**
     * 是否只读
     */
    private boolean readOnly = false;

    public int getRowOffset() {
        return rowOffset;
    }

    public ExcelSheetProperty setRowOffset(int rowOffset) {
        this.rowOffset = rowOffset;
        return this;
    }

    public int getColOffset() {
        return colOffset;
    }

    public ExcelSheetProperty setColOffset(int colOffset) {
        this.colOffset = colOffset;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public ExcelSheetProperty setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public ExcelSheetProperty setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExcelSheetProperty that = (ExcelSheetProperty) o;
        return rowOffset == that.rowOffset &&
                colOffset == that.colOffset &&
                pageSize == that.pageSize &&
                readOnly == that.readOnly;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowOffset, colOffset, pageSize, readOnly);
    }

    @Override
    public String toString() {
        return "ExcelSheetProperty{" +
                "rowOffset=" + rowOffset +
                ", colOffset=" + colOffset +
                ", pageSize=" + pageSize +
                ", readOnly=" + readOnly +
                '}';
    }
}
