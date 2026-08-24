package org.hzero.report.infra.mapper;

import java.util.List;

import org.hzero.report.domain.entity.TemplateDtl;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 报表模板明细Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-12-05 08:58:44
 */
public interface TemplateDtlMapper extends BaseMapper<TemplateDtl> {

    /**
     * 查询报表模板明细
     *
     * @param templateId 模板Id
     * @return 分页
     */
    List<TemplateDtl> selectTemplateDtlsByTemplateId(Long templateId);

}
