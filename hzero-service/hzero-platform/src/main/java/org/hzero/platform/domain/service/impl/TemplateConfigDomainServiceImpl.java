package org.hzero.platform.domain.service.impl;

import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.repository.TemplateAssignRepository;
import org.hzero.platform.domain.repository.TemplateConfigRepository;
import org.hzero.platform.domain.service.TemplateConfigDomainService;
import org.hzero.platform.domain.vo.TemplateConfigVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 模板配置领域服务实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2019/08/08 17:03
 */
@Component
public class TemplateConfigDomainServiceImpl implements TemplateConfigDomainService {
    @Autowired
    private TemplateConfigRepository configRepository;
    @Autowired
    private TemplateAssignRepository assignRepository;

    @Override
    public void createTemplateConfigCache(TemplateConfig templateConfig) {
        TemplateConfigVO cacheVO = new TemplateConfigVO();
        BeanUtils.copyProperties(templateConfig, cacheVO);
        // 添加缓存
        configRepository.addOrUpdateTemplateConfigCache(cacheVO, null);
        boolean flag = assignRepository.checkDefaultTpl(templateConfig.getTemplateAssignId());
        if (flag) {
            // 是默认模板下的配置，需要将配置信息也添加到默认模板配置缓存中
            configRepository.generateDefaultTplCache(templateConfig);
        }
    }

    @Override
    public void updateTemplateConfigCache(TemplateConfig templateConfig, TemplateConfigVO removeCacheVO) {
        TemplateConfigVO createCacheVO = new TemplateConfigVO();
        // 生成新缓存数据所需VO
        BeanUtils.copyProperties(templateConfig, createCacheVO);
        // 更新缓存
        configRepository.addOrUpdateTemplateConfigCache(createCacheVO, removeCacheVO);
        boolean flag = assignRepository.checkDefaultTpl(templateConfig.getTemplateAssignId());
        if (flag) {
            // 更新默认缓存，先清除默认缓存，再新增
            configRepository.clearCache(configRepository.generateDefaultTplCacheKey(removeCacheVO));
            configRepository.generateDefaultTplCache(templateConfig.getTemplateAssignId());
        }
    }

    @Override
    public void deleteTemplateConfigCache(List<TemplateConfig> templateConfigs, List<String> cacheKeys, List<String> defaultTplCacheKeys) {
        // 判断是否是默认模板，默认模板需要生成默认模板缓存Key集合
        boolean flag = assignRepository.checkDefaultTpl(templateConfigs.get(0).getTemplateAssignId());
        // 生成缓存Key集合
        if (flag) {
            // 是默认模板下的配置，删除时需要删除默认模板缓存
            templateConfigs.forEach(templateConfig -> {
                // 获取缓存Key，防止数据删除后无法获取redisKey所需参数
                TemplateConfigVO templateConfigVO = templateConfig.convertConfigToVO();
                String cacheKey = configRepository.generateCacheKey(templateConfigVO);
                cacheKeys.add(cacheKey);
                String defaultTplCacheKey = configRepository.generateDefaultTplCacheKey(templateConfigVO);
                defaultTplCacheKeys.add(defaultTplCacheKey);
            });
        } else {
            // 不是默认模板下的配置，直接删除模板的配置及缓存即可
            templateConfigs.forEach(templateConfig -> {
                TemplateConfigVO templateConfigVO = templateConfig.convertConfigToVO();
                String cacheKey = configRepository.generateCacheKey(templateConfigVO);
                cacheKeys.add(cacheKey);
            });
        }
    }
}
