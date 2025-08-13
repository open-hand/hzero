package org.hzero.file.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.file.app.service.CapacityConfigService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.CapacityConfig;
import org.hzero.file.domain.entity.UploadConfig;
import org.hzero.file.domain.repository.CapacityConfigRepository;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
 * 文件容量配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
@Api(tags = FileSwaggerApiConfig.FILE_CAPACITY_CONFIG_SITE)
@RestController("capacityConfigSiteController.v1")
@RequestMapping("/v1/capacity-configs")
public class CapacityConfigSiteController extends BaseController {

    private final CapacityConfigService capacityConfigService;
    private final CapacityConfigRepository capacityConfigRepository;

    @Autowired
    public CapacityConfigSiteController(CapacityConfigService capacityConfigService,
                                        CapacityConfigRepository capacityConfigRepository) {
        this.capacityConfigService = capacityConfigService;
        this.capacityConfigRepository = capacityConfigRepository;
    }

    @ApiOperation(value = "文件容量配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY, HfleConstant.BODY_LIST_CONFIG})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", required = true, paramType = "query")
    })
    public ResponseEntity<CapacityConfig> pageCapacityConfig(Long tenantId,
                                                             @ApiIgnore @SortDefault(value = UploadConfig.FIELD_UPLOAD_CONFIG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(capacityConfigService.pageUploadConfig(tenantId, pageRequest));
    }

    @ApiOperation(value = "文件容量配置明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{capacityConfigId}")
    public ResponseEntity<CapacityConfig> detail(@PathVariable @Encrypt Long capacityConfigId) {
        return Results.success(capacityConfigRepository.selectByPrimaryKey(capacityConfigId));
    }

    @ApiOperation(value = "保存文件容量配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<CapacityConfig> update(@RequestBody @Encrypt CapacityConfig capacityConfig) {
        validObject(capacityConfig);
        return Results.success(capacityConfigService.createOrUpdate(capacityConfig));
    }
}
