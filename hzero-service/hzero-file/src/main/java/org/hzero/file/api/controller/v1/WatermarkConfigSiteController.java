package org.hzero.file.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.file.app.service.WatermarkConfigService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.WatermarkConfig;
import org.hzero.file.domain.repository.WatermarkConfigRepository;
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
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 水印配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
@Api(tags = FileSwaggerApiConfig.WATERMARK_CONFIG_SITE)
@RestController("watermarkConfigSiteController.v1")
@RequestMapping("/v1/watermark-configs")
public class WatermarkConfigSiteController extends BaseController {

    private final WatermarkConfigService watermarkConfigService;
    private final WatermarkConfigRepository watermarkConfigRepository;

    @Autowired
    public WatermarkConfigSiteController(WatermarkConfigService watermarkConfigService,
                                         WatermarkConfigRepository watermarkConfigRepository) {
        this.watermarkConfigService = watermarkConfigService;
        this.watermarkConfigRepository = watermarkConfigRepository;
    }

    @ApiOperation(value = "水印配置列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户", paramType = "query"),
            @ApiImplicitParam(name = "watermarkCode", value = "水印配置编码", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "描述", paramType = "query"),
            @ApiImplicitParam(name = "watermarkType", value = "水印类型", paramType = "query")
    })
    public ResponseEntity<Page<WatermarkConfig>> pageWatermark(Long tenantId, String watermarkCode, String description, String watermarkType,
                                                               @ApiIgnore @SortDefault(value = WatermarkConfig.FIELD_WATERMARK_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(watermarkConfigRepository.pageWatermark(pageRequest, tenantId, watermarkCode, description, watermarkType));
    }

    @ApiOperation(value = "水印配置明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{watermarkId}")
    public ResponseEntity<WatermarkConfig> detailWatermark(@PathVariable @Encrypt Long watermarkId) {
        return Results.success(watermarkConfigService.detailWatermarkConfig(null, watermarkId));
    }

    @ApiOperation(value = "创建水印配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<WatermarkConfig> createWatermark(@RequestBody @Encrypt WatermarkConfig watermarkConfig) {
        validObject(watermarkConfig);
        return Results.success(watermarkConfigService.createWatermarkConfig(watermarkConfig));
    }

    @ApiOperation(value = "修改水印配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<WatermarkConfig> updateWatermark(@RequestBody @Encrypt WatermarkConfig watermarkConfig) {
        validObject(watermarkConfig);
        SecurityTokenHelper.validToken(watermarkConfig);
        return Results.success(watermarkConfigService.updateWatermarkConfig(watermarkConfig));
    }
}
