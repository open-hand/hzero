package org.hzero.report.domain.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.engine.query.Query;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * 报表处理领域服务接口
 *
 * @author xianzhi.chen@hand-china.com 2018年12月13日下午7:12:55
 */
public interface IReportGenerateService {

    /**
     * 获取报表数据集信息
     *
     * @param report         报表
     * @param formParameters 参数
     * @return ReportDataSet
     */
    ReportDataSet getReportDataSet(Report report, Map<String, Object> formParameters);

    /**
     * 生成平面报表行数据
     *
     * @param report     报表
     * @param formParams 参数
     * @return List<MetaDataRow>
     */
    List<MetaDataRow> generateTableRows(Report report, Map<String, Object> formParams);

    /**
     * 生成报表
     *
     * @param reportUuid 报表uuid
     * @param data       data
     * @param request    request
     */
    void generate(String reportUuid, JSONObject data, HttpServletRequest request);

    /**
     * 生成报表
     *
     * @param report     报表
     * @param data       data
     * @param request 参数
     */
    void generate(Report report, JSONObject data, HttpServletRequest request);

    /**
     * 生成简单表格报表
     *
     * @param report     报表
     * @param data       data
     * @param parameters 参数
     */
    void generateSimpleTable(Report report, JSONObject data, Map<?, ?> parameters);

    /**
     * 生成复杂平面报表
     *
     * @param report     报表
     * @param data       data
     * @param parameters 参数
     */
    void generateTable(Report report, JSONObject data, Map<?, ?> parameters);

    /**
     * 生成复杂平面报表
     *
     * @param queryer         报表查询接口
     * @param reportParameter 报表参数
     * @param data            data
     */
    void generateTable(Query queryer, ReportParameter reportParameter, JSONObject data);

    /**
     * 生成复杂平面报表
     *
     * @param metaDataSet     元数据集
     * @param reportParameter 报表参数
     * @param data            data
     */
    void generateTable(MetaDataSet metaDataSet, ReportParameter reportParameter, JSONObject data);

    /**
     * 生成复杂平面报表
     *
     * @param report     报表
     * @param formParams 表单参数
     * @return ReportTable
     */
    ReportTable generateTable(Report report, Map<String, Object> formParams);

    /**
     * 生成图形报表
     *
     * @param report     报表
     * @param data       data
     * @param parameters 参数
     */
    void generateChart(Report report, JSONObject data, Map<?, ?> parameters);

    /**
     * 获取默认图表数据
     *
     * @param data data
     */
    void getDefaultChartData(JSONObject data);

    /**
     * 生成模板报表
     *
     * @param report     报表
     * @param data       data
     * @param parameters 参数
     */
    void generateDocument(Report report, JSONObject data, Map<?, ?> parameters);

    /**
     * 导出报表附件
     *
     * @param reportUuid 报表uuid
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportReportFile(String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出报表附件
     *
     * @param tenantId   租户ID
     * @param reportUuid 报表uuid
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportReportFileInside(Long tenantId, String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获取报表附件
     *
     * @param reportUuid 报表uuid
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @return 文件
     */
    byte[] getReportFile(String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出平面报表文件
     *
     * @param report     报表
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportSimpleTableReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出平面报表文件
     *
     * @param report     报表
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportTableReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获取平面报表文件
     *
     * @param report     报表
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @return 文件
     */
    byte[] getTableReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出图表报表文件
     *
     * @param report     报表
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportChartReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获取图表报表文件
     *
     * @param report     报表
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @return 文件
     */
    byte[] getChartReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出模板报表文件
     *
     * @param report     报表
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportDocumentReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获取模板报表文件
     *
     * @param report     报表
     * @param outputType 输出类型
     * @param request    request
     * @param response   response
     * @return 文件
     */
    byte[] getDocumentReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response);

    /**
     * 通过解析参数获取模板对象
     *
     * @param report          报表
     * @param httpReqParamMap 参数
     * @return 模板
     */
    TemplateDtl getTemplateByParams(Report report, Map<?, ?> httpReqParamMap);

    /**
     * 异步执行导出报表文件
     *
     * @param report     报表信息
     * @param formParams 表单信息
     */
    void asyncExportReportFile(Report report, Map<String, Object> formParams);

    /**
     * 异步处理导出文件过程方法
     *
     * @param tenantId   租户Id
     * @param requestId  请求ID
     * @param report     报表信息
     * @param formParams 表单参数
     * @param token      token
     * @param userDetail 用户信息
     */
    void asyncGenerateTable(Long tenantId, Long requestId, Report report, Map<String, Object> formParams, String token, CustomUserDetails userDetail);

    /**
     * 处理导出文件过程方法
     *
     * @param tenantId   租户Id
     * @param requestId  请求ID
     * @param report     报表信息
     * @param formParams 表单参数
     * @return 文件url
     */
    String generateTable(Long tenantId, Long requestId, Report report, Map<String, Object> formParams);
}
