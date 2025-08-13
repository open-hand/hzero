package org.hzero.imported.domain.repository;

import java.util.List;

import org.hzero.imported.domain.entity.TemplateTarget;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 模板头配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/29 11:04
 */
public interface TemplateTargetRepository extends BaseRepository<TemplateTarget> {
    /**
     * 查询模板目标
     *
     * @param headerId 头ID
     * @return 模板目标
     */
    List<TemplateTarget> listTemplateTarget(long headerId);

    /**
     * 删除模板数据目标
     *
     * @param templateId 模板ID
     * @return 删除数量
     */
    int deleteByHeaderId(Long templateId);
}
