package org.hzero.file.infra.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.domain.entity.StorageConfig;
import org.hzero.file.domain.repository.StorageConfigRepository;
import org.hzero.file.domain.vo.StorageConfigVO;
import org.hzero.file.infra.config.FileConfig;
import org.hzero.file.infra.constant.FileServiceType;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.mapper.StorageConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 文件存储配置 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-06-29 12:58:25
 */
@Component("storageConfigRepository")
public class StorageConfigRepositoryImpl extends BaseRepositoryImpl<StorageConfig> implements StorageConfigRepository {

    private final LovAdapter lovAdapter;
    private final FileConfig fileConfig;
    private final RedisHelper redisHelper;
    private final StorageConfigMapper storageConfigMapper;

    private Cache<Long, StorageConfig> storageConfigCache;

    @Autowired
    public StorageConfigRepositoryImpl(LovAdapter lovAdapter,
                                       FileConfig fileConfig,
                                       RedisHelper redisHelper,
                                       StorageConfigMapper storageConfigMapper) {
        this.lovAdapter = lovAdapter;
        this.fileConfig = fileConfig;
        this.redisHelper = redisHelper;
        this.storageConfigMapper = storageConfigMapper;
        if (fileConfig.getStoreCache().isEnable()) {
            storageConfigCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(fileConfig.getStoreCache().getTime(), TimeUnit.SECONDS)
                    .softValues()
                    .initialCapacity(128)
                    .build();
        }
    }

    @Override
    public List<StorageConfig> selectByUnique(Long tenantId, String storageCode) {
        return storageConfigMapper.selectByUnique(tenantId, storageCode);
    }

