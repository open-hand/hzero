package org.hzero.file.api.controller.v1;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.file.app.service.CapacityConfigService;
import org.hzero.file.app.service.UploadConfigService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.UploadConfig;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 文件上传配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-14 14:16:33
 */
@Api(tags = FileSwaggerApiConfig.FILE_UPLOAD_CONFIG)
@RestController("uploadConfigController.v1")
@RequestMapping("/v1/{organizationId}/upload-configs")
public class UploadConfigController extends BaseController {

    private final UploadConfigRepository uploadConfigRepository;
    private final CapacityConfigService capacityConfigService;
    private final UploadConfigService uploadConfigService;

    @Autowired
    public UploadConfigController(UploadConfigRepository uploadConfigRepository,
                                  CapacityConfigService capacityConfigService,
                                  UploadConfigService uploadConfigService) {
        this.uploadConfigRepository = uploadConfigRepository;
        this.capacityConfigService = capacityConfigService;
        this.uploadConfigService = uploadConfigService;
    }

    @ApiOperation(value = "文件上传配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = {BaseConstants.FIELD_BODY, HfleConstant.BODY_LIST_CONFIG})
    @GetMapping("/{uploadConfigId}")
    public ResponseEntity<UploadConfig> detailUploadConfig(@PathVariable("organizationId") Long organizationId,
                                                           @PathVariable @Encrypt Long uploadConfigId) {
        UploadConfig uploadConfig = new UploadConfig();
        uploadConfig.setTenantId(organizationId);
        uploadConfig.setUploadConfigId(uploadConfigId);
        return Results.success(uploadConfigRepository.selectOne(uploadConfig));
    }

    @ApiOperation(value = "文件上传配置查询明细")
    @Permission(permissionLogin = true)
    @GetMapping("/detail")
    public ResponseEntity<UploadConfig> detail(@PathVariable("organizationId") Long organizationId,
                                               @RequestParam @ApiParam(value = "文件目录", required = true) String bucketName,
                                               @ApiParam(value = "文件目录") String directory) {
        if (directory == null) {
            directory = StringUtils.EMPTY;
        }
        return Results.success(uploadConfigService.detailConfig(bucketName, organizationId, directory));
    }

    @ApiOperation(value = "创建文件上传配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @PostMapping
    public ResponseEntity<UploadConfig> createUploadConfig(@PathVariable("organizationId") Long organizationId,
                                                           @RequestBody @Encrypt UploadConfig uploadConfig) {
        uploadConfig.setTenantId(organizationId);
        if (uploadConfig.getDirectory() == null) {
            uploadConfig.setDirectory(StringUtils.EMPTY);
        }
        uploadConfig.setDirectory(uploadConfig.getDirectory());
        validObject(uploadConfig);
        uploadConfig.validateRepeat(uploadConfigRepository);
        uploadConfig.validateSize(capacityConfigService);
        return Results.success(uploadConfigService.createUploadConfig(uploadConfig));
    }


    @ApiOperation(value = "修改文件上传配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @PutMapping
    public ResponseEntity<UploadConfig> updateUploadConfig(@PathVariable("organizationId") Long organizationId,
                                                           @RequestBody @Encrypt UploadConfig uploadConfig) {
        uploadConfig.setTenantId(organizationId);
        if (uploadConfig.getDirectory() == null) {
            uploadConfig.setDirectory(StringUtils.EMPTY);
        }
        uploadConfig.setDirectory(uploadConfig.getDirectory());
        SecurityTokenHelper.validToken(uploadConfig);
        validObject(uploadConfig);
        uploadConfig.validateSize(capacityConfigService);
        return Results.success(uploadConfigService.updateUploadConfig(uploadConfig));
    }

    @ApiOperation(value = "删除文件上传配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> deleteUploadConfig(@RequestBody @Encrypt UploadConfig uploadConfig) {
        SecurityTokenHelper.validToken(uploadConfig);
        uploadConfigService.deleteUploadConfig(uploadConfig.getUploadConfigId());
        return Results.success();
    }
}