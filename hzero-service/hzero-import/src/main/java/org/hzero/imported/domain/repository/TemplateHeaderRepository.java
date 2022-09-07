package org.hzero.imported.domain.repository;

import java.util.List;

import org.hzero.imported.domain.entity.TemplateHeader;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 模板头配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/29 11:04
 */
public interface TemplateHeaderRepository extends BaseRepository<TemplateHeader> {
    /**
     * 查询模板头列表
     *
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @param tenantId     租户ID
     * @return List<TemplateHeader>
     */
    List<TemplateHeader> selectTemplateHeaderList(String templateCode, String templateName, Long tenantId);

    /**
     * 根据模板ID查询头
     *
     * @param templateId 模板ID
     * @param tenantId   租户ID
     * @return TemplateHeader
     */
    TemplateHeader selectHeaderByTemplateId(Long templateId, Long tenantId);

    /**
     * 根据目标Id查询
     *
     * @param targetId 模板id
     * @return TemplateHeader
     */
    TemplateHeader selectByTargetId(Long targetId);
}
