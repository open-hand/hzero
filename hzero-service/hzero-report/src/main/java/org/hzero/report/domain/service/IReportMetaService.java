package org.hzero.report.domain.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.hzero.report.domain.entity.Report;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.report.infra.engine.data.ReportDataSource;
import org.hzero.report.infra.engine.data.ReportParameter;
import org.hzero.report.infra.meta.form.FormElement;
import org.hzero.report.infra.meta.option.QueryParameterOptions;
import org.hzero.report.infra.meta.option.ReportOptions;

/**
 * 报表元数据服务接口
 *
 * @author xianzhi.chen@hand-china.com 2018年12月5日上午10:08:13
 */
public interface IReportMetaService {

    /**
     * 获取数据源信息
     *
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @return ReportDataSource 报表数据源
     */
    ReportDataSource getReportDataSource(Long tenantId, String datasourceCode);

    /**
     * 获取报表信息
     *
     * @param reportId 报表Id
     * @return Report 报表信息
     */
    Report getReportById(long reportId);

    /**
     * 获取报表信息(而且鉴权)
     *
     * @param reportKey 报表Key(reportUuid或reportCode)
     * @return Report 报表信息
     */
    Report getReportByKey(String reportKey);

    /**
     * 获取报表信息(不鉴权)
     *
     * @param tenantId  租户ID
     * @param reportKey 报表Key(reportUuid或reportCode)
     * @return Report 报表信息
     */
    Report getReportIgnorePermission(Long tenantId, String reportKey);


    /**
     * 解析json格式的报表选项ReportOptions
     *
     * @param json json
     * @return ReportOptions
     */
    ReportOptions parseReportOptions(String json);

    /**
     * 创建渲染简单报表的参数对象
     *
     * @param report     报表
     * @param formParams 参数
     * @return ReportParameter
     */
    ReportParameter createSimpleReportParameter(Report report, Map<String, Object> formParams);

    /**
     * 创建渲染报表的参数对象
     *
     * @param report     报表
     * @param formParams 参数
     * @return ReportParameter
     */
    ReportParameter createReportParameter(Report report, Map<String, Object> formParams);

    /**
     * 解析json格式的报表元数据列为MetaDataColumn对象集合
     *
     * @param json json
     * @return List<MetaDataColumn>
     */
    List<MetaDataColumn> parseMetaColumns(String json);

    /**
     * 获取报表元数据列
     *
     * @param tenantId       租户ID
     * @param datasourceCode 数据源编码
     * @param sqlText        sql
     * @return List<MetaDataColumn>
     */
    List<MetaDataColumn> getMetaDataColumns(Long tenantId, String datasourceCode, String sqlText);

    /**
     * 获取元数据总条数
     *
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @param sqlText        sql脚本
     * @return 总条数
     */
    Long getMetaDataCount(Long tenantId, String datasourceCode, String sqlText);

    /**
     * 解析json格式的报表查询参数为QueryParameter对象集合
     *
     * @param json json
     * @return List<QueryParameterOptions>
     */
    List<QueryParameterOptions> parseQueryParams(String json);

    /**
     * 获取查询参数
     *
     * @param sqlText sql
     * @return List<QueryParameterOptions>
     */
    List<QueryParameterOptions> getQueryParameters(String sqlText);

    /**
     * 获取构建参数
     *
     * @param httpReqParamMap 参数
     * @return Map
     */
    Map<String, Object> getBuildInParameters(Map<?, ?> httpReqParamMap);

    /**
     * 获取表单参数
     *
     * @param datasetId       数据集Id
     * @param httpReqParamMap 参数
     * @return Map
     */
    Map<String, Object> getFormParameters(Long datasetId, Map<?, ?> httpReqParamMap);

    /**
     * 获取查询参数元素
     *
     * @param datasetId     数据集Id
     * @param buildInParams 参数
     * @return List<FormElement>
     */
    List<FormElement> getQueryParamFormElements(Long datasetId, Map<String, Object> buildInParams);

    /**
     * 获取SQL脚本（可处理参数）
     *
     * @param sqlText     sql
     * @param queryParams 查询参数
     * @param request     request
     * @return 预处理SQL脚本
     */
    String getSqlText(String sqlText, String queryParams, HttpServletRequest request);

    /**
     * 获取可执行SQL脚本
     *
     * @param sqlText    SQL语句
     * @param formParams 表单参数
     * @return 可执行SQL
     */
    String getExecuteSqlText(String sqlText, Map<String, Object> formParams);

    /**
     * 是否异步执行报表
     *
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @param sqlText        SQL脚本
     * @param formParams     表单参数
     * @param thresholdValue 异步阈值
     * @param asyncFlag      异步标识
     * @return 布尔值
     */
    boolean isAsyncExecute(Long tenantId, String datasourceCode, String sqlText, Map<String, Object> formParams, Long thresholdValue, Integer asyncFlag);
}
