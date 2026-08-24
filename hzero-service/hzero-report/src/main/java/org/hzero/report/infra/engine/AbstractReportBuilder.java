package org.hzero.report.infra.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.infra.engine.data.*;

/**
 * 报表构建抽象类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:11:56
 */
public abstract class AbstractReportBuilder implements ReportBuilder {
    protected final AbstractReportDataSet reportDataSet;
    protected final ReportParameter reportParameter;
    protected final StringBuilder tableRows = new StringBuilder();
    int totalWidth;

    AbstractReportBuilder(AbstractReportDataSet reportDataSet, ReportParameter reportParameter) {
        this.reportDataSet = reportDataSet;
        this.reportParameter = reportParameter;
        this.totalWidth = 0;
    }


    @Override
    public ReportTable getTable() {
        String width = totalWidth == 0 ? "100%" : totalWidth + "px";
        String table = String.format("<table id=\"hreport\" class=\"hreport\" style=\"overflow: auto;word-break: break-all;word-wrap: break-word;table-layout: fixed;\" width=\"%s\">%s</table>", width, this.tableRows.toString());
        return new ReportTable(table, this.reportParameter.getSqlText(),
                this.reportDataSet.getMetaData().getRows().size(),
                this.reportDataSet.getMetaData().getColumns().size(),
                this.reportParameter.getSqlPageInfo().getSize(),
                this.reportDataSet.getMetaData().getMetaDataRowTotal());
    }

    @Override
    public void drawTableHeaderRows() {
        List<ReportDataColumn> leftFixedColumns = this.reportDataSet.getHeaderLeftFixedColumns();
        ColumnTree rightColumnTree = this.reportDataSet.getHeaderRightColumnTree();
        int rowCount = rightColumnTree.getDepth();
        String rowSpan = rowCount > 1 ? String.format(" rowspan=\"%s\"", rowCount) : "";

        this.tableRows.append("<thead>");
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            this.tableRows.append("<tr class=\"hreport-header\">");
            if (rowIndex == 0) {
                for (ReportDataColumn leftColumn : leftFixedColumns) {
                    if (leftColumn.getMetaData().getHidden() == BaseConstants.Flag.NO) {
                        this.tableRows.append(String.format("<th width=\"%spx\" title=\"%s\"%s>%s</th>", leftColumn.getMetaData().getWidth(),
                                leftColumn.getMetaData().getComment(), rowSpan, leftColumn.getText()));
                        totalWidth += leftColumn.getMetaData().getWidth();
                    }
                }
            }
            for (ColumnTreeNode rightColumn : rightColumnTree.getNodesByLevel(rowIndex)) {
                if (rightColumn.getColumn().getMetaData().getHidden() == BaseConstants.Flag.NO) {
                    String colSpan = rightColumn.getSpans() > 1 ? String.format(" colspan=\"%s\"", rightColumn.getSpans()) : "";
                    this.tableRows.append(String.format("<th width=\"%spx\" title=\"%s\"%s>%s</th>", rightColumn.getColumn().getMetaData().getWidth(),
                            rightColumn.getColumn().getMetaData().getComment(), colSpan, rightColumn.getValue()));
                }
            }
            this.tableRows.append("</tr>");
        }
        this.tableRows.append("</thead>");
    }

    /**
     * 生成表体左边每一行的单元格
     *
     * @param treeNodePathMap 树中每个节点的path属性为key,treeNode属性为value的map对象
     * @param lastNodePaths   上一个跨行结点的树路径
     * @param rowNode         当前行结点
     * @param isRowSpan       是否跨行(rowspan)
     * @return 列
     */
    String[] drawLeftFixedColumn(Map<String, ColumnTreeNode> treeNodePathMap, String[] lastNodePaths,
                                 ColumnTreeNode rowNode, boolean isRowSpan) {
        if (isRowSpan) {
            return this.drawLeftRowSpanColumn(treeNodePathMap, lastNodePaths, rowNode);
        }
        String[] paths = StringUtils.splitPreserveAllTokens(rowNode.getPath(), this.reportDataSet.getPathSeparator());
        if (paths == null || paths.length == 0) {
            return new String[0];
        }

        int level = paths.length > 1 ? paths.length - 1 : 1;
        for (int i = 0; i < level; i++) {
            this.tableRows.append(String.format("<td class=\"hreport-fixed-column\">%s</td>", paths[i]));
        }
        return new String[0];
    }

    /**
     * 按层次遍历报表列树中每个结点，然后以结点path为key,treeNode属性为value，生成一个Map对象
     *
     * @param columnTree 报表列树对象
     * @return 树中每个节点的path属性为key, treeNode属性为value的map对象
     */
    Map<String, ColumnTreeNode> getTreeNodePathMap(ColumnTree columnTree) {
        Map<String, ColumnTreeNode> treeNodePathMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        for (int level = 0, length = columnTree.getDepth(); level < length; level++) {
            for (ColumnTreeNode treeNode : columnTree.getNodesByLevel(level)) {
                treeNodePathMap.put(treeNode.getPath(), treeNode);
            }
        }
        return treeNodePathMap;
    }

    /**
     * 生成表体左边每一行的跨行(rowspan)单元格
     *
     * @param treeNodePathMap 树中每个节点的path属性为key,treeNode属性为value的map对象
     * @param lastNodePaths   上一个跨行结点的树路径
     * @param rowNode         当前行结点
     * @return 当前跨行结点的树路径
     */
    private String[] drawLeftRowSpanColumn(Map<String, ColumnTreeNode> treeNodePathMap, String[] lastNodePaths, ColumnTreeNode rowNode) {
        String[] paths = StringUtils.splitPreserveAllTokens(rowNode.getPath(), this.reportDataSet.getPathSeparator());
        if (paths == null || paths.length == 0) {
            return new String[0];
        }

        int level = paths.length > 1 ? paths.length - 1 : 1;
        String[] currNodePaths = new String[level];
        for (int i = 0; i < level; i++) {
            String currPath = paths[i] + this.reportDataSet.getPathSeparator();
            currNodePaths[i] = (i > 0 ? currNodePaths[i - 1] + currPath : currPath);
            if (lastNodePaths != null && lastNodePaths[i].equals(currNodePaths[i])) {
                continue;
            }
            ColumnTreeNode treeNode = treeNodePathMap.get(currNodePaths[i]);
            if (treeNode == null) {
                this.tableRows.append("<td class=\"hreport-fixed-column\"></td>");
            } else {
                String rowspan = treeNode.getSpans() > 1 ? String.format(" rowspan=\"%s\"", treeNode.getSpans()) : "";
                this.tableRows.append(String.format("<td class=\"hreport-fixed-column\"%s>%s</td>", rowspan,
                        treeNode.getValue()));
            }
        }
        lastNodePaths = currNodePaths;
        return lastNodePaths;
    }
}
