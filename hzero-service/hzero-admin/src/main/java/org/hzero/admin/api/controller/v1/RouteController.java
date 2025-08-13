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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 服务路由配置 管理 API
 *
 * @author bojiangzhou
 * @author zhitying.dong@hand-china.com 2018-12-14 12:39:44
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = SwaggerApiConfig.SERVICE_ROUTE)
@RestController("serviceRouteController.v1")
@RequestMapping("/v1/{organizationId}/routes")
public class RouteController extends BaseController {

    @Autowired
    private ServiceRouteService routeService;

    @ApiOperation(value = "分页查询服务路由列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<ServiceRoute>> pageRoutes(@PathVariable("organizationId") Long organizationId,
                                                         @Encrypt ServiceRoute serviceRoute,
                                                         @ApiIgnore PageRequest pageRequest) {
        return Results.success(routeService.pageServiceRoute(pageRequest, serviceRoute));
    }

}
