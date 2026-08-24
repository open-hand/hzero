package org.hzero.report.infra.engine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.engine.query.Query;
import org.hzero.report.infra.engine.query.QueryerFactory;
import org.springframework.util.Assert;

/**
 * 数据执行器类，负责选择正确的报表查询器并获取数据，最终转化为成报表的数据集
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:37:41
 */
public class DataExecutor {
    private ReportParameter parameter;
    private ReportDataSource dataSource;
    private Query queryer;

    /**
     * 数据执行器
     *
     * @param parameter 报表参数对象
     */
    DataExecutor(ReportParameter parameter) {
        this.parameter = parameter;
        this.dataSource = null;
        this.queryer = null;
    }

    /**
     * 数据执行器
     *
     * @param dataSource 报表数据源配置对象
     * @param parameter  报表参数对象
     */
    DataExecutor(ReportDataSource dataSource, ReportParameter parameter) {
        this.dataSource = dataSource;
        this.parameter = parameter;
        this.queryer = null;
    }

    /**
     * 数据执行器
     *
     * @param queryer   报表查询器对象
     * @param parameter 报表参数对象
     */
    DataExecutor(Query queryer, ReportParameter parameter) {
        this.dataSource = null;
        this.parameter = parameter;
        this.queryer = queryer;
    }

    /**
     * 选择正确的报表查询器并获取数据，最终转化为成报表的数据集
     *
     * @return ReportDataSet报表数据集对象
     */
    public AbstractReportDataSet execute() {
        Assert.notNull(this.getQueryer(), HrptMessageConstants.ERROR_NOFUND_QUERYER);
        // 元数据列集合
        List<MetaDataColumn> metaDataColumns = this.getQueryer().getMetaDataColumns();
        // 元数据行集合
        List<MetaDataRow> metaDataRows = this.getQueryer().getMetaDataRows();
        // 总条数
        long metaDataRowTotal = 0L;
        if (this.parameter.getSqlPageInfo().isCount()) {
            metaDataRowTotal = this.getQueryer().getMetaDataCount();
        }
        MetaDataSet metaDataSet = new MetaDataSet(metaDataRows, metaDataColumns, this.parameter.getEnabledStatColumns(), metaDataRowTotal);
        return StringUtils.equals(this.parameter.getStatColumnLayout(), HrptConstants.LayoutType.VERTICAL)
                ? new VerticalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout())
                : new HorizontalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout());
    }

    /**
     * 选择正确的报表查询器并获取数据，最终转化为成报表的数据集
     *
     * @return ReportDataSet报表数据集对象
     */
    AbstractReportDataSet executeSimple() {
        Assert.notNull(this.getQueryer(), HrptMessageConstants.ERROR_NOFUND_QUERYER);
        // 元数据列集合
        List<MetaDataColumn> metaDataColumns = this.getQueryer().getMetaDataColumns();
        // 元数据行集合
        List<MetaDataRow> metaDataRows = this.getQueryer().getMetaDataRows();
        // 总条数
        long metaDataRowTotal = 0L;
        if (this.parameter.getSqlPageInfo().isCount()) {
            metaDataRowTotal = this.getQueryer().getMetaDataCount();
        }
        MetaDataSet metaDataSet = new MetaDataSet(metaDataRows, metaDataColumns, this.parameter.getEnabledStatColumns(), metaDataRowTotal);
        return new SimpleColumnDataSet(metaDataSet);
    }

    /**
     * 选择正确的报表查询器并获取数据，最终转化为成报表的数据集
     *
     * @param metaDataSet 元数据集
     * @return ReportDataSet报表数据集对象
     */
    public AbstractReportDataSet execute(MetaDataSet metaDataSet) {
        Assert.notNull(metaDataSet, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        return StringUtils.equals(this.parameter.getStatColumnLayout(), HrptConstants.LayoutType.VERTICAL) ?
                new VerticalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout()) :
                new HorizontalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout());
    }

    /**
     * 获取报表元数据行列表
     *
     * @return List<MetaDataRow>
     */
    List<MetaDataRow> simpleExecute() {
        Assert.notNull(this.getQueryer(), HrptMessageConstants.ERROR_NOFUND_QUERYER);
        return this.getQueryer().getMetaDataRows();
    }

    /**
     * 获取查询器
     */
    private Query getQueryer() {
        if (this.queryer != null) {
            return this.queryer;
        }
        return QueryerFactory.create(this.dataSource, this.parameter);
    }
}
