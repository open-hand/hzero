package org.hzero.report.infra.engine;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.report.infra.engine.data.*;

/**
 * 纵向展示统计列的报表生成类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:47:38
 */
public class VerticalStatColumnReportBuilder extends AbstractReportBuilder implements ReportBuilder {

    /**
     * 纵向展示统计列的报表生成类
     *
     * @param reportDataSet   报表数据集
     * @param reportParameter 报表参数
     */
    VerticalStatColumnReportBuilder(AbstractReportDataSet reportDataSet, ReportParameter reportParameter) {
        super(reportDataSet, reportParameter);
    }

    @Override
    public void drawTableBodyRows() {
        ColumnTree leftFixedColumnTree = this.reportDataSet.getBodyLeftFixedColumnTree();
        List<ColumnTreeNode> rowNodes = leftFixedColumnTree.getLastLevelNodes();
        List<ColumnTreeNode> columnNodes = this.reportDataSet.getBodyRightColumnNodes();
        Map<String, ReportDataRow> statRowMap = this.reportDataSet.getRowMap();
        Map<String, ColumnTreeNode> treeNodePathMap = this.getTreeNodePathMap(leftFixedColumnTree);
        List<ReportDataColumn> enabledStatColumns = this.reportDataSet.getEnabledStatColumns();
        String defaultColumName = "";
        if (CollectionUtils.isNotEmpty(enabledStatColumns)) {
            defaultColumName = enabledStatColumns.get(0).getName();
        }
        boolean isHideStatColumn = this.reportDataSet.isHideStatColumn();
        int rowIndex = 0;
        String[] lastNodePaths = null;
        this.tableRows.append("<tbody>");
        for (ColumnTreeNode rowNode : rowNodes) {
            String columnName = isHideStatColumn ? defaultColumName : rowNode.getName();
            this.tableRows.append("<tr").append(rowIndex % 2 == 0 ? " class=\"hreport-row\"" : "").append(">");
            lastNodePaths = this.drawLeftFixedColumn(treeNodePathMap, lastNodePaths, rowNode, this.reportParameter.isRowSpan());
            // 不存在统计列，不显示默认统计列
            if (StringUtils.isNotBlank(defaultColumName)) {
                for (ColumnTreeNode columnNode : columnNodes) {
                    String rowKey = this.reportDataSet.getRowKey(rowNode, columnNode);
                    ReportDataRow statRow = statRowMap.get(rowKey);
                    if (statRow == null) {
                        statRow = new ReportDataRow();
                    }
                    Object cell = statRow.getCell(columnName);
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
