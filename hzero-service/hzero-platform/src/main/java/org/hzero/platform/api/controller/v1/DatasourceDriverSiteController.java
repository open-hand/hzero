package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DatasourceDriverService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DatasourceDriver;
import org.hzero.platform.domain.repository.DatasourceDriverRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 数据源驱动配置 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-08-21 19:55:37
 */
@Api(tags = { PlatformSwaggerApiConfig.DATASOURCE_DRIVER_SITE})
@RestController("datasourceDriverSiteController.v1")
@RequestMapping("/v1/datasource-drivers")
public class DatasourceDriverSiteController extends BaseController {

    private final DatasourceDriverRepository driverRepository;
    private final DatasourceDriverService driverService;

    @Autowired
    public DatasourceDriverSiteController(DatasourceDriverRepository driverRepository,
            DatasourceDriverService driverService) {
        this.driverRepository = driverRepository;
        this.driverService = driverService;
    }

    @ApiOperation(value = "数据源驱动配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<DatasourceDriver>> listDatasourceDrivers(DatasourceDriver datasourceDriver,
            @ApiIgnore @SortDefault(value = DatasourceDriver.FIELD_DRIVER_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(driverRepository.listDatasourceDrivers(pageRequest, datasourceDriver, false));
    }

    @ApiOperation(value = "数据源驱动配置明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{driverId}")
    public ResponseEntity<DatasourceDriver> detail(@PathVariable @Encrypt Long driverId) {
        return Results.success(driverRepository.getDriverDetails(driverId));
    }

    @ApiOperation(value = "创建数据源驱动配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<DatasourceDriver> create(@RequestBody DatasourceDriver datasourceDriver) {
        validObject(datasourceDriver);
        return Results.success(driverService.createDatasourceDriver(datasourceDriver));
    }

    @ApiOperation(value = "修改数据源驱动配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<DatasourceDriver> update(@RequestBody @Encrypt DatasourceDriver datasourceDriver) {
        validObject(datasourceDriver);
        SecurityTokenHelper.validToken(datasourceDriver);
        return Results.success(driverService.updateDatasourceDriver(datasourceDriver));
    }

    @ApiOperation(value = "通过数据库类型获取数据源驱动")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "SITEId", value = "租户Id", paramType = "query"),
            @ApiImplicitParam(name = "databaseType", value = "数据库类型", paramType = "query")})
    @GetMapping("/by-type")
    public ResponseEntity<DatasourceDriver> getDriverByDatabaseType(@RequestParam("tenantId") Long tenantId,
            @RequestParam("databaseType") String databaseType) {
        return Results.success(driverRepository.getDriverByDatabaseType(tenantId, databaseType));
    }

    @ApiOperation(value = "删除数据源驱动")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@RequestBody @Encrypt DatasourceDriver datasourceDriver) {
        SecurityTokenHelper.validToken(datasourceDriver);
        driverService.deleteDriver(datasourceDriver);
        return Results.success();
    }

    @ApiOperation(value = "条件查询所有匹配的数据源驱动信息")
    @Permission(permissionWithin = true)
    @PostMapping("/all")
    public ResponseEntity<List<DatasourceDriver>> listDriverByCondition(@RequestBody(required = false) DatasourceDriver datasourceDriver) {
        return Results.success(driverRepository.listDriverByCondition(datasourceDriver));
    }

}
