package org.hzero.report.infra.engine.data;

import org.hzero.report.infra.util.NumberFormatUtils;

/**
 * 报表数据单元格
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:17:55
 */
public class ReportDataCell {
    private ReportDataColumn column;
    private String name;
    private Object value;

    public ReportDataCell(ReportDataColumn column, String name, Object value) {
        this.column = column;
        this.name = name;
        this.value = value;
    }

    public ReportDataColumn getColumn() {
        return this.column;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        int decimals = this.column.getMetaData().getDecimals();
        if (this.column.getMetaData().getPercent() == 1) {
            decimals = decimals <= 0 ? 2 : decimals;
            return NumberFormatUtils.percentFormat(this.value, decimals);
        }
        if ("DECIMAL".equals(this.column.getMetaData().getDataType())
                || "DOUBLE".equals(this.column.getMetaData().getDataType())
                || "FLOAT".equals(this.column.getMetaData().getDataType())) {
            decimals = decimals <= 0 ? 4 : decimals;
            return NumberFormatUtils.decimalFormat(this.value, decimals);
        }
        return NumberFormatUtils.format(this.value);
    }
}
