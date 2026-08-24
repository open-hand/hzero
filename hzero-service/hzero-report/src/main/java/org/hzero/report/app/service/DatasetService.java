package org.hzero.report.app.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.report.infra.meta.option.QueryParameterOptions;

/**
 * 报表数据集应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-10-31 15:36:59
 */
public interface DatasetService {

    /**
     * 插入数据集
     *
     * @param dataset 数据集
     */
    void insertDataset(Dataset dataset);

    /**
     * 更新数据集
     *
     * @param dataset 数据集
     */
    void updateDataset(Dataset dataset);

    /**
     * 获取列元数据
     *
     * @param datasetId      数据集Id
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @param sqlText        SQL语句字符串
     * @return 元数据列集合
     */
    List<MetaDataColumn> initMetaDataColumns(Long datasetId, Long tenantId, String datasourceCode, String sqlText);

    /**
     * 获取参数元数据
     *
     * @param datasetId 数据集Id
     * @param sqlText   SQL语句字符串
     * @return 元数据参数集合
     */
    List<QueryParameterOptions> initQueryParameters(Long datasetId, String sqlText);

    /**
     * 获取Xml示例数据
     *
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @param sqlType        SQL类型
     * @param sqlText        SQL语句字符串
     * @return
     */
    Document parseXmlDataSample(Long tenantId, String datasourceCode, String sqlType, String sqlText);

    /**
     * 获取map示例数据
     *
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @param sqlType        SQL类型
     * @param sqlText        SQL语句字符串
     * @return
     */
    Map<String, Object> parseMapDataSample(Long tenantId, String datasourceCode, String sqlType, String sqlText);

    /**
     * SQL预览
     *
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @param sqlText        SQL语句字符串
     * @param queryParams    查询参数
     * @param request        request
     * @return SQL
     */
    String previewSql(Long tenantId, String datasourceCode, String sqlText, String queryParams, HttpServletRequest request);

    /**
     * 解析XML示例数据
     *
     * @param tenantId 租户Id
     * @param dataset  数据集
     * @param request  request
     * @return Document文档
     */
    Document parseXmlData(Long tenantId, Dataset dataset, HttpServletRequest request);

    /**
     * 解析Map示例数据
     *
     * @param tenantId 租户Id
     * @param dataset  数据集
     * @param request  request
     * @return Document文档
     */
    Map<String, Object> parseMapData(Long tenantId, Dataset dataset, HttpServletRequest request);

    /**
     * 解析Map示例数据
     *
     * @param tenantId 租户Id
     * @param dataset  数据集
     * @param request  request
     * @return Document文档
     */
    Map<String, String> parseMapDataWithType(Long tenantId, Dataset dataset, HttpServletRequest request);

    /**
     * 构建参数
     *
     * @param parameterMap 参数值Map
     * @return 构建结果
     */
    Map<String, Object> getBuildInParameters(Map<String, String[]> parameterMap);

    /**
     * 获取查询SQL
     *
     * @param sqlText     SQL语句字符串
     * @param queryParams 查询参数
     * @param request     request
     * @return sql
     */
    String getSqlText(String sqlText, String queryParams, HttpServletRequest request);

}
