package org.hzero.file.app.service.impl;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.app.service.CapacityConfigService;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.domain.entity.CapacityConfig;
import org.hzero.file.domain.entity.UploadConfig;
import org.hzero.file.domain.repository.CapacityConfigRepository;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.domain.vo.CapacityConfigVO;
import org.hzero.file.infra.config.MaxSizeConfig;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 文件容量配置应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
@Service
public class CapacityConfigServiceImpl implements CapacityConfigService {

    private final CapacityUsedService capacityUsedService;
    private final CapacityConfigRepository capacityConfigRepository;
    private final UploadConfigRepository uploadConfigRepository;
    private final RedisHelper redisHelper;
    private final LovAdapter lovAdapter;
    private final MaxSizeConfig maxSizeConfig;

    @Autowired
    public CapacityConfigServiceImpl(CapacityUsedService capacityUsedService,
                                     CapacityConfigRepository capacityConfigRepository,
                                     UploadConfigRepository uploadConfigRepository,
                                     RedisHelper redisHelper,
                                     LovAdapter lovAdapter,
                                     MaxSizeConfig maxSizeConfig) {
        this.capacityUsedService = capacityUsedService;
        this.capacityConfigRepository = capacityConfigRepository;
        this.uploadConfigRepository = uploadConfigRepository;
        this.redisHelper = redisHelper;
        this.lovAdapter = lovAdapter;
        this.maxSizeConfig = maxSizeConfig;
    }

    @Override
    public CapacityConfig pageUploadConfig(Long tenantId, PageRequest pageRequest) {
        // 手动翻译contentType
        Page<UploadConfig> page = this.contentTypeLOV(uploadConfigRepository.pageUploadConfig(tenantId, pageRequest), tenantId);
        CapacityConfig capacityConfig = capacityConfigRepository.selectCapacityConfig(tenantId);
        if (capacityConfig == null) {
            capacityConfig = capacityConfigRepository.selectCapacityConfig(BaseConstants.DEFAULT_TENANT_ID);
            if (capacityConfig == null) {
                capacityConfig = new CapacityConfig();
            } else {
                capacityConfigRepository.insertSelective(capacityConfig.setTenantId(tenantId).setCapacityConfigId(null).setUsedCapacity(null));
                capacityConfig = capacityConfigRepository.selectCapacityConfig(tenantId);
            }
        }
        capacityConfig.setListConfig(page);
        // 从缓存获取已使用容量
        if (capacityConfig.getCapacityConfigId() != null) {
            Long used = Optional.ofNullable(capacityUsedService.getCache(tenantId)).orElse((long) BaseConstants.Digital.ZERO);
            capacityConfig.setRedisUsedCapacity(used);
        }
        return capacityConfig;
    }

    /**
     * 翻译contentType
     *
     * @param page     分页
     * @param tenantId 租户Id
     * @return 分页
     */
    private Page<UploadConfig> contentTypeLOV(Page<UploadConfig> page, Long tenantId) {
        List<LovValueDTO> list = lovAdapter.queryLovValue(HfleConstant.Lov.CONTENT_TYPE, tenantId);
        Map<String, String> lov = new HashMap<>(BaseConstants.Digital.ONE);
        list.forEach(dto -> lov.put(dto.getValue(), dto.getMeaning()));

        for (UploadConfig uploadConfig : page) {
            StringBuilder meanings = new StringBuilder();
            if (StringUtils.isNotBlank(uploadConfig.getContentType())) {
                String[] contentTypes = uploadConfig.getContentType().split(",");
                for (String type : contentTypes) {
                    if (StringUtils.isNotBlank(lov.get(type))) {
                        meanings.append(",").append(lov.get(type));
                    }
                }
                if (StringUtils.isNotBlank(meanings)) {
                    uploadConfig.setContentTypeMeaning(meanings.substring(BaseConstants.Digital.ONE, meanings.length()));
                }
            }
        }
        return page;
    }

    @Override
    public CapacityConfig selectByTenantId(Long tenantId) {
        CapacityConfig capacityConfig = CapacityConfig.getCache(redisHelper, tenantId);
        if (capacityConfig == null) {
            capacityConfig = capacityConfigRepository.selectCapacityConfig(tenantId);
            // 写入缓存
            if (capacityConfig != null) {
                CapacityConfigVO configVO = new CapacityConfigVO();
                BeanUtils.copyProperties(capacityConfig, configVO);
                CapacityConfig.refreshCache(redisHelper, tenantId, configVO);
            }
        }
        return capacityConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CapacityConfig createOrUpdate(CapacityConfig capacityConfig) {
        String maxCapacity = StringUtils.EMPTY;
        CapacityConfig defaultConfig = null;
        if (!Objects.equals(capacityConfig.getTenantId(), BaseConstants.DEFAULT_TENANT_ID)) {
            defaultConfig = capacityConfigRepository.selectOne(new CapacityConfig().setTenantId(BaseConstants.DEFAULT_TENANT_ID));
        }
        if (defaultConfig != null) {
            maxCapacity = defaultConfig.getTotalCapacity() + defaultConfig.getTotalCapacityUnit();
        }
        if (capacityConfigRepository.selectCount(new CapacityConfig().setTenantId(capacityConfig.getTenantId())) == BaseConstants.Digital.ZERO) {
            capacityConfig.validateRepeat(capacityConfigRepository);
            capacityConfig.validateSize(maxSizeConfig.getMaxFileSize(), maxCapacity);
            return createCapacityConfig(capacityConfig);
        } else {
            SecurityTokenHelper.validToken(capacityConfig);
            capacityConfig.validateSize(maxSizeConfig.getMaxFileSize(), maxCapacity);
            capacityConfig.validateSize(uploadConfigRepository);
            return updateCapacityConfig(capacityConfig);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CapacityConfig createCapacityConfig(CapacityConfig capacityConfig) {
        capacityConfigRepository.insertSelective(capacityConfig);
        // 添加缓存
        CapacityConfigVO capacityConfigVO = new CapacityConfigVO();
        BeanUtils.copyProperties(capacityConfig, capacityConfigVO);
        CapacityConfig.refreshCache(redisHelper, capacityConfig.getTenantId(), capacityConfigVO);
        // 初始化租户已使用文件容量
        capacityUsedService.initCache(capacityConfig.getTenantId(), (long) BaseConstants.Digital.ZERO);
        return capacityConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CapacityConfig updateCapacityConfig(CapacityConfig capacityConfig) {
        capacityConfigRepository.updateOptional(capacityConfig,
                CapacityConfig.FIELD_TOTAL_CAPACITY,
                CapacityConfig.FIELD_TOTAL_CAPACITY_UNIT,
                CapacityConfig.FIELD_STORAGE_UNIT,
                CapacityConfig.FIELD_STORAGE_SIZE);
        // 添加缓存
        CapacityConfigVO capacityConfigVO = new CapacityConfigVO();
        BeanUtils.copyProperties(capacityConfig, capacityConfigVO);
        CapacityConfig.refreshCache(redisHelper, capacityConfig.getTenantId(), capacityConfigVO);
        return capacityConfig;
    }
}
