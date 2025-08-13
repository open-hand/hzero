package org.hzero.platform.infra.repository.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.domain.entity.Config;
import org.hzero.platform.domain.repository.ConfigRepository;
import org.hzero.platform.domain.vo.TitleConfigCacheVO;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.mapper.ConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 系统配置repositoryImpl
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/19 11:52
 */
@Component
public class ConfigRepositoryImpl extends BaseRepositoryImpl<Config> implements ConfigRepository {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private ConfigMapper configMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Config> batchUpdate(List<Config> configList) {
        if (CollectionUtils.isNotEmpty(configList)) {
            configList.forEach(config -> {
                this.updateOptional(config, Config.FIELD_CONFIG_VALUE);
                List<TitleConfigCacheVO> resultMap = configMapper.selectLanguageValueMap(config.getConfigId());
                config.setLanguageValue(resultMap);
                config.refreshCache(redisHelper);
            });
        }
        return configList;
    }

    @Override
    public void initAllConfigToRedis() {
        SecurityTokenHelper.close();
        List<Config> configList = configMapper.selectAll();
        configList.forEach(config -> {
            if (Constants.CONFIG_CODE_TITLE.equals(config.getConfigCode())) {
                List<TitleConfigCacheVO> languageValueMap = configMapper.selectLanguageValueMap(config.getConfigId());
                config.setLanguageValue(languageValueMap);
            }
            config.refreshCache(redisHelper);
        });
        SecurityTokenHelper.clear();
    }

    @Override
    public List<Config> selectConfigByTenantId(Long tenantId) {
        return configMapper.selectConfigByTenantId(tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Config> insertOrUpdateConfig(List<Config> configList, Long tenantId) {
        if (CollectionUtils.isNotEmpty(configList)) {
            configList.forEach(config -> {
                // config值为空，设为空字符串
                if (StringUtils.isBlank(config.getConfigValue())){
                    config.setConfigValue(StringUtils.EMPTY);
                }
                if (config.getObjectVersionNumber() != null) {
                    // 两种情况，一种是传入租户与config中的租户不一致，此时新增，否则更新
                    if (Objects.equals(config.getTenantId(), tenantId)) {
                        // 更新
                        this.updateOptional(config, Config.FIELD_CONFIG_VALUE);
                        List<TitleConfigCacheVO> resultMap = configMapper.selectLanguageValueMap(config.getConfigId());
                        config.setLanguageValue(resultMap);
                        config.refreshCache(redisHelper);
                    } else {
                        // 新增
                        createConfig(tenantId, config);
                    }
                } else {
                    // 新建
                    createConfig(tenantId, config);
                }
            });
        }
        return configList;
    }

    /**
     * 新建配置
     */
    private void createConfig(Long tenantId, Config config) {
        Config tempConfig = new Config();
        tempConfig.setTenantId(tenantId);
        tempConfig.setConfigCode(config.getConfigCode());
        if (this.selectCount(tempConfig) != 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
        config.setConfigId(null);
        config.setTenantId(tenantId);
        this.insertSelective(config);
        List<TitleConfigCacheVO> resultMap = configMapper.selectLanguageValueMap(config.getConfigId());
        config.setLanguageValue(resultMap);
        config.refreshCache(redisHelper);
    }
}
