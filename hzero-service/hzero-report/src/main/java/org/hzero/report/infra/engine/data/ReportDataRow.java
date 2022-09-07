package org.hzero.report.infra.engine.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.core.base.BaseConstants;

/**
 * 报表数据行类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:17:20
 */
public class ReportDataRow {
    private final Map<String, ReportDataCell> cells = new HashMap<>(BaseConstants.Digital.SIXTEEN);

    public ReportDataRow add(final ReportDataCell cell) {
        this.cells.put(cell.getName(), cell);
        return this;
    }

    public ReportDataRow addAll(final List<ReportDataCell> cells) {
        cells.forEach(this::add);
        return this;
    }

    public Map<String, ReportDataCell> getCells() {
        return this.cells;
    }

    public ReportDataCell getCell(final String name) {
        return this.cells.get(name);
    }

    public Object getCellValue(final String name) {
        final ReportDataCell cell = this.cells.get(name);
        return (cell == null) ? "" : cell.getValue();
    }
}
