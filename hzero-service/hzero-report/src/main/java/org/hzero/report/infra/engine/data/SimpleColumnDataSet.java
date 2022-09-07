package org.hzero.report.infra.engine.data;

import java.util.List;

/**
 * 简单报表报表统计列的报表数据集类
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/02 15:40
 */
public class SimpleColumnDataSet extends AbstractReportDataSet implements ReportDataSet {

    public SimpleColumnDataSet(MetaDataSet metaDataSet) {
        super(metaDataSet, null, null);
    }

    @Override
    public String getRowKey(ColumnTreeNode rowNode, ColumnTreeNode columnNode) {
        return null;
    }

    @Override
    public List<ReportDataColumn> getHeaderLeftFixedColumns() {
        return null;
    }

    @Override
    public ColumnTree getHeaderRightColumnTree() {
        return null;
    }

    @Override
    public ColumnTree getBodyLeftFixedColumnTree() {
        return null;
    }

    @Override
    public List<ColumnTreeNode> getBodyRightColumnNodes() {
        return null;
    }

    @Override
    public boolean isHideStatColumn() {
        return false;
    }
}
