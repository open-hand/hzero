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
@Api(tags = FileSwaggerApiConfig.FILE_STORAGE_CONFIG_SITE_V2)
@RestController("storageConfigSiteController.v2")
@RequestMapping("/v2/storage-configs")
public class StorageConfigSiteController extends BaseController {

    private final StorageConfigRepository storageConfigRepository;

    @Autowired
    public StorageConfigSiteController(StorageConfigRepository storageConfigRepository) {
        this.storageConfigRepository = storageConfigRepository;
    }

    @ApiOperation(value = "文件存储配置信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{storageType}")
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<StorageConfig>> detail(@PathVariable Integer storageType, Long tenantId,
                                                      @ApiIgnore @SortDefault(value = StorageConfig.FIELD_STORAGE_CONFIG_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        StorageConfig sc = new StorageConfig().setStorageType(storageType).setTenantId(tenantId);
        Page<StorageConfig> storageConfigList = PageHelper.doPageAndSort(pageRequest, () -> storageConfigRepository.selectStorageConfig(sc));
        return Results.success(storageConfigList);
    }

    @ApiOperation(value = "租户默认的存储配置信息及同类型集合")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<StorageConfig>> defaultConfig(Long tenantId,
                                                             @ApiIgnore @SortDefault(value = StorageConfig.FIELD_STORAGE_CONFIG_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<StorageConfig> storageConfigList;
        StorageConfig defaultConfig = storageConfigRepository.selectOne(new StorageConfig().setDefaultFlag(BaseConstants.Flag.YES).setTenantId(tenantId));
        StorageConfig config = new StorageConfig().setTenantId(tenantId);
        if (defaultConfig == null) {
            storageConfigList = PageHelper.doPageAndSort(pageRequest, () -> storageConfigRepository.selectStorageConfig(config.setStorageType(FileServiceType.ALIYUN.getValue())));
        } else {
            storageConfigList = PageHelper.doPageAndSort(pageRequest, () -> storageConfigRepository.selectStorageConfig(config.setStorageType(defaultConfig.getStorageType())));
        }
        return Results.success(storageConfigList);
    }

    @ApiOperation(value = "修改文件存储配置信息")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Void> insertOrUpdate(@RequestBody @Encrypt StorageConfig storageConfig) {
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
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> deleteConfig(@RequestBody @Encrypt StorageConfig storageConfig) {
        storageConfigRepository.deleteConfig(storageConfig);
        return Results.success();
    }
}
