package org.hzero.report.infra.mapper;

import java.util.List;

import org.hzero.report.domain.entity.Template;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 报表模板Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
public interface TemplateMapper extends BaseMapper<Template> {

    /**
     * 获取模板列表
     *
     * @param template 模板
     * @return 分页
     */
    List<Template> selectTemplates(Template template);

    /**
     * 获取模板明细
     *
     * @param templateId 模板Id
     * @return 模板
     */
    Template selectTemplate(Long templateId);

    /**
     * 获取模板被引用数量
     *
     * @param templateId 模板Id
     * @return 被引用数量
     */
    int selectReferenceCount(Long templateId);

}
