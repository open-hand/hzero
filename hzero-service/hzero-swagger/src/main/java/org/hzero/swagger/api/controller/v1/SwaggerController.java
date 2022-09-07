package org.hzero.swagger.api.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.core.util.Results;
import org.hzero.swagger.api.dto.swagger.ControllerDTO;
import org.hzero.swagger.app.ApiService;
import org.hzero.swagger.app.SwaggerService;
import org.hzero.swagger.infra.constant.Versions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

/**
 * swagger控制器
 *
 * @author huiyu.chen
 * @author wuguokai
 */
@RestController
public class SwaggerController {

    @Autowired
    private SwaggerService swaggerService;
    @Autowired
    private ApiService apiService;

    @ApiIgnore
    @RequestMapping(value = "/swagger-resources/configuration/security")
    ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return Results.success(swaggerService.getSecurityConfiguration());
    }

    @ApiIgnore
    @RequestMapping(value = "/swagger-resources/configuration/ui")
    ResponseEntity<UiConfiguration> uiConfiguration() {
        return Results.success(swaggerService.getUiConfiguration());
    }

    /**
     * 获取swagger服务列表，swagger页面自动请求
     *
     * @return list
     */
    @ApiIgnore
    @RequestMapping(value = "/swagger-resources")
    ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<>(swaggerService.getSwaggerResource(), HttpStatus.OK);
    }

    /**
     * 查询不包含跳过的服务的路由列表
     */
    @ApiIgnore
    @GetMapping("/v1/swaggers/resources")
    public ResponseEntity<List<SwaggerResource>> resources() {
        return Results.success(swaggerService.getSwaggerResource());
    }

    /**
     * 查询服务controller和接口
     */
    @ApiIgnore
    @GetMapping("/v1/swaggers/controllers/{servicePrefix}")
    public ResponseEntity<Page<ControllerDTO>> queryByNameAndVersion(
            @PathVariable("servicePrefix") String serviceName,
            @RequestParam(value = "version", required = false,defaultValue = Versions.NULL_VERSION) String version,
            @RequestParam(required = false, name = "params") String params,
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "description") String description,
            @SortDefault(value = "name", direction = Sort.Direction.ASC)
                    PageRequest pageRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("params", params);
        map.put("name", name);
        map.put("description", description);
        return Results.success(apiService.getControllers(serviceName, version, pageRequest, map));
    }

}
