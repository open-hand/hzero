package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.TemplateConfig;

import java.util.List;

/**
 * 模板配置应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-06-28 10:26:29
 */
public interface TemplateConfigService {

    /**
     * 创建模板配置信息
     *
     * @param templateConfig 新建数据
     * @return TemplateConfig
     */
    TemplateConfig createTemplateConfig(TemplateConfig templateConfig);

    /**
     * 更新模板配置信息
     *
     * @param templateConfig  更新数据
     * @return TemplateConfig
     */
    TemplateConfig updateTemplateConfig(TemplateConfig templateConfig);

    /**
     * 删除模板配置
     *
     * @param templateConfigs 删除数据
     */
    void removeTemplateConfig(List<TemplateConfig> templateConfigs);
}
