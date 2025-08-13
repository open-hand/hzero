package org.hzero.file.app.service.impl;

import java.util.List;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.domain.entity.CapacityConfig;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.CapacityConfigRepository;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.vo.CapacityConfigVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 租户剩余容量缓存控制服务实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/27 9:32
 */
@Service
public class CapacityUsedServiceImpl implements CapacityUsedService {

    private final RedisHelper redisHelper;
    private final FileRepository fileRepository;
    private final CapacityConfigRepository configRepository;

    public CapacityUsedServiceImpl(RedisHelper redisHelper,
                                   FileRepository fileRepository,
                                   CapacityConfigRepository configRepository) {
        this.redisHelper = redisHelper;
        this.fileRepository = fileRepository;
        this.configRepository = configRepository;
    }

    @Override
    public String getCacheKey(Long tenantId) {
        return HZeroService.File.CODE + ":capacity-used:" + tenantId;
    }

    @Override
    public void initCache(Long tenantId, Long capacity) {
        clearRedisCache(tenantId);
        redisHelper.strSet(getCacheKey(tenantId), String.valueOf(capacity));
    }

    @Override
    public Long refreshCache(Long tenantId, Long fileSize) {
        return redisHelper.strIncrement(getCacheKey(tenantId), fileSize);
    }

    @Override
    public Long getCache(Long tenantId) {
        String result = redisHelper.strGet(getCacheKey(tenantId));
        if (result != null) {
            return Long.valueOf(result);
        }
        return null;
    }

    @Override
    public void clearRedisCache(Long tenantId) {
        redisHelper.delKey(getCacheKey(tenantId));
    }

    @Override
    public void initUsedRedis() {
        List<File> list = fileRepository.sumFileSizeByTenantId();
        for (File file : list) {
            Long fileSize = file.getFileSize();
            Long tenantId = file.getTenantId();
            // 初始化缓存
            this.initCache(tenantId, fileSize);
            CapacityConfig config = configRepository.selectCapacityConfig(tenantId);
            if (config != null) {
                config.setUsedCapacity(fileSize);
                configRepository.updateOptional(config, CapacityConfig.FIELD_USED_CAPACITY);
                // 添加缓存
                CapacityConfigVO capacityConfigVO = new CapacityConfigVO();
                BeanUtils.copyProperties(config, capacityConfigVO);
                CapacityConfig.refreshCache(redisHelper, config.getTenantId(), capacityConfigVO);
            }
        }
    }
}
