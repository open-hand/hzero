package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.TemplateDtl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表模板明细资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-12-05 08:58:44
 */
public interface TemplateDtlRepository extends BaseRepository<TemplateDtl> {

    /**
     * 查询模板明细
     *
     * @param pageRequest 分页
     * @param templateId  模板Id
     * @return 分页数据
     */
    Page<TemplateDtl> selectTemplateDtlsByTemplateId(PageRequest pageRequest, Long templateId);

    /**
     * 查询报表模板明细
     *
     * @param templateDtlId 模板明细Id
     * @return 模板明细
     */
    TemplateDtl selectTemplateDtl(Long templateDtlId);

}
