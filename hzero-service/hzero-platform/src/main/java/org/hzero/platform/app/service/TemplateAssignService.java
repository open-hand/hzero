package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.TemplateAssign;

import java.util.List;

/**
 * 分配模板应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-03 10:57:26
 */
public interface TemplateAssignService {

    /**
     * 批量分配模板
     *
     * @param templateAssigns 分配模板参数
     * @return List<TemplateAssign>
     */
    List<TemplateAssign> batchCreateTemplateAssigns(List<TemplateAssign> templateAssigns);

    /**
     * 设置默认分配模板
     *
     * @param templateAssignId 默认分配模板Id
     * @return TemplateAssign
     */
    TemplateAssign defaultTemplateAssign(Long templateAssignId);

    /**
     * 批量删除分配模板信息
     *
     * @param templateAssigns  templateAssigns
     */
    void batchDeleteTemplateAssigns(List<TemplateAssign> templateAssigns);

    /**
     * 删除分配模板信息
     *
     * @param templateAssign 删除数据
     */
    void deleteTemplateAssign(TemplateAssign templateAssign);
}
