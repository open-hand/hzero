package org.hzero.file.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.file.app.service.ServerConfigService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.ServerConfig;
import org.hzero.file.domain.repository.ServerConfigRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
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
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 服务器上传配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-03 20:38:55
 */
@Api(tags = FileSwaggerApiConfig.SERVER_CONFIG)
@RestController("serverConfigController.v1")
@RequestMapping("/v1/{organizationId}/server-configs")
public class ServerConfigController extends BaseController {

    private final ServerConfigService serverConfigService;
    private final ServerConfigRepository serverConfigRepository;

    @Autowired
    public ServerConfigController(ServerConfigService serverConfigService,
                                  ServerConfigRepository serverConfigRepository) {
        this.serverConfigService = serverConfigService;
        this.serverConfigRepository = serverConfigRepository;
    }

    @ApiOperation(value = "服务器上传配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "configCode", value = "配置编码", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "描述", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标识", paramType = "query")
    })
    public ResponseEntity<Page<ServerConfig>> pageConfig(@PathVariable Long organizationId, String configCode, String description, Integer enabledFlag,
                                                         @ApiIgnore @SortDefault(value = ServerConfig.FIELD_CONFIG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(serverConfigRepository.pageConfig(pageRequest, organizationId, configCode, description, enabledFlag));
    }

    @ApiOperation(value = "服务器上传配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{configId}")
    public ResponseEntity<ServerConfig> detailConfig(@PathVariable Long organizationId, @PathVariable @Encrypt Long configId) {
        return Results.success(serverConfigService.detailConfig(organizationId, configId));
    }

    @ApiOperation(value = "创建服务器上传配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<ServerConfig> createConfig(@PathVariable Long organizationId, @RequestBody @Encrypt ServerConfig serverConfig) {
        serverConfig.setTenantId(organizationId);
        validObject(serverConfig);
        return Results.success(serverConfigService.createConfig(serverConfig));
    }

    @ApiOperation(value = "修改服务器上传配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<ServerConfig> updateConfig(@PathVariable Long organizationId, @RequestBody @Encrypt ServerConfig serverConfig) {
        serverConfig.setTenantId(organizationId);
        SecurityTokenHelper.validTokenIgnoreInsert(serverConfig);
        return Results.success(serverConfigService.updateConfig(serverConfig));
    }
}
