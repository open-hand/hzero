package org.hzero.swagger.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.swagger.app.DocumentService;
import org.hzero.swagger.config.SwaggerApiConfig;
import org.hzero.swagger.infra.constant.Versions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

/**
 * 获取swagger信息controller
 */
@Api(tags = SwaggerApiConfig.SWAGGER_DOCUMENT)
@RestController
@RequestMapping(value = "/docs")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    /**
     * 获取服务id对应的版本的swagger json
     *
     * @param name    服务id，形如 uaa
     * @param version 服务版本
     * @return String
     */
    @Permission(permissionPublic = true)
    @ApiIgnore
    @ApiOperation("获取服务id对应的版本swagger json字符串")
    @GetMapping(value = "/{servicePrefix}")
    public ResponseEntity<String> get(@PathVariable("servicePrefix") String name,
                                      @RequestParam(value = "version", required = false,
                                              defaultValue = Versions.NULL_VERSION) String version) {
        String swaggerJson;
        try {
            swaggerJson = documentService.getSwaggerJson(name, version);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            String log = "服务" + name + " version " + version + "没有在运行";
            return new ResponseEntity<>(log, HttpStatus.NOT_FOUND);
        }
        if ("".equals(swaggerJson)) {
            return new ResponseEntity<>(swaggerJson, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(swaggerJson, HttpStatus.OK);
        }
    }

    /**
     * 手动刷新表中swagger和刷新权限
     *
     * @param serviceName 服务名
     * @param version     服务版本
     * @return null
     */
    @Permission(permissionPublic = true)
    @ApiOperation("手动刷新表中swagger")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "serviceName", value = "服务名称", paramType = "path"),
        @ApiImplicitParam(name = "version", value = "标记版本", paramType = "query"),
    })
    @PostMapping(value = "/swagger/refresh/{serviceName}")
    public ResponseEntity refresh(@PathVariable("serviceName") String serviceName,
                                  @RequestParam(value = "version", required = false, defaultValue = Versions.NULL_VERSION) String version) {
        documentService.manualRefresh(serviceName, version);
        return Results.success("refresh swagger document success.");
    }

    /**
     * 手动刷新表中swagger和刷新权限
     *
     * @param serviceName 服务名
     * @param version     服务版本
     * @return null
     */
    @Permission(permissionWithin = true)
    @ApiOperation("刷新表中swagger(供内部调用)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serviceName", value = "服务名称", paramType = "path"),
            @ApiImplicitParam(name = "version", value = "标记版本", paramType = "query"),
    })
    @PostMapping(value = "/inner/swagger/refresh/{serviceName}")
    public ResponseEntity innerRefresh(@PathVariable("serviceName") String serviceName,
                                       @RequestParam(value = "version", required = false, defaultValue = Versions.NULL_VERSION) String version) {
        documentService.autoRefresh(serviceName, version);
        return Results.success();
    }
}
