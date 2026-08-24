package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.ServiceConfigService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.ServiceConfig;
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
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 服务配置 管理 API
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-17 09:42:38
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = {SwaggerApiConfig.SERVICE_CONFIG_SITE})
@RestController("serviceConfigSiteController.v1")
@RequestMapping("/v1/configs")
public class ConfigSiteController extends BaseController {

    @Autowired
    private ServiceConfigService configService;

    @ApiOperation(value = "创建配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<ServiceConfig> create(@Encrypt @RequestBody ServiceConfig serviceConfig) {
        return Results.success(configService.createConfig(serviceConfig));
    }

    @ApiOperation(value = "修改配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<ServiceConfig> update(@Encrypt @RequestBody ServiceConfig serviceConfig) {
        return Results.success(configService.updateConfig(serviceConfig));
    }

    /**
     * 删除配置，默认配置不可删除
     *
     * @param serviceConfig 配置信息
     */
    @ApiOperation("删除配置，默认配置不可删除")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity delete(@Encrypt @RequestBody ServiceConfig serviceConfig) {
        configService.deleteServiceConfig(serviceConfig);
        return Results.success();
    }

    /**
     * 查询详细
     *
     * @param serviceConfigId 配置id
     * @return 查询配置的yaml形式
     */
    @ApiOperation("查询配置详细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/{serviceConfigId}")
    public ResponseEntity<ServiceConfig> queryYaml(@Encrypt @PathVariable("serviceConfigId") Long serviceConfigId) {
        return Results.success(configService.selectSelf(serviceConfigId));
    }

    @ApiOperation("分页查询配置列表信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ServiceConfig>> pageServiceConfigList(@Encrypt ServiceConfig serviceConfig,
                                                                     @ApiIgnore @SortDefault(value = ServiceConfig.FIELD_SERVICE_CONFIG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(configService.pageServiceConfigList(serviceConfig, pageRequest));
    }

}
