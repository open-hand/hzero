package org.hzero.report.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.app.service.DatasetService;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.domain.repository.DatasetRepository;
import org.hzero.report.domain.service.IReportMetaService;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.SqlXmlParser;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.report.infra.engine.data.ReportDataSource;
import org.hzero.report.infra.engine.query.QueryerFactory;
import org.hzero.report.infra.meta.option.QueryParameterOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;

import io.choerodon.core.exception.CommonException;

/**
 * 报表数据集应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-10-31 15:36:59
 */
@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private IReportMetaService reportMetaService;
    @Autowired
    private DatasetRepository datasetRepository;

    @Override
    public void insertDataset(Dataset dataset) {
        // 参数合规性校验
        checkParameter(dataset.getQueryParams());
        datasetRepository.insertSelective(dataset);
    }

    @Override
    public void updateDataset(Dataset dataset) {
        // 参数合规性校验
        checkParameter(dataset.getQueryParams());
        datasetRepository.updateOptional(dataset,
                Dataset.FIELD_DATASET_NAME,
                Dataset.FIELD_ENABLED_FLAG,
                Dataset.FIELD_META_COLUMNS,
                Dataset.FIELD_QUERY_PARAMS,
                Dataset.FIELD_SQL_TEXT,
                Dataset.FIELD_SQL_TYPE,
                Dataset.FIELD_REMARK);
    }

    @Override
    public List<MetaDataColumn> initMetaDataColumns(Long datasetId, Long tenantId, String datasourceCode, String sqlText) {
        List<MetaDataColumn> result = new ArrayList<>();
        // sql解析结果
        List<MetaDataColumn> newMetaColumnList = reportMetaService.getMetaDataColumns(tenantId, datasourceCode, sqlText);
        if (CollectionUtils.isEmpty(newMetaColumnList)) {
            return result;
        }
        // 新增则直接返回
        if (datasetId == null) {
            return newMetaColumnList;
        }
        // 获取数据库数据
        List<MetaDataColumn> metaColumnList = this.getMetaDataColumnsByDatasetId(datasetId);
        Map<String, MetaDataColumn> columnMap = metaColumnList.stream().collect(Collectors.toMap(MetaDataColumn::getName, Function.identity(), (k1, k2) -> k1));
        int ordinal = 0;
        for (MetaDataColumn metaDataColumn : newMetaColumnList) {
            ordinal++;
            // 字段名存在使用原数据，不存在使用新数据，并重设排序号
            result.add(columnMap.getOrDefault(metaDataColumn.getName(), metaDataColumn).setOrdinal(ordinal));
        }
        return result;
    }

    @Override
    public List<QueryParameterOptions> initQueryParameters(Long datasetId, String sqlText) {
        List<QueryParameterOptions> result = new ArrayList<>();
        // sql解析结果
        List<QueryParameterOptions> newQueryParameterList = reportMetaService.getQueryParameters(sqlText);
        if (CollectionUtils.isEmpty(newQueryParameterList)) {
            return result;
        }
        // 合规性检查
        checkParameter(newQueryParameterList);
        // 新增则直接返回
        if (datasetId == null) {
            return newQueryParameterList;
        }
        // 获取数据集已维护信息
        List<QueryParameterOptions> queryParameterList = getQueryParamsByDatasetId(datasetId);
        Map<String, QueryParameterOptions> paramMap = queryParameterList.stream().collect(Collectors.toMap(QueryParameterOptions::getName, Function.identity(), (k1, k2) -> k1));
        int ordinal = 0;
        for (QueryParameterOptions queryParam : newQueryParameterList) {
            ordinal++;
            // 字段名存在使用原数据，不存在使用新数据，并重设排序号
            result.add(paramMap.getOrDefault(queryParam.getName(), queryParam).setOrdinal(ordinal));
        }
        return result;
    }


    @Override
    public Map<String, Object> getBuildInParameters(Map<String, String[]> parameterMap) {
        return reportMetaService.getBuildInParameters(parameterMap);
    }

    @Override
    public String getSqlText(String sqlText, String queryParams, HttpServletRequest request) {
        return reportMetaService.getSqlText(sqlText, queryParams, request);
    }

    @Override
    public Document parseXmlDataSample(Long tenantId, String datasourceCode, String sqlType, String sqlText) {
        final ReportDataSource reportDataSource = reportMetaService.getReportDataSource(tenantId, datasourceCode);
        return QueryerFactory.create(reportDataSource).getMetaDataXml(sqlType, sqlText);
    }

    @Override
    public Map<String, Object> parseMapDataSample(Long tenantId, String datasourceCode, String sqlType, String sqlText) {
        final ReportDataSource reportDataSource = reportMetaService.getReportDataSource(tenantId, datasourceCode);
        return QueryerFactory.create(reportDataSource).getMetaDataMap(sqlType, sqlText);
    }

    @Override
    public String previewSql(Long tenantId, String datasourceCode, String sqlText, String queryParams,
                             HttpServletRequest request) {
        Assert.notNull(datasourceCode, HrptMessageConstants.ERROR_NOSELECT_DATASOURCE);
        sqlText = reportMetaService.getSqlText(sqlText, queryParams, request);
        // 判断是否为XML类型SQL
        boolean isXmlData;
        try {
            isXmlData = SqlXmlParser.isSqlXml(sqlText);
        } catch (Exception ex) {
            throw new CommonException(HrptMessageConstants.ERROR_REPORT_PARAMETER_SET, ex);
        }
        if (!isXmlData) {
            reportMetaService.getMetaDataColumns(tenantId, datasourceCode, sqlText);
        }
        return sqlText;
    }

    @Override
    public Document parseXmlData(Long tenantId, Dataset dataset, HttpServletRequest request) {
        // 获取执行SQL
        String sqlText = reportMetaService.getSqlText(dataset.getSqlText(), dataset.getQueryParams(), request);
        // 获取sql类型
        String sqlType = Dataset.checkSqlType(sqlText);
        if (!Objects.equals(sqlType, HrptConstants.DataSetType.TYPE_A)){
            Assert.notNull(dataset.getDatasourceCode(), HrptMessageConstants.ERROR_NOSELECT_DATASOURCE);
        }
        // 获取Xml示例数据
        return this.parseXmlDataSample(tenantId, dataset.getDatasourceCode(), sqlType, sqlText);
    }

    @Override
    public Map<String, Object> parseMapData(Long tenantId, Dataset dataset, HttpServletRequest request) {
        // 获取执行SQL
        String sqlText = reportMetaService.getSqlText(dataset.getSqlText(), dataset.getQueryParams(), request);
        // 获取sql类型
        String sqlType = Dataset.checkSqlType(sqlText);
        if (!Objects.equals(sqlType, HrptConstants.DataSetType.TYPE_A)){
            Assert.notNull(dataset.getDatasourceCode(), HrptMessageConstants.ERROR_NOSELECT_DATASOURCE);
        }
        // 获取Xml示例数据
        return this.parseMapDataSample(tenantId, dataset.getDatasourceCode(), sqlType, sqlText);
    }

    @Override
    public Map<String, String> parseMapDataWithType(Long tenantId, Dataset dataset,  HttpServletRequest request) {
        Dataset set = datasetRepository.selectOne(new Dataset().setTenantId(tenantId).setDatasetCode(dataset.getDatasetCode()));
        Assert.notNull(set, BaseConstants.ErrorCode.DATA_INVALID);
        set.setQueryParams(dataset.getQueryParams());
        Map<String, Object> data = parseMapData(tenantId, set, request);
        Map<String, String> resultMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        resultMap.put(BaseConstants.FIELD_CONTENT, JSON.toJSONString(data));
        resultMap.put(Dataset.FIELD_SQL_TYPE, set.getSqlType());
        return resultMap;
    }

    /**
     * 根据数据集获取元数据列
     */
    private List<MetaDataColumn> getMetaDataColumnsByDatasetId(Long datasetId) {
        String metaColumns = datasetRepository.selectByPrimaryKey(datasetId).getMetaColumns();
        return JSON.parseArray(metaColumns, MetaDataColumn.class);
    }

    /**
     * 根据数据集获取查询参数
     */
    private List<QueryParameterOptions> getQueryParamsByDatasetId(Long datasetId) {
        String queryParam = datasetRepository.selectByPrimaryKey(datasetId).getQueryParams();
        return JSON.parseArray(queryParam, QueryParameterOptions.class);
    }

    /**
     * 验证参数合规性
     */
    private void checkParameter(String parameterJson) {
        // 参数合规性校验
        if (StringUtils.isNoneBlank(parameterJson)) {
            List<QueryParameterOptions> queryParameterList = reportMetaService.parseQueryParams(parameterJson);
            checkParameter(queryParameterList);
        }
    }

    /**
     * 验证参数合规性
     */
    private void checkParameter(List<QueryParameterOptions> queryParameterOptions) {
        queryParameterOptions.forEach(queryParameterOption -> Assert.isTrue(
                !queryParameterOption.getName().startsWith(HrptConstants.FixedParam.PREFIX),
                HrptMessageConstants.ERROR_PARAMETER_NAME));
    }

}
