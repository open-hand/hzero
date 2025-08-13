
package org.hzero.admin.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.api.dto.swagger.ControllerDTO;
import org.hzero.admin.app.service.SwaggerService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.infra.util.VersionUtil;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;
import java.util.Map;

/**
 * Swagger 管理API
 *
 * @author bergturing on 2020-5-9.
 */
@Api(tags = {SwaggerApiConfig.SWAGGER_SITE})
@RestController("swaggerSiteController.v1")
@RequestMapping("/v1/swagger")
public class SwaggerSiteController {
    /**
     * swagger 资源库对象
     */
    private final SwaggerService swaggerService;

    @Autowired
    public SwaggerSiteController(SwaggerService swaggerService) {
        this.swaggerService = swaggerService;
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询不包含跳过的服务的路由列表")
    @GetMapping("/resource")
    public ResponseEntity<List<SwaggerResource>> querySwaggerResources() {
        // 查询数据，并返回结果
        return Results.success(this.swaggerService.getSwaggerResource());
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("查询所有运行实例的api树形接口")
    @GetMapping("/tree")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> queryTreeMenu() {
        return Results.success(this.swaggerService.queryTreeMenu());
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("根据path的url和method查询单个path")
    @GetMapping("/{serviceName}/controllers/{name}/paths")
    public ResponseEntity<ControllerDTO> queryPathDetail(
            @PathVariable("serviceName") String serviceName,
            @PathVariable("name") String controllerName,
            @RequestParam(value = "version", required = false, defaultValue = VersionUtil.NULL_VERSION) String version,
            @RequestParam("operationId") String operationId) {
        return Results.success(this.swaggerService.queryPathDetail(serviceName, version, controllerName, operationId));
    }
}
