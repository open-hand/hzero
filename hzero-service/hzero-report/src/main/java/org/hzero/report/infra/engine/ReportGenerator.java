package org.hzero.report.infra.engine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.engine.query.Query;

/**
 * 报表产生器类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:36:03
 */
public class ReportGenerator {

    private ReportGenerator() {
    }

    /**
     * @param ds        数据源
     * @param parameter 参数
     * @return ReportTable
     */
    public static ReportTable generateSimple(ReportDataSource ds, ReportParameter parameter) {
        return generateSimple(getSimpleDataSet(ds, parameter), parameter);
    }

    /**
     * @param ds        数据源
     * @param parameter 参数
     * @return ReportTable
     */
    public static ReportTable generate(ReportDataSource ds, ReportParameter parameter) {
        return generate(getDataSet(ds, parameter), parameter);
    }

    /**
     * @param queryer   查询
     * @param parameter 参数
     * @return ReportTable
     */
    public static ReportTable generate(Query queryer, ReportParameter parameter) {
        return generate(getDataSet(queryer, parameter), parameter);
    }

    /**
     * @param metaDataSet 元数据集
     * @param parameter   参数
     * @return ReportTable
     */
    public static ReportTable generate(MetaDataSet metaDataSet, ReportParameter parameter) {
        return generate(getDataSet(metaDataSet, parameter), parameter);
    }

    /**
     * @param ds        数据源
     * @param parameter 参数
     * @return AbstractReportDataSet
     */
    private static AbstractReportDataSet getSimpleDataSet(ReportDataSource ds, ReportParameter parameter) {
        return new DataExecutor(ds, parameter).executeSimple();
    }

    /**
     * @param ds        数据源
     * @param parameter 参数
     * @return AbstractReportDataSet
     */
    public static AbstractReportDataSet getDataSet(ReportDataSource ds, ReportParameter parameter) {
        return new DataExecutor(ds, parameter).execute();
    }

    /**
     * 生成报表元数据行
     *
     * @param ds        数据源
     * @param parameter 参数
     * @return List<MetaDataRow>
     */
    public static List<MetaDataRow> getMetaDataRows(ReportDataSource ds, ReportParameter parameter) {
        return new DataExecutor(ds, parameter).simpleExecute();
    }

    /**
     * @param queryer   查询
     * @param parameter 参数
     * @return AbstractReportDataSet
     */
    private static AbstractReportDataSet getDataSet(Query queryer, ReportParameter parameter) {
        return new DataExecutor(queryer, parameter).execute();
    }

    /**
     * @param metaDataSet 数据集
     * @param parameter   参数
     * @return AbstractReportDataSet
     */
    private static AbstractReportDataSet getDataSet(MetaDataSet metaDataSet, ReportParameter parameter) {
        return new DataExecutor(parameter).execute(metaDataSet);
    }

    /**
     * 构建简单报表
     *
     * @param dataSet   数据集
     * @param parameter 参数
     * @return ReportTable
     */
    private static ReportTable generateSimple(AbstractReportDataSet dataSet, ReportParameter parameter) {
        ReportBuilder builder = new SimpleColumnReportBuilder(dataSet, parameter);
        builder.drawTableHeaderRows();
        builder.drawTableBodyRows();
        builder.drawTableFooterRows();
        return builder.getTable();
    }

    /**
     * 构建复杂报表
     *
     * @param dataSet   数据集
     * @param parameter 参数
     * @return ReportTable
     */
    public static ReportTable generate(AbstractReportDataSet dataSet, ReportParameter parameter) {
        ReportBuilder builder = createBuilder(dataSet, parameter);
        ReportDirector director = new ReportDirector(builder);
        director.build();
        return builder.getTable();
    }

    /**
     * 加载不同的reportBuilder
     */
    private static ReportBuilder createBuilder(AbstractReportDataSet reportDataSet, ReportParameter parameter) {
        if (StringUtils.equals(parameter.getStatColumnLayout(), HrptConstants.LayoutType.HORIZONTAL)) {
            return new HorizontalStatColumnReportBuilder(reportDataSet, parameter);
        }
        return new VerticalStatColumnReportBuilder(reportDataSet, parameter);
    }
}
