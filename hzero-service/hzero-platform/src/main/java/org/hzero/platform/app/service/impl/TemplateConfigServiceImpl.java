package org.hzero.platform.app.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.TemplateConfigService;
import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.repository.TemplateAssignRepository;
import org.hzero.platform.domain.repository.TemplateConfigRepository;
import org.hzero.platform.domain.service.TemplateConfigDomainService;
import org.hzero.platform.domain.vo.TemplateConfigVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 模板配置应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-06-28 10:26:29
 */
@Service
public class TemplateConfigServiceImpl implements TemplateConfigService {

    @Autowired
    private TemplateConfigRepository configRepository;
    @Autowired
    private TemplateConfigDomainService configDomainService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateConfig createTemplateConfig(TemplateConfig templateConfig) {
        // 数据不存在，即可新增配置信息
        configRepository.insertSelective(templateConfig);
        // 新建配置生成缓存
        configDomainService.createTemplateConfigCache(templateConfig);
        return templateConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateConfig updateTemplateConfig(TemplateConfig templateConfig) {
        TemplateConfigVO removeCacheVO = new TemplateConfigVO();
        // 获取历史数据
        TemplateConfig configDB = configRepository.selectTemplateConfigDetails(templateConfig.getConfigId());
        Assert.notNull(configDB, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 删除历史缓存所需VO
        BeanUtils.copyProperties(configDB, removeCacheVO);
        configRepository.updateOptional(templateConfig, TemplateConfig.FIELD_CONFIG_TYPE_CODE,
                        TemplateConfig.FIELD_CONFIG_VALUE, TemplateConfig.FIELD_REMARK, TemplateConfig.FIELD_ORDER_SEQ,
                        TemplateConfig.FIELD_LINK);
        configDomainService.updateTemplateConfigCache(templateConfig, removeCacheVO);
        return templateConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTemplateConfig(List<TemplateConfig> templateConfigs) {
        if (CollectionUtils.isEmpty(templateConfigs)) {
            throw new CommonException(BaseConstants.ErrorCode.NOT_NULL);
        }
        List<String> defaultTplCacheKeys = new LinkedList<>();
        List<String> cacheKeys = new LinkedList<>();
        configDomainService.deleteTemplateConfigCache(templateConfigs, cacheKeys, defaultTplCacheKeys);
        configRepository.batchDeleteByPrimaryKey(templateConfigs);
        // 删除缓存
        cacheKeys.forEach(key -> configRepository.clearCache(key));
        if (CollectionUtils.isNotEmpty(defaultTplCacheKeys)) {
            defaultTplCacheKeys.forEach(key -> configRepository.clearCache(key));
        }
    }
}
