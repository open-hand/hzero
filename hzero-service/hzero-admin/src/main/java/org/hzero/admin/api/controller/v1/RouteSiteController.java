package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.ServiceRouteService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 服务路由配置 管理 API
 *
 * @author bojiangzhou
 * @author zhitying.dong@hand-china.com 2018-12-14 12:39:44
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = SwaggerApiConfig.SERVICE_ROUTE_SITE)
@RestController("serviceRouteSiteController.v1")
@RequestMapping("/v1/routes")
public class RouteSiteController extends BaseController {

    @Autowired
    private ServiceRouteService routeService;

    @ApiOperation(value = "分页查询服务路由配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<ServiceRoute>> pageRoutes(@Encrypt ServiceRoute serviceRoute, @ApiIgnore PageRequest pageRequest) {
        return Results.success(routeService.pageServiceRoute(pageRequest, serviceRoute));
    }

    @ApiOperation(value = "服务路由配置明细(id)")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{serviceRouteId}")
    public ResponseEntity<ServiceRoute> detailById(@Encrypt @PathVariable Long serviceRouteId) {
        return Results.success(routeService.selectRouteDetails(serviceRouteId));
    }

    @ApiOperation(value = "根据服务名查询服务路由(serviceCode)")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/list")
    public ResponseEntity<List<ServiceRoute>> listByServiceCode(@RequestParam("serviceCode") String serviceCode) {
        return Results.success(routeService.selectRouteDetails(serviceCode));
    }

    @ApiOperation(value = "增加一个新路由并创建服务，必传参数：serviceCode/name/path")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<ServiceRoute> create(@Encrypt @RequestBody ServiceRoute serviceRoute) {
        return Results.success(routeService.createServiceRoute(serviceRoute));
    }

    @ApiOperation(value = "修改服务路由配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<ServiceRoute> update(@Encrypt @RequestBody ServiceRoute serviceRoute) {
        return Results.success(routeService.updateServiceRoute(serviceRoute));
    }

    @ApiOperation(value = "删除服务路由配置")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@Encrypt @RequestBody ServiceRoute serviceRoute) {
        routeService.deleteServiceRoute(serviceRoute);
        return Results.success();
    }

}
