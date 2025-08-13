package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.ConfigService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Config;
import org.hzero.platform.domain.repository.ConfigRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 系统配置管理controller
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/21 14:24
 */
@Api(tags = PlatformSwaggerApiConfig.CONFIG_MANAGE_SITE)
@RestController("configManagerSiteController.v1")
@RequestMapping("/v1/config")
public class ConfigManagerSiteController extends BaseController {
    private ConfigRepository configRepository;
    private ConfigService configService;

    @Autowired
    public ConfigManagerSiteController(ConfigRepository configRepository, ConfigService configService) {
        this.configRepository = configRepository;
        this.configService = configService;
    }

    /**
     * 根据租户id查询系统配置
     *
     * @return 通用返回结果
     */
    @ApiOperation(value = "根据租户id查询系统配置", notes = "根据租户id查询系统配置")
    @GetMapping("/{tenantId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity selectConfigByTenantId(@PathVariable Long tenantId) {
        return Results.success(configRepository.selectConfigByTenantId(tenantId));
    }

    /**
     * 根据租户id更新系统配置
     *
     * @return 更新后的租户系统配置
     */
    @ApiOperation(value = "更新租户系统配置", notes = "更新租户系统配置")
    @PutMapping("/{tenantId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity insertOrUpdate(@RequestBody @Encrypt List<Config> configList, @PathVariable Long tenantId) {
        validList(configList);
        SecurityTokenHelper.validTokenIgnoreInsert(configList);
        return Results.success(configRepository.insertOrUpdateConfig(configList, tenantId));
    }

    /**
     * 初始化公司配置
     *
     * @return 更新后的租户系统配置
     */
    @ApiIgnore
    @PostMapping("/initialization")
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    public ResponseEntity<List<Config>> initCompanyConfig(@RequestBody List<Config> configList) {
        return Results.success(configService.initCompanyConfig(BaseConstants.DEFAULT_TENANT_ID, configList));
    }
}
