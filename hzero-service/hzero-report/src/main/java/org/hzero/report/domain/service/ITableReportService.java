package org.hzero.report.domain.service;

import java.util.List;
import java.util.Map;

import org.hzero.report.domain.entity.Report;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.engine.query.Query;

/**
 * 表格报表生成服务接口
 *
 * @author xianzhi.chen@hand-china.com 2018年10月22日下午5:15:38
 */
public interface ITableReportService {

    /**
     * 获取平面表格报表
     *
     * @param reportId   报表Id
     * @param formParams 参数
     * @return ReportTable
     */
    ReportTable getReportTable(long reportId, Map<String, Object> formParams);

    /**
     * 获取简单平面表格报表
     *
     * @param report     报表
     * @param formParams 参数
     * @return ReportTable
     */
    ReportTable getSimpleReportTable(Report report, Map<String, Object> formParams);

    /**
     * 获取平面表格报表
     *
     * @param report     报表
     * @param formParams 参数
     * @return ReportTable
     */
    ReportTable getReportTable(Report report, Map<String, Object> formParams);

    /**
     * 获取平面表格报表
     *
     * @param queryer         查询接口
     * @param reportParameter 报表参数
     * @return ReportTable
     */
    ReportTable getReportTable(Query queryer, ReportParameter reportParameter);

    /**
     * 获取平面表格报表
     *
     * @param metaDataSet     元数据集
     * @param reportParameter 参数
     * @return ReportTable
     */
    ReportTable getReportTable(MetaDataSet metaDataSet, ReportParameter reportParameter);

    /**
     * 获取表格报表行数据
     *
     * @param report     报表
     * @param formParams 参数
     * @return 行数据
     */
    List<MetaDataRow> getReportTableRows(Report report, Map<String, Object> formParams);

    /**
     * 获取报表数据集
     *
     * @param report     报表
     * @param parameters 参数
     * @return 报表数据集
     */
    ReportDataSet getReportDataSet(Report report, Map<String, Object> parameters);
}
