package org.hzero.report.app.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.report.infra.engine.data.ReportDataSet;

/**
 * 报表信息应用服务接口
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
public interface ReportService {

    /**
     * 查询报表明细
     *
     * @param reportUuid    报表uuid
     * @param buildInParams 参数
     * @return 明细信息
     */
    Report selectReport(String reportUuid, Map<String, Object> buildInParams);

    /**
     * 获取报表基本信息
     *
     * @param reportUuid 报表uuid
     * @return 基本信息
     */
    Report getReportBaseInfo(String reportUuid);

    /**
     * 复制报表
     *
     * @param reportId 报表ID
     * @param tenantId 租户ID
     */
    void copyReport(Long reportId, Long tenantId);

    /**
     * 插入报表信息
     *
     * @param report 报表
     */
    void insertReport(Report report);

    /**
     * 更新报表信息
     *
     * @param report 报表
     */
    void updateReport(Report report);

    /**
     * 删除
     *
     * @param reportId 报表Id
     */
    void deleteReport(Long reportId);

    /**
     * 初始化报表列
     *
     * @param reportId  报表Id
     * @param datasetId 数据集Id
     * @return 报表列集合
     */
    List<MetaDataColumn> initMetaDataColumns(Long reportId, Long datasetId);

    /**
     * 构建报表参数
     *
     * @param parameterMap 参数
     * @return 报表参数
     */
    Map<String, Object> getBuildInParameters(Map<String, String[]> parameterMap);

    /**
     * 获取报表数据详细集
     *
     * @param reportUuid 报表UUID
     * @param request    HttpServletRequest
     * @return 报表数据详细集
     */
    ReportDataSet getReportDataSet(String reportUuid, HttpServletRequest request);

    /**
     * 获取报表数据详细集
     *
     * @param report         报表
     * @param formParameters 参数
     * @return 报表数据详细集
     */
    ReportDataSet getReportDataSet(Report report, Map<String, Object> formParameters);

    /**
     * 获取报表数据
     *
     * @param reportUuid 报表uuid
     * @param data       data
     * @param request    request
     * @return 报表数据
     */
    JSONObject getReportData(String reportUuid, JSONObject data, HttpServletRequest request);

    /**
     * 导出报表文件
     *
     * @param reportUuid 报表UUID
     * @param outputType 导出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportReportFile(String reportUuid, String outputType, HttpServletRequest request,
                          HttpServletResponse response) throws IOException;

    /**
     * 导出报表文件
     *
     * @param tenantId   租户ID
     * @param reportUuid 报表UUID
     * @param outputType 导出类型
     * @param request    request
     * @param response   response
     * @throws IOException IO异常
     */
    void exportReportFileInside(Long tenantId, String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 获取报表文件
     *
     * @param reportUuid 报表UUID
     * @param outputType 导出类型
     * @param request    request
     * @param response   response
     * @return 文件数据
     */
    byte[] getReportFile(String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response);

    /**
     * 创建并发请求
     *
     * @param tenantId   租户Id
     * @param reportUuid 报表uuid
     * @param request    请求
     */
    void createConcRequest(Long tenantId, String reportUuid, HttpServletRequest request);

    /**
     * 获取报表支持的导出类型
     *
     * @param reportType   报表类型
     * @param templateType 模板类型
     * @return 导出类型
     */
    List<String> getDefaultType(String reportType, String templateType);

    /**
     * 获取导出类型
     *
     * @return 导出类型
     */
    Map<String, Object> getExportType();
}
