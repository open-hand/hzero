package org.hzero.file.api.controller.v2;

import java.util.Objects;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.StorageConfig;
import org.hzero.file.domain.repository.StorageConfigRepository;
import org.hzero.file.infra.constant.FileServiceType;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 文件存储配置 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-06-29 12:58:25
 */
@Api(tags = FileSwaggerApiConfig.FILE_STORAGE_CONFIG_V2)
@RestController("storageConfigController.v2")
@RequestMapping("/v2/{organizationId}/storage-configs")
public class StorageConfigController extends BaseController {

    private final StorageConfigRepository storageConfigRepository;

    @Autowired
    public StorageConfigController(StorageConfigRepository storageConfigRepository) {
        this.storageConfigRepository = storageConfigRepository;
    }

    @ApiOperation(value = "文件存储配置信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{storageType}")
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<StorageConfig>> detail(@PathVariable Integer storageType, @PathVariable("organizationId") Long organizationId,
                                                      @ApiIgnore @SortDefault(value = StorageConfig.FIELD_STORAGE_CONFIG_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        StorageConfig sc = new StorageConfig().setStorageType(storageType).setTenantId(organizationId);
        Page<StorageConfig> storageConfigList = PageHelper.doPageAndSort(pageRequest, () -> storageConfigRepository.selectStorageConfig(sc));
        return Results.success(storageConfigList);
    }

    @ApiOperation(value = "租户默认的存储配置信息及同类型集合")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<StorageConfig>> defaultConfig(@PathVariable("organizationId") Long organizationId,
                                                             @ApiIgnore @SortDefault(value = StorageConfig.FIELD_STORAGE_CONFIG_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<StorageConfig> storageConfigList;
        StorageConfig defaultConfig = storageConfigRepository.selectOne(new StorageConfig().setDefaultFlag(BaseConstants.Flag.YES).setTenantId(organizationId));
        StorageConfig config = new StorageConfig().setTenantId(organizationId);
        if (defaultConfig == null) {
            storageConfigList = PageHelper.doPageAndSort(pageRequest, () -> storageConfigRepository.selectStorageConfig(config.setStorageType(FileServiceType.ALIYUN.getValue())));
        } else {
            storageConfigList = PageHelper.doPageAndSort(pageRequest, () -> storageConfigRepository.selectStorageConfig(config.setStorageType(defaultConfig.getStorageType())));
        }
        return Results.success(storageConfigList);
    }

    @ApiOperation(value = "修改文件存储配置信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Void> insertOrUpdate(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt StorageConfig storageConfig) {
        storageConfig.setTenantId(organizationId);
        // 若是本地存储,将不可为null的字段置为空串
        if (Objects.equals(storageConfig.getStorageType(), HfleConstant.Digital.SIX)) {
            storageConfig.initData();
        }
        // 校验配置数据
        validObject(storageConfig);
        // 根据不同类型校验
        if (Objects.equals(storageConfig.getStorageType(), FileServiceType.QCLOUD.getValue()) && !storageConfig.validateQCloud()) {
            throw new CommonException(HfleMessageConstant.ERROR_QCLOUD_CONFIG);
        }
        storageConfigRepository.insertOrUpdateStorageConfig(storageConfig);
        return Results.success();
    }

    @ApiOperation(value = "删除文件存储配置信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> deleteConfig(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt StorageConfig storageConfig) {
        storageConfig.setTenantId(organizationId);
        SecurityTokenHelper.validToken(storageConfig);
        storageConfigRepository.deleteConfig(storageConfig);
        return Results.success();
    }
}
