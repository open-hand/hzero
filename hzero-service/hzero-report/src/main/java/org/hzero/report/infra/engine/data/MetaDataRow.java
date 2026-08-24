package org.hzero.report.infra.engine.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.core.base.BaseConstants;
import org.hzero.report.infra.constant.HrptConstants;

/**
 * 报表元数据行类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午6:58:47
 */
public class MetaDataRow {
    private final Map<String, MetaDataCell> cells = new HashMap<>(BaseConstants.Digital.SIXTEEN);
    private final StringBuilder rowKeyBuilder = new StringBuilder();

    private String asyncReportUuid;

    public MetaDataRow add(final MetaDataCell cell) {
        this.cells.put(cell.getName(), cell);
        if (!HrptConstants.ColumnType.STATISTICAL.equals(cell.getColumn().getType())) {
            final Object cellValue = cell.getValue();
            this.rowKeyBuilder.append((cellValue == null) ? "" : cellValue.toString().trim());
            this.rowKeyBuilder.append(HrptConstants.SEPARATOR);
        }
        return this;
    }

    public MetaDataRow addAll(final List<MetaDataCell> cells) {
        cells.forEach(this::add);
        return this;
    }

    public Map<String, MetaDataCell> getCells() {
        return this.cells;
    }

    public MetaDataCell getCell(final String name) {
        return this.cells.get(name);
    }

    public Object getCellValue(final String name) {
        final MetaDataCell cell = this.cells.get(name);
        return (cell == null) ? null : cell.getValue();
    }

    public String getRowKey() {
        return this.rowKeyBuilder.toString();
    }

    public String getAsyncReportUuid() {
        return asyncReportUuid;
    }

    public MetaDataRow setAsyncReportUuid(String asyncReportUuid) {
        this.asyncReportUuid = asyncReportUuid;
        return this;
    }
}
