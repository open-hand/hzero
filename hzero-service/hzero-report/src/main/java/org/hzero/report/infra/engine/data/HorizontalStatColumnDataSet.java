package org.hzero.report.infra.engine.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.report.infra.constant.HrptConstants;

/**
 * 横向展示报表统计列的报表数据集类。
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:42:07
 */
public class HorizontalStatColumnDataSet extends AbstractReportDataSet implements ReportDataSet {

    public HorizontalStatColumnDataSet(final MetaDataSet metaDataSet, final String layout,
                                       final String statColumnLayout) {
        super(metaDataSet, layout, statColumnLayout);
    }

    @Override
    public String getRowKey(final ColumnTreeNode rowNode, final ColumnTreeNode columnNode) {
        return StringUtils.equals(this.layout, HrptConstants.LayoutType.HORIZONTAL) ? (columnNode.getPath() + rowNode.getPath())
                : (rowNode.getPath() + columnNode.getPath());
    }

    @Override
    public List<ReportDataColumn> getHeaderLeftFixedColumns() {
        return StringUtils.equals(this.layout, HrptConstants.LayoutType.HORIZONTAL) ? this.getDimColumns() : this.getLayoutColumns();
    }

    @Override
    public ColumnTree getHeaderRightColumnTree() {
        if (this.headerColumnTree != null) {
            return this.headerColumnTree;
        }
        // 如果布局列纵向展示
        if (StringUtils.equals(this.layout, HrptConstants.LayoutType.HORIZONTAL)) {
            return this.getHorizontalLayoutHeaderColumnTree();
        }
        return this.getVerticalLayoutHeaderColumnTree();
    }

    @Override
    public ColumnTree getBodyLeftFixedColumnTree() {
        return StringUtils.equals(this.layout, HrptConstants.LayoutType.HORIZONTAL) ? this.getDimColumnTree() : this.getLayoutColumnTree();
    }

    @Override
    public List<ColumnTreeNode> getBodyRightColumnNodes() {
        return StringUtils.equals(this.layout, HrptConstants.LayoutType.HORIZONTAL) ? this.getLayoutColumnTree().getLastLevelNodes()
                : this.getDimColumnTree().getLastLevelNodes();
    }

    @Override
    public boolean isHideStatColumn() {
        // 如果布局列横向显示
        // 或者布局列纵向显示（即显示在表体左边)且表头有维度列
        return this.getEnabledStatColumns().size() == 1
                && (StringUtils.equals(this.layout, HrptConstants.LayoutType.HORIZONTAL) || this.getDimColumnCount() > 0);
    }

    private ColumnTree getHorizontalLayoutHeaderColumnTree() {
        final ColumnTree statColumnTree = this.getStatColumnTree();

        final ColumnTree layoutColumnTree = this.getLayoutColumnTree();
        if (this.isHideStatColumn()) {
            return layoutColumnTree;
        }

        for (final ColumnTreeNode leafNode : layoutColumnTree.getLeafNodes()) {
            leafNode.getChildren().addAll(statColumnTree.getRoots());
        }
        this.setTreeNodeSpansAndDepth(layoutColumnTree.getRoots(), this.getLayoutColumns());
        final int depth = layoutColumnTree.getDepth() + statColumnTree.getDepth();
        this.headerColumnTree = new ColumnTree(layoutColumnTree.getRoots(), depth);
        return this.headerColumnTree;
    }

    private ColumnTree getVerticalLayoutHeaderColumnTree() {
        final ColumnTree statColumnTree = this.getStatColumnTree();

        // 如果右边没有维度列则表头列直接设置为统计列
        if (this.getDimColumnCount() == 0) {
            this.setTreeNodeSpansAndDepth(statColumnTree.getRoots(), false, this.getEnabledStatColumns());
            this.headerColumnTree = new ColumnTree(statColumnTree.getRoots(), statColumnTree.getDepth());
            this.headerColumnTree.setLeafNodes(statColumnTree.getRoots());
            return this.headerColumnTree;
        }

        final ColumnTree dimColumnTree = this.getDimColumnTree();
        if (this.isHideStatColumn()) {
            return dimColumnTree;
        }

        for (final ColumnTreeNode leafNode : dimColumnTree.getLeafNodes()) {
            leafNode.getChildren().addAll(statColumnTree.getRoots());
        }
        this.setTreeNodeSpansAndDepth(dimColumnTree.getRoots(), this.getDimColumns());
        final int depth = dimColumnTree.getDepth() + statColumnTree.getDepth();
        this.headerColumnTree = new ColumnTree(dimColumnTree.getRoots(), depth);
        return this.headerColumnTree;
    }
}
