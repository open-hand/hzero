package org.hzero.file.app.service.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.app.service.CapacityConfigService;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.UploadConfigService;
import org.hzero.file.domain.entity.CapacityConfig;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.UploadConfig;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.domain.vo.UploadConfigVO;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.util.FileHeaderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 并发请求应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/20 11:25
 */
@Service
public class UploadConfigServiceImpl implements UploadConfigService {

    private final UploadConfigRepository uploadConfigRepository;
    private final CapacityConfigService capacityConfigService;
    private final CapacityUsedService capacityUsedService;
    private final RedisHelper redisHelper;

    @Autowired
    public UploadConfigServiceImpl(UploadConfigRepository uploadConfigRepository,
                                   CapacityConfigService capacityConfigService,
                                   CapacityUsedService capacityUsedService,
                                   RedisHelper redisHelper) {
        this.uploadConfigRepository = uploadConfigRepository;
        this.capacityConfigService = capacityConfigService;
        this.capacityUsedService = capacityUsedService;
        this.redisHelper = redisHelper;
    }

    @Override
    public UploadConfig detailConfig(String bucketName, Long tenantId, String directory) {
        UploadConfig uploadConfig = uploadConfigRepository.selectOne(
                new UploadConfig().setTenantId(tenantId).setBucketName(bucketName).setDirectory(directory));
        // 若未获取租户配置，查询平台配置
        if (uploadConfig == null) {
            uploadConfig = uploadConfigRepository.selectOne(new UploadConfig()
                    .setTenantId(BaseConstants.DEFAULT_TENANT_ID).setBucketName(bucketName).setDirectory(directory));
        }
        return uploadConfig;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UploadConfig createUploadConfig(UploadConfig uploadConfig) {

        uploadConfigRepository.insertSelective(uploadConfig);
        // 添加缓存
        UploadConfigVO uploadConfigVO = new UploadConfigVO();
        BeanUtils.copyProperties(uploadConfig, uploadConfigVO);
        UploadConfig.refreshCache(redisHelper, uploadConfig.getTenantId(), uploadConfig.getBucketName(),
                uploadConfig.getDirectory(), uploadConfigVO);
        return uploadConfig;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UploadConfig updateUploadConfig(UploadConfig uploadConfig) {
        uploadConfigRepository.updateOptional(uploadConfig, UploadConfig.FIELD_CONTENT_TYPE,
                UploadConfig.FIELD_STORAGE_UNIT, UploadConfig.FIELD_STORAGE_SIZE,
                UploadConfig.FIELD_FILE_FORMAT, UploadConfig.MULTIPLE_FILE_FLAG);
        // 更新缓存
        UploadConfigVO uploadConfigVO = new UploadConfigVO();
        BeanUtils.copyProperties(uploadConfig, uploadConfigVO);
        UploadConfig.refreshCache(redisHelper, uploadConfig.getTenantId(), uploadConfig.getBucketName(),
                uploadConfig.getDirectory(), uploadConfigVO);
        return uploadConfig;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUploadConfig(Long uploadConfigId) {
        UploadConfig uploadConfig = uploadConfigRepository.selectByPrimaryKey(uploadConfigId);
        uploadConfigRepository.deleteByPrimaryKey(uploadConfigId);
        // 清除缓存
        UploadConfig.clearRedisCache(redisHelper, uploadConfig.getTenantId(), uploadConfig.getBucketName(),
                uploadConfig.getDirectory());
    }

    @Override
    public void validateFileSize(File file, String fileCode) {
        Long tenantId = file.getTenantId();
        String bucketName = file.getBucketName();
        String directory = file.getDirectory();
        UploadConfig uploadConfig = UploadConfig.getCache(redisHelper, tenantId, bucketName, directory);
        if (uploadConfig == null) {
            uploadConfig = uploadConfigRepository.selectByTenantId(bucketName, directory, tenantId);
            // 写入缓存
            if (uploadConfig != null) {
                UploadConfigVO configVO = new UploadConfigVO();
                BeanUtils.copyProperties(uploadConfig, configVO);
                UploadConfig.refreshCache(redisHelper, tenantId, bucketName, directory, configVO);
            }
        }

        Long fileSize = file.getFileSize();
        String[] str = file.getFileName().split("\\.");
        Assert.isTrue(str.length > 0, BaseConstants.ErrorCode.DATA_INVALID);
        String suffix = str[str.length - BaseConstants.Digital.ONE].toLowerCase();
        // 详细配置未定义
        if (uploadConfig == null) {
            this.noUploadConfig(fileSize, bucketName, directory, suffix);
        } else {
            if (str.length <= BaseConstants.Digital.ONE) {
                // 未获取到文件名后缀
                throw new CommonException(HfleMessageConstant.ERROR_LOAD_FILE_TYPE);
            } else {
                // 检查文件头与文件类型是否匹配
                FileHeaderUtils.checkFileType(fileCode, suffix);
                // 校验配置
                this.hasUploadConfig(uploadConfig, suffix, fileSize);
            }
        }
        // 检查租户剩余容量
        checkResidualCapacity(file.getTenantId());
    }

    /**
     * 详细配置未定义
     *
     * @param fileSize   文件大小
     * @param bucketName 桶
     * @param directory  目录
     * @param suffix     文件后缀名
     */
    private void noUploadConfig(Long fileSize, String bucketName, String directory, String suffix) {
        // 获取平台的文件目录配置
        UploadConfig uploadConfig =
                UploadConfig.getCache(redisHelper, BaseConstants.DEFAULT_TENANT_ID, bucketName, directory);
        if (uploadConfig == null) {
            // 查询数据库并写入缓存
            uploadConfig = uploadConfigRepository.selectByTenantId(bucketName, directory,
                    BaseConstants.DEFAULT_TENANT_ID);
            if (uploadConfig != null) {
                UploadConfigVO configVO = new UploadConfigVO();
                BeanUtils.copyProperties(uploadConfig, configVO);
                UploadConfig.refreshCache(redisHelper, BaseConstants.DEFAULT_TENANT_ID, bucketName, directory,
                        configVO);
            }
        }
        if (uploadConfig != null) {
            String fileFormat = uploadConfig.getFileFormat();
            if (StringUtils.isBlank(fileFormat)) {
                Long size = uploadConfig.getStorageSize();
                String unit = uploadConfig.getStorageUnit();
                // 判断并抛出
                this.checkFileSize(unit, fileSize, size);
            } else if (StringUtils.isNotBlank(fileFormat) && fileFormat.contains(suffix)) {
                Long size = uploadConfig.getStorageSize();
                String unit = uploadConfig.getStorageUnit();
                // 判断并抛出
                this.checkFileSize(unit, fileSize, size);
            } else {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE);
            }
        }
    }

    /**
     * 定义了详细配置
     *
     * @param uploadConfig 文件上传配置
     * @param suffix       文件名后缀
     * @param fileSize     文件大小
     */
    private void hasUploadConfig(UploadConfig uploadConfig, String suffix, Long fileSize) {
        if (StringUtils.isBlank(uploadConfig.getFileFormat())) {
            // 文件格式为空
            Long size = uploadConfig.getStorageSize();
            String unit = uploadConfig.getStorageUnit();
            // 判断并抛出
            this.checkFileSize(unit, fileSize, size);
        } else {
            // 指定了文件格式
            String[] fileFormat = uploadConfig.getFileFormat().split(",");
            if (Arrays.asList(fileFormat).contains(suffix)) {
                // 定义了该后缀
                Long size = uploadConfig.getStorageSize();
                String unit = uploadConfig.getStorageUnit();
                // 判断并抛出
                this.checkFileSize(unit, fileSize, size);
            } else {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_FORMAT_NOT_SITE);
            }
        }
    }

    /**
     * @param unit     文件上传配置中的单位
     * @param fileSize 实际文件大小
     * @param size     文件上传配置中的文件大小
     */
    private void checkFileSize(String unit, Long fileSize, Long size) {
        if (HfleConstant.StorageUnit.MB.equals(unit)
                && fileSize > size * HfleConstant.ENTERING * HfleConstant.ENTERING) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, size + unit);
        } else if (HfleConstant.StorageUnit.KB.equals(unit) && fileSize > size * HfleConstant.ENTERING) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, size + unit);
        }
    }

    @Override
    public void validateByteFileSize(File file) {
        Long tenantId = file.getTenantId();
        Long fileSize = file.getFileSize();
        CapacityConfig capacityConfig = capacityConfigService.selectByTenantId(tenantId);
        if (capacityConfig == null) {
            // 获取平台配置
            capacityConfig = capacityConfigService.selectByTenantId(BaseConstants.DEFAULT_TENANT_ID);
            if (capacityConfig == null) {
                // 平台未配置，不校验
                return;
            }
        }
        Long size = capacityConfig.getStorageSize();
        String unit = capacityConfig.getStorageUnit();
        this.checkFileSize(unit, fileSize, size);
        checkResidualCapacity(file.getTenantId());
    }

    /**
     * 检查租户可用容量剩余
     *
     * @param tenantId 租户Id
     */
    private void checkResidualCapacity(Long tenantId) {
        // 检查租户可用容量剩余
        Long used = capacityUsedService.getCache(tenantId);
        if (used == null) {
            return;
        }
        CapacityConfig capacity = CapacityConfig.getCache(redisHelper, tenantId);
        if (capacity == null) {
            capacity = capacityConfigService.selectByTenantId(tenantId);
            // 数据库未配置该租户数据, 对比平台默认容量配置
            if (capacity == null) {
                capacity = capacityConfigService.selectByTenantId(BaseConstants.DEFAULT_TENANT_ID);
                if (capacity == null) {
                    return;
                }
            }
        }
        Long totalCapacity = capacity.getTotalCapacity();
        String unit = capacity.getTotalCapacityUnit();
        if (HfleConstant.StorageUnit.MB.equals(unit)) {
            totalCapacity = totalCapacity * HfleConstant.ENTERING * HfleConstant.ENTERING;
        } else if (HfleConstant.StorageUnit.KB.equals(unit)) {
            totalCapacity = totalCapacity * HfleConstant.ENTERING;
        }
        if (used > totalCapacity) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_CAPACITY);
        }
    }
}
