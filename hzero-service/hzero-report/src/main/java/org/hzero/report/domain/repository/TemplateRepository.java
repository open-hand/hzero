package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.Template;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
public interface TemplateRepository extends BaseRepository<Template> {

    /**
     * 获取模板列表
     *
     * @param pageRequest 分页
     * @param template    模板参数
     * @return 分页数据
     */
    Page<Template> selectTemplates(PageRequest pageRequest, Template template);

    /**
     * 获取模板明细
     *
     * @param templateId 模板Id
     * @return 模板明细
     */
    Template selectTemplate(Long templateId);

    /**
     * 查询被引用数量
     *
     * @param templateId 模板Id
     * @return 数量
     */
    int selectReferenceCount(Long templateId);

}
