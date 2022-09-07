package org.hzero.imported.app.service;

import org.hzero.imported.domain.entity.TemplateHeader;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author shuangfei.zhu@hand-china.com 2018-12-7 16:42:53
 */
public interface TemplateHeaderService {
    /**
     * 分页查询
     *
     * @param templateCode 编码
     * @param templateName 名称
     * @param tenantId     租户Id
     * @param pageRequest  分页
     * @return 头
     */
    Page<TemplateHeader> pageTemplateHeader(String templateCode, String templateName, Long tenantId, PageRequest pageRequest);

    /**
     * 通过主键查询模板头表信息
     *
     * @param templateId 头Id
     * @param tenantId   租户Id
     * @return 头
     */
    TemplateHeader detailTemplateHeader(Long templateId, Long tenantId);

    /**
     * 根据模板code及租户查询
     *
     * @param templateCode 模板code
     * @param tenantId     租户Id
     * @return 头
     */
    TemplateHeader getTemplateHeader(String templateCode, Long tenantId);

    /**
     * 创建模板头表信息
     *
     * @param templateHeader header+target
     * @return header+target
     */
    TemplateHeader createTemplateHeader(TemplateHeader templateHeader);

    /**
     * 更新模板头表信息
     *
     * @param templateHeader header+target
     * @return header+target
     */
    TemplateHeader updateTemplateHeader(TemplateHeader templateHeader);

    /**
     * 删除头及对应的目标
     *
     * @param templateId 模板Id
     */
    void deleteTemplateHeader(Long templateId);

    /**
     * 删除目标
     *
     * @param targetId 模板目标Id
     */
    void deleteTemplateTarget(Long targetId);
}