    @Override
    public StorageConfig getStorageConfig(Long tenantId) {
        StorageConfig storageConfig;
        boolean useCache = fileConfig.getStoreCache().isEnable();
        if (useCache) {
            storageConfig = storageConfigCache.getIfPresent(tenantId);
            if (storageConfig != null) {
                return storageConfig;
            }
        }
        storageConfig = new StorageConfig();
        StorageConfig param = new StorageConfig().setTenantId(tenantId).setDefaultFlag(BaseConstants.Flag.YES);
        // 先从缓存中取
        StorageConfigVO cache = param.getDefaultConfigCache(redisHelper);
        if (cache != null) {
            BeanUtils.copyProperties(cache, storageConfig);
        } else {
            // 从数据库取数据
            storageConfig = selectOne(param);
            if (storageConfig != null) {
                // 刷新缓存，返回数据
                param.refreshDefaultConfigCache(redisHelper, storageConfig);
            }
        }
        // 记录二级缓存
        if (useCache && storageConfig != null) {
            storageConfigCache.put(tenantId, storageConfig);
        }
        return storageConfig;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertOrUpdateStorageConfig(StorageConfig storageConfig) {
        // 校验当前对象是否是默认配置，若是默认配置，不可更新为非默认
        StorageConfig defaultConfig = selectOne(new StorageConfig().setTenantId(storageConfig.getTenantId()).setDefaultFlag(BaseConstants.Flag.YES));
        if (defaultConfig != null && Objects.equals(storageConfig.getStorageConfigId(), defaultConfig.getStorageConfigId())) {
            Assert.isTrue(Objects.equals(storageConfig.getDefaultFlag(), BaseConstants.Flag.YES), HfleMessageConstant.DEFAULT_CONFIG);
        }
        // 判断是否需要重置默认值(同一租户只能有一个默认值)
        if (BaseConstants.Flag.YES.equals(storageConfig.getDefaultFlag())) {
            storageConfigMapper.resetDefaultStorageConfig(storageConfig.getTenantId());
        }
        // 判断是否新增或更新
        if (storageConfig.getStorageConfigId() == null) {
            // 唯一性校验
            List<StorageConfig> list = selectByUnique(storageConfig.getTenantId(), storageConfig.getStorageCode());
            Assert.isTrue(CollectionUtils.isEmpty(list), BaseConstants.ErrorCode.DATA_EXISTS);
            // 插入之前开启加密，任何一次数据库操作都会关闭他
            DataSecurityHelper.open();
            insertSelective(storageConfig);
        } else {
            if (StringUtils.isNoneBlank(storageConfig.getAccessKeySecret())) {
                DataSecurityHelper.open();
                updateOptional(storageConfig,
                        StorageConfig.FIELD_STORAGE_TYPE,
                        StorageConfig.FIELD_DOMAIN,
                        StorageConfig.FIELD_END_POINT,
                        StorageConfig.FIELD_ACCESS_KEY_ID,
                        StorageConfig.FIELD_ACCESS_KEY_SECRET,
                        StorageConfig.FIELD_APP_ID,
                        StorageConfig.FIELD_REGION,
                        StorageConfig.FIELD_DEFAULT_FLAG,
                        StorageConfig.FIELD_TENANT_ID,
                        StorageConfig.FIELD_ACCESS_CONTROL,
                        StorageConfig.FIELD_BUCKET_PREFIX,
                        StorageConfig.FIELD_PREFIX_STRATEGY,
                        StorageConfig.FIELD_CREATE_BUCKET_FLAG);
            } else {
                updateOptional(storageConfig,
                        StorageConfig.FIELD_STORAGE_TYPE,
                        StorageConfig.FIELD_DOMAIN,
                        StorageConfig.FIELD_END_POINT,
                        StorageConfig.FIELD_ACCESS_KEY_ID,
                        StorageConfig.FIELD_APP_ID,
                        StorageConfig.FIELD_REGION,
                        StorageConfig.FIELD_DEFAULT_FLAG,
                        StorageConfig.FIELD_TENANT_ID,
                        StorageConfig.FIELD_ACCESS_CONTROL,
                        StorageConfig.FIELD_BUCKET_PREFIX,
                        StorageConfig.FIELD_PREFIX_STRATEGY,
                        StorageConfig.FIELD_CREATE_BUCKET_FLAG);
            }
        }
        // 若是默认配置，刷新缓存
        if (Objects.equals(storageConfig.getDefaultFlag(), BaseConstants.Flag.YES)) {
            storageConfig.refreshDefaultConfigCache(redisHelper, selectByPrimaryKey(storageConfig.getStorageConfigId()));
            if (fileConfig.getStoreCache().isEnable()) {
                storageConfigCache.put(storageConfig.getTenantId(), storageConfig);
            }
        }
    }

    @Override
    public List<StorageConfig> selectStorageConfig(StorageConfig record) {
        List<StorageConfig> storageConfigList = select(record);
        if (record.getStorageType() == FileServiceType.MICROSOFT.getValue()) {
            // 微软手动翻译endpoint
            List<LovValueDTO> valueList = lovAdapter.queryLovValue("HFLE.MICROSOFT.ENDPOINT_SUFFIX", ObjectUtils.defaultIfNull(record.getTenantId(), BaseConstants.DEFAULT_TENANT_ID));
            Map<String, String> map = valueList.stream().collect(Collectors.toMap(LovValueDTO::getValue, LovValueDTO::getMeaning));
            storageConfigList.forEach(item -> {
                item.setAccessKeySecret(null);
                String endPoint = item.getEndPoint();
                if (StringUtils.isNotBlank(endPoint)) {
                    item.setEndPointMeaning(map.getOrDefault(endPoint, endPoint));
                }
            });
        } else {
            storageConfigList.forEach(item -> item.setAccessKeySecret(null));
        }
        return storageConfigList;
    }

    @Override
    public void deleteConfig(StorageConfig config) {
        Long configId = config.getStorageConfigId();
        StorageConfig storageConfig = selectByPrimaryKey(configId);
        if (storageConfig != null && Objects.equals(storageConfig.getDefaultFlag(), BaseConstants.Flag.YES)) {
            throw new CommonException(HfleMessageConstant.DEFAULT_CONFIG);
        }
        deleteByPrimaryKey(configId);
    }
}
