package org.hzero.platform.api.controller.v1;

import java.util.Map;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DatasourceInfoService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Datasource;
import org.hzero.platform.domain.vo.DatasourcePoolOptionVO;
import org.hzero.platform.infra.enums.DBPoolTypeEnum;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 数据源配置 管理 API
 *
 * @author like.zhang@hand-china.com 2018-09-13 14:10:13
 */
@Api(tags = {PlatformSwaggerApiConfig.DATASOURCE})
@RestController("datasourceController.v1")
@RequestMapping("/v1/{organizationId}/datasources")
public class DatasourceController extends BaseController {

    @Autowired
    private DatasourceInfoService datasourceInfoService;

    @ApiOperation(value = "数据源配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({@ApiImplicitParam(name = "datasourceCode", value = "数据源编码", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "数据源描述(数据源名称)", paramType = "query"),
            @ApiImplicitParam(name = "datasourceUrl", value = "数据源URL地址", paramType = "query"),
            @ApiImplicitParam(name = "dsPurposeCode", value = "数据源用途", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注信息", paramType = "query")})
    @CustomPageRequest
    public ResponseEntity<Page<Datasource>> pageDatasource(@PathVariable("organizationId") Long organizationId,
                                                           Datasource datasource,
                                                           @ApiIgnore @SortDefault(value = Datasource.FIELD_DATASOURCE_CODE) PageRequest pageRequest) {
        datasource.setTenantId(organizationId);
        Page<Datasource> page = datasourceInfoService.pageDatasource(pageRequest, datasource, true);
        return Results.success(page);
    }

    @ApiOperation(value = "获取数据源配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{datasourceId}")
    public ResponseEntity<Datasource> detail(@PathVariable Long organizationId, @PathVariable @Encrypt Long datasourceId) {
        return Results.success(datasourceInfoService.selectDatasource(datasourceId));
    }

    @ApiOperation(value = "根据编码获取数据源配置明细")
    @Permission(permissionWithin = true)
    @GetMapping("/{datasourceCode}/detail")
    public ResponseEntity<Datasource> getByUnique(@PathVariable Long organizationId, @PathVariable String datasourceCode, @RequestParam String dsPurposeCode) {
        return Results.success(datasourceInfoService.getByUnique(organizationId, datasourceCode, dsPurposeCode));
    }

    @ApiOperation(value = "创建数据源配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Datasource> createDatasource(@PathVariable("organizationId") Long organizationId,
                                                       @RequestBody Datasource datasource) {
        datasource.setTenantId(organizationId);
        validObject(datasource);
        datasource = datasourceInfoService.createDatasource(datasource);
        return Results.success(datasource);
    }

    @ApiOperation(value = "修改数据源配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Datasource> updateDatasource(@PathVariable("organizationId") Long organizationId,
                                                       @RequestBody @Encrypt Datasource datasource) {
        SecurityTokenHelper.validTokenIgnoreInsert(datasource);
        datasource.setTenantId(organizationId);
        validObject(datasource);
        datasource = datasourceInfoService.updateDatasource(datasource);
        return Results.success(datasource);
    }

    @ApiOperation(value = "删除数据源配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> removeDatasourceById(@RequestBody @Encrypt Datasource datasource) {
        SecurityTokenHelper.validTokenIgnoreInsert(datasource);
        datasourceInfoService.removeDatasourceById(datasource.getDatasourceId());
        return Results.success();
    }

    @ApiOperation(value = "获取连接初始化信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dbType}/initialize")
    public ResponseEntity<?> getConnectInit(@PathVariable String dbType) {
        Map<String, String> resultMap = datasourceInfoService.getConnectInit(dbType);
        return Results.success(resultMap);
    }

    @ApiOperation(value = "获取连接池属性")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dbPoolType}/dbpool-option")
    public ResponseEntity<?> getDbPoolOption(@PathVariable String dbPoolType) {
        switch (DBPoolTypeEnum.valueOf2(dbPoolType)) {
            case C3P0:
                return Results.success(new DatasourcePoolOptionVO.C3p0OptionVO());
            case DBCP2:
                return Results.success(new DatasourcePoolOptionVO.Dbcp2OptionVO());
            case DRUID:
                return Results.success(new DatasourcePoolOptionVO.DruidOptionVO());
            default:
                return Results.success();
        }
    }

    @ApiOperation(value = "测试数据源连接")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/test-connection")
    public ResponseEntity<Datasource> testConnection(@PathVariable("organizationId") Long organizationId,
                                                     @RequestBody @Encrypt Datasource datasource) {
        datasource.setTenantId(organizationId);
        validObject(datasource);
        datasourceInfoService.testConnection(datasource);
        return Results.success();
    }
}
