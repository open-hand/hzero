package org.hzero.platform.api.controller.v1;

import java.util.List;
import java.util.Map;

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
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = {PlatformSwaggerApiConfig.DATASOURCE_SITE})
@RestController("datasourceSiteController.v1")
@RequestMapping("/v1/datasources")
public class DatasourceSiteController extends BaseController {

    @Autowired
    private DatasourceInfoService datasourceRelService;

    @ApiOperation(value = "数据源配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "datasourceCode", value = "数据源编码", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "数据源描述", paramType = "query"),
            @ApiImplicitParam(name = "datasourceUrl", value = "数据源URL地址", paramType = "query"),
            @ApiImplicitParam(name = "dsPurposeCode", value = "数据源用途", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注信息", paramType = "query")
    })
    @CustomPageRequest
    public ResponseEntity<Page<Datasource>> pageDatasource(Datasource datasource, @ApiIgnore PageRequest pageRequest) {
        Page<Datasource> page = datasourceRelService.pageDatasource(pageRequest, datasource, false);
        return Results.success(page);
    }

    @ApiOperation(value = "获取数据源配置明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{datasourceId}")
    public ResponseEntity<Datasource> detail(@PathVariable @Encrypt Long datasourceId) {
        return Results.success(datasourceRelService.selectDatasource(datasourceId));
    }

    @ApiOperation(value = "创建数据源配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Datasource> createDatasource(@RequestBody Datasource datasource) {
        validObject(datasource);
        datasource = datasourceRelService.createDatasource(datasource);
        return Results.success(datasource);
    }

    @ApiOperation(value = "修改数据源配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Datasource> updateDatasource(@RequestBody @Encrypt Datasource datasource) {
        SecurityTokenHelper.validTokenIgnoreInsert(datasource);
        validObject(datasource);
        datasource = datasourceRelService.updateDatasource(datasource);
        return Results.success(datasource);
    }

    @ApiOperation(value = "删除数据源配置")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity removeDatasourceById(@RequestBody @Encrypt Datasource datasource) {
        SecurityTokenHelper.validTokenIgnoreInsert(datasource);
        datasourceRelService.removeDatasourceById(datasource.getDatasourceId());
        return Results.success();
    }

    @ApiOperation(value = "获取连接初始化信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{dbType}/initialize")
    public ResponseEntity<?> getConnectInit(@PathVariable String dbType) {
        Map<String, String> resultMap = datasourceRelService.getConnectInit(dbType);
        return Results.success(resultMap);
    }

    @ApiOperation(value = "获取连接池属性")
    @Permission(level = ResourceLevel.SITE)
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
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/test-connection")
    public ResponseEntity<Datasource> testConnection(@RequestBody @Encrypt Datasource datasource) {
        validObject(datasource);
        datasourceRelService.testConnection(datasource);
        return Results.success();
    }


    @ApiOperation(value = "条件查询所有匹配的数据源信息")
    @Permission(permissionWithin = true)
    @PostMapping("/all")
    public ResponseEntity<List<Datasource>> listDatasourceByCondition(@RequestBody(required = false) Datasource datasource) {
        return Results.success(datasourceRelService.listDatasourceByCondition(datasource));
    }
}
