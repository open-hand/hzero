package org.hzero.platform.domain.service;

import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.vo.TemplateConfigVO;

import java.util.List;

/**
 * 模板配置领域服务接口
 *
 * @author xiaoyu.zhao@hand-china.com 2019/08/08 17:02
 */
public interface TemplateConfigDomainService {
    /**
     * 新建模板配置时处理模板配置缓存
     */
    void createTemplateConfigCache(TemplateConfig templateConfig);

    /**
     * 更新模板配置时处理模板配置缓存
     */
    void updateTemplateConfigCache(TemplateConfig templateConfig, TemplateConfigVO removeCacheVO);

    /**
     * 批量删除模板配置时处理模板配置缓存
     */
    void deleteTemplateConfigCache(List<TemplateConfig> templateConfigs, List<String> cacheKeys, List<String> defaultTplCacheKeys);

}
