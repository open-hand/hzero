package org.hzero.report.app.service;

import java.util.List;

import org.hzero.report.domain.entity.TemplateDtl;

/**
 * 报表模板明细应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-12-05 08:58:44
 */
public interface TemplateDtlService {

    /**
     * 删除模板明细
     *
     * @param templateDtls 模板明细
     */
    void deleteTemplateDtl(List<TemplateDtl> templateDtls);

    /**
     * 插入模板明细
     *
     * @param templateDtl 模板明细
     */
    void insertTemplateDtl(TemplateDtl templateDtl);

    /**
     * 更新模板明细
     *
     * @param templateDtl 模板明细
     */
    void updateTemplateDtl(TemplateDtl templateDtl);

}
