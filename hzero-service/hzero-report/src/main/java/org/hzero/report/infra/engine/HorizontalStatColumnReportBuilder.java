package org.hzero.report.infra.engine;

import java.util.List;
import java.util.Map;

import org.hzero.report.infra.engine.data.*;

/**
 * 横向展示统计列的报表生成类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:46:20
 */
public class HorizontalStatColumnReportBuilder extends AbstractReportBuilder implements ReportBuilder {

    /**
     * 横向展示统计列的报表生成类
     *
     * @param reportDataSet   报表数据集
     * @param reportParameter 报表参数
     */
    HorizontalStatColumnReportBuilder(AbstractReportDataSet reportDataSet, ReportParameter reportParameter) {
        super(reportDataSet, reportParameter);
    }

    @Override
    public void drawTableBodyRows() {
        ColumnTree leftFixedColumnTree = this.reportDataSet.getBodyLeftFixedColumnTree();
        List<ColumnTreeNode> rowNodes = leftFixedColumnTree.getLastLevelNodes();
        List<ColumnTreeNode> columnNodes = this.reportDataSet.getBodyRightColumnNodes();
        Map<String, ReportDataRow> statRowMap = this.reportDataSet.getRowMap();
        List<ReportDataColumn> statColumns = this.reportDataSet.getEnabledStatColumns();
        Map<String, ColumnTreeNode> treeNodePathMap = this.getTreeNodePathMap(leftFixedColumnTree);

        int rowIndex = 0;
        String[] lastNodePaths = null;
        this.tableRows.append("<tbody>");
        for (ColumnTreeNode rowNode : rowNodes) {
            this.tableRows.append("<tr").append(rowIndex % 2 == 0 ? " class=\"hreport-row\"" : "").append(">");
            lastNodePaths = this.drawLeftFixedColumn(treeNodePathMap, lastNodePaths, rowNode, this.reportParameter.isRowSpan());
            for (ColumnTreeNode columnNode : columnNodes) {
                String rowKey = this.reportDataSet.getRowKey(rowNode, columnNode);
                ReportDataRow statRow = statRowMap.get(rowKey);
                if (statRow == null) {
                    statRow = new ReportDataRow();
                }
                for (ReportDataColumn statColumn : statColumns) {
                    ReportDataCell cell = statRow.getCell(statColumn.getName());
                    String value = (cell == null) ? "" : cell.toString();
                    this.tableRows.append("<td>").append(value).append("</td>");
                }
            }
            this.tableRows.append("</tr>");
            rowIndex++;
        }
        this.tableRows.append("</tbody>");
    }

    @Override
    public void drawTableFooterRows() {
    }
}
