package org.hzero.report.infra.engine.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hzero.report.infra.constant.HrptConstants;

/**
 * 报表元数据集类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:15:13
 */
public class MetaDataSet {
    private List<MetaDataRow> rows;
    private List<MetaDataColumn> columns;
    private List<MetaDataColumn> nonComputeColumns;
    private List<MetaDataColumn> layoutColumns;
    private List<MetaDataColumn> dimColumns;
    private List<MetaDataColumn> statColumns;
    private long metaDataRowTotal;

    /**
     * 构造函数
     *
     * @param rows               报表元数据行集合
     * @param columns            报表元数据列集合
     * @param enabledStatColumns 报表中启用的统计列
     * @param metaDataRowTotal   元数据行总条数
     */
    public MetaDataSet(List<MetaDataRow> rows, List<MetaDataColumn> columns, Set<String> enabledStatColumns,
                       long metaDataRowTotal) {
        this.rows = rows;
        this.columns = columns;
        this.initilizeColumn(enabledStatColumns);
        this.metaDataRowTotal = metaDataRowTotal;
    }

    /**
     * 获取报表的元数据行集合
     *
     * @return List<MetaDataRow>
     */
    public List<MetaDataRow> getRows() {
        return this.rows;
    }

    /**
     * 获取报表的所有元数据列
     *
     * @return List<MetaDataColumn>
     */
    public List<MetaDataColumn> getColumns() {
        return this.columns;
    }

    /**
     * 获取报表的所有非计算元数据列
     *
     * @return List<MetaDataColumn>
     */
    public List<MetaDataColumn> getNonComputeColumns() {
        return this.nonComputeColumns;
    }

    /**
     * 获取报表的布局元数据列
     *
     * @return List<MetaDataColumn>
     */
    public List<MetaDataColumn> getLayoutColumns() {
        return this.layoutColumns;
    }

    /**
     * 获取报表的维度元数据列
     *
     * @return List<MetaDataColumn>
     */
    public List<MetaDataColumn> getDimColumns() {
        return this.dimColumns;
    }

    /**
     * 获取报表的统计(含计算)元数据列
     *
     * @return List<MetaDataColumn>
     */
    public List<MetaDataColumn> getStatColumns() {
        return this.statColumns;
    }

    /**
     * 获取元数据行总条数
     *
     * @return 元数据行总条数
     */
    public long getMetaDataRowTotal() {
        return this.metaDataRowTotal;
    }

    private void initilizeColumn(Set<String> enabledStatColumns) {
        this.nonComputeColumns = new ArrayList<>();
        this.layoutColumns = new ArrayList<>();
        this.dimColumns = new ArrayList<>();
        this.statColumns = new ArrayList<>();

        for (int i = 0; i < this.columns.size(); i++) {
            MetaDataColumn column = this.columns.get(i);
            column.setOrdinal(i + 1);
            if (!HrptConstants.ColumnType.COMPUTED.equals(column.getType())) {
                this.nonComputeColumns.add(column);
            }
            if (HrptConstants.ColumnType.LAYOUT.equals(column.getType())) {
                this.layoutColumns.add(column);
            } else if (HrptConstants.ColumnType.DIMENSION.equals(column.getType())) {
                this.dimColumns.add(column);
            } else if (HrptConstants.ColumnType.STATISTICAL.equals(column.getType())
                    || HrptConstants.ColumnType.COMPUTED.equals(column.getType())) {
                if (enabledStatColumns.size() > 0 && !enabledStatColumns.contains(column.getName())) {
                    column.setHidden(1);
                }
                this.statColumns.add(column);
            }
        }
    }
}
