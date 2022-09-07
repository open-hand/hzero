package org.hzero.file.domain.service.factory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.domain.entity.StorageConfig;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.repository.StorageConfigRepository;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.starter.file.entity.StoreConfig;
import org.hzero.starter.file.service.AbstractFileService;
import org.hzero.starter.file.service.StoreCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * 文件服务工厂类
 *
 * @author xianzhi.chen@hand-china.com 2018年6月27日下午8:20:38
 */
@Service
public class StoreFactory {

    private static final Logger logger = LoggerFactory.getLogger(StoreFactory.class);

    private final FileRepository fileRepository;
    private final StorageConfigRepository configRepository;
    private final CapacityUsedService residualCapacityService;

    @Autowired
    public StoreFactory(FileRepository fileRepository,
                        StorageConfigRepository configRepository,
                        CapacityUsedService residualCapacityService) {
        this.configRepository = configRepository;
        this.fileRepository = fileRepository;
        this.residualCapacityService = residualCapacityService;
    }

    public StoreService build(Long tenantId, String storageCode) {
        // 获取云存储配置信息
        StorageConfig storageConfig;
        if (StringUtils.isBlank(storageCode)) {
            storageConfig = configRepository.getStorageConfig(tenantId);
            if (storageConfig == null) {
                storageConfig = configRepository.getStorageConfig(BaseConstants.DEFAULT_TENANT_ID);
            }
            Assert.notNull(storageConfig, HfleMessageConstant.ERROR_FILE_UPDATE);
        } else {
            // 若指定了编码，使用指定编码配置
            List<StorageConfig> configList = configRepository.selectByUnique(tenantId, storageCode);
            if (CollectionUtils.isEmpty(configList)) {
                configList = configRepository.selectByUnique(BaseConstants.DEFAULT_TENANT_ID, storageCode);
            }
            Assert.isTrue(CollectionUtils.isNotEmpty(configList), HfleMessageConstant.ERROR_FILE_UPDATE);
            storageConfig = configList.get(0);
        }
        // 解密AccessKeySecret
        try {
            storageConfig.setAccessKeySecret(DataSecurityHelper.decrypt(storageConfig.getAccessKeySecret()));
        } catch (Exception e) {
            logger.warn("AccessKeySecret decrypt failed!");
        }
        StoreCreator creator = null;
        Map<String, StoreCreator> creatorMap = ApplicationContextHelper.getContext().getBeansOfType(StoreCreator.class);
        for (Map.Entry<String, StoreCreator> entry : creatorMap.entrySet()) {
            if (Objects.equals(entry.getValue().storeType(), storageConfig.getStorageType())) {
                creator = entry.getValue();
                break;
            }
        }
        if (creator == null) {
            throw new CommonException(HfleMessageConstant.CLIENT_INIT);
        }
        StoreConfig config = new StoreConfig();
        BeanUtils.copyProperties(storageConfig, config);
        AbstractFileService abstractFileService = creator.getFileService().init(config);
        return new StoreService(abstractFileService, config, fileRepository, residualCapacityService);
    }
}