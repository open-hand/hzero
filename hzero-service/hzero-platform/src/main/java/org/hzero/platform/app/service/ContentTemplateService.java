package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.ContentTemplate;

/**
 * 内容模板管理服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019/06/25 15:44
 */
public interface ContentTemplateService {

    /**
     * 新建模板
     *
     * @param templates 门户模板
     * @return 门户模板
     */
    ContentTemplate insertTemplates(ContentTemplate templates);

    /**
     * 更新模板
     *
     * @param templates 更新参数
     * @return 更新结果
     */
    ContentTemplate updateTemplate(ContentTemplate templates);

    /**
     * 删除模板信息
     *
     * @param templates 删除参数
     */
    void removeTemplate(ContentTemplate templates);

}
