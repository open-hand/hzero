package org.hzero.report.api.controller.v1;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dom4j.Document;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.DatasetService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.domain.repository.DatasetRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.report.infra.meta.option.QueryParameterOptions;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 报表数据集 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-10-31 15:36:59
 */
@Api(tags = ReportSwaggerApiConfig.DATASET)
@RestController("datasetController.v1")
@RequestMapping("/v1/{organizationId}/datasets")
public class DatasetController extends BaseController {

    @Autowired
    private DatasetService datasetService;
    @Autowired
    private DatasetRepository datasetRepository;

    @ApiOperation(value = "报表数据集列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Dataset>> list(@PathVariable Long organizationId, Dataset dataset,
                                              @ApiIgnore @SortDefault(value = Dataset.FIELD_DATASET_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dataset.setTenantId(organizationId);
        return Results.success(datasetRepository.selectDatasets(pageRequest, dataset));
    }

    @ApiOperation(value = "报表数据集明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{dataSetId}")
    public ResponseEntity<Dataset> detail(@PathVariable Long organizationId, @Encrypt @PathVariable Long dataSetId) {
        return Results.success(datasetRepository.selectDataset(organizationId, dataSetId));
    }

    @ApiOperation(value = "编码查询报表数据集明细")
    @Permission(permissionWithin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/detail")
    public ResponseEntity<Dataset> detailDataSet(@PathVariable Long organizationId, String datasetCode) {
        return Results.success(datasetRepository.selectOne(new Dataset().setTenantId(organizationId).setDatasetCode(datasetCode)));
    }

    @ApiOperation(value = "创建报表数据集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Dataset> create(@PathVariable Long organizationId, @RequestBody Dataset dataset) {
        dataset.setTenantId(organizationId);
        this.validObject(dataset);
        dataset.validateDataset(datasetRepository);
        datasetService.insertDataset(dataset);
        return Results.success(dataset);
    }

    @ApiOperation(value = "修改报表数据集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Dataset> update(@PathVariable Long organizationId, @Encrypt @RequestBody Dataset dataset) {
        dataset.setTenantId(organizationId);
        this.validObject(dataset);
        SecurityTokenHelper.validToken(dataset);
        datasetService.updateDataset(dataset);
        return Results.success(dataset);
    }

    @ApiOperation(value = "预览SQL语句")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping(value = "/preview-sql")
    public ResponseEntity<Map<String, String>> previewSql(@PathVariable Long organizationId, @Encrypt @RequestBody Dataset dataset, final HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        String sqlText = this.datasetService.previewSql(organizationId, dataset.getDatasourceCode(), dataset.getSqlText(), dataset.getQueryParams(), request);
        resultMap.put(HrptConstants.ParamDataSource.SQL, sqlText);
        return Results.success(resultMap);
    }

    @ApiOperation(value = "获取元数据列集合")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping(value = "/execute-sql")
    public ResponseEntity<List<MetaDataColumn>> execSqlText(@PathVariable Long organizationId, @Encrypt @RequestBody Dataset dataset, final HttpServletRequest request) {
        Assert.notNull(dataset.getDatasourceCode(), HrptMessageConstants.ERROR_NOSELECT_DATASOURCE);
        String sqlText = this.datasetService.getSqlText(dataset.getSqlText(), dataset.getQueryParams(), request);
        return Results.success(this.datasetService.initMetaDataColumns(dataset.getDatasetId(), organizationId, dataset.getDatasourceCode(), sqlText));
    }

    @ApiOperation(value = "获取数据集参数集合")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping(value = "/extract-param")
    public ResponseEntity<List<QueryParameterOptions>> extractParam(@PathVariable Long organizationId, @Encrypt @RequestBody Dataset dataset) {
        return Results.success(this.datasetService.initQueryParameters(dataset.getDatasetId(), dataset.getSqlText()));
    }

    @ApiOperation(value = "删除报表数据集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@PathVariable Long organizationId, @Encrypt @RequestBody Dataset dataset) {
        SecurityTokenHelper.validToken(dataset);
        Assert.isTrue(!dataset.existReference(datasetRepository), HrptMessageConstants.ERROR_EXIST_REFERENCE);
        datasetRepository.deleteByPrimaryKey(dataset.getDatasetId());
        return Results.success();
    }

    @ApiOperation(value = "获取XML示例数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping(value = "/xml-sample")
    public ResponseEntity<Map<String, String>> parseXmlData(@PathVariable Long organizationId, @Encrypt @RequestBody Dataset dataset, final HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        Document xml = this.datasetService.parseXmlData(organizationId, dataset, request);
        resultMap.put(BaseConstants.FIELD_CONTENT, xml.asXML());
        return Results.success(resultMap);
    }

    @ApiOperation(value = "获取MAP示例数据")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping(value = "/map-sample")
    public ResponseEntity<Map<String, String>> parseMapData(@PathVariable Long organizationId, @Encrypt @RequestBody Dataset dataset, final HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        Map<String, Object> data = this.datasetService.parseMapData(organizationId, dataset, request);
        resultMap.put(BaseConstants.FIELD_CONTENT, JSON.toJSONString(data));
        return Results.success(resultMap);
    }

    /**
     * 预警平台调用
     */
    @ApiOperation(value = "获取MAP示例数据及数据集类型")
    @Permission(permissionWithin = true)
    @PostMapping(value = "/type/map-sample")
    public ResponseEntity<Map<String, String>> parseMapDataWithType(@PathVariable Long organizationId, @RequestBody Dataset dataset, final HttpServletRequest request) {
        return Results.success(datasetService.parseMapDataWithType(organizationId, dataset, request));
    }
}
