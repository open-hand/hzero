package org.hzero.report.infra.engine;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.engine.data.*;

/**
 * 简单报表html构建
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/02 20:04
 */
public class SimpleColumnReportBuilder extends AbstractReportBuilder implements ReportBuilder {

    SimpleColumnReportBuilder(AbstractReportDataSet reportDataSet, ReportParameter reportParameter) {
        super(reportDataSet, reportParameter);
    }

    @Override
    public ReportTable getTable() {
        return new ReportTable(this.tableRows.toString(), this.reportParameter.getSqlText(),
                this.reportDataSet.getMetaData().getRows().size(),
                this.reportDataSet.getMetaData().getColumns().size(),
                this.reportParameter.getSqlPageInfo().getSize(),
                this.reportDataSet.getMetaData().getMetaDataRowTotal());
    }

    @Override
    public void drawTableHeaderRows() {
        List<MetaDataColumn> columns = this.reportDataSet.getMetaData().getColumns().stream().sorted(Comparator.comparing(MetaDataColumn::getOrdinal)).collect(Collectors.toList());
        this.tableRows.append("<div class=\"hreport-table-head\"><table class=\"hreport\" id=\"hreport\" width=\"100%\" style=\"overflow: auto;word-break: break-all;word-wrap: break-word;table-layout: fixed;\"><thead>");
        this.tableRows.append("<tr class=\"hreport-header\">");
        for (MetaDataColumn column : columns) {
            if (column.getHidden() == BaseConstants.Flag.NO) {
                if (column.getWidth() > 0) {
                    this.tableRows.append(String.format("<th width=\"%spx\" title=\"%s\">%s</th>", column.getWidth(), column.getComment(), column.getText()));
                } else {
                    this.tableRows.append(String.format("<th title=\"%s\">%s</th>", column.getComment(), column.getText()));
                }
                totalWidth += column.getWidth();
            }
        }
        this.tableRows.append("</tr>");
        this.tableRows.append("</thead></table></div>");
    }

    @Override
    public void drawTableBodyRows() {
        List<MetaDataRow> rows = this.reportDataSet.getMetaData().getRows();
        List<MetaDataColumn> columns = reportDataSet.getMetaData().getColumns().stream().filter(item -> item.getHidden() == BaseConstants.Flag.NO).collect(Collectors.toList());
        this.tableRows.append("<div class=\"hreport-table-body\"><table class=\"hreport\" id=\"hreport\" width=\"100%\" style=\"overflow: auto;word-break: break-all;word-wrap: break-word;table-layout: fixed;\"><tbody>");
        for (MetaDataRow row : rows) {
            this.tableRows.append("<tr").append(" class=\"hreport-row\"").append(">");
            String[] data = StringUtils.splitPreserveAllTokens(row.getRowKey(), HrptConstants.SEPARATOR);
            for (int i = 0; i < row.getCells().size(); i++) {
                MetaDataColumn column = columns.get(i);
                if (column.getWidth() > 0) {
                    this.tableRows.append(String.format("<td class=\"hreport-fixed-column\" width=\"%spx\">%s</td>", column.getWidth(), data[i]));
                } else {
                    this.tableRows.append(String.format("<td class=\"hreport-fixed-column\">%s</td>", data[i]));
                }
            }
            this.tableRows.append("</tr>");
        }
        this.tableRows.append("</tbody></table></div>");
    }

    @Override
    public void drawTableFooterRows() {

    }
}
