package org.hzero.imported.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.imported.domain.entity.TemplateHeader;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author xiaowei.zhang@hand-china.com
 * @date 2018/5/30
 * <p>
 * changeBy shuangfei.zhu@hand-china.com
 */
public interface TemplateHeaderMapper extends BaseMapper<TemplateHeader> {
    /**
     * 查询模板头列表
     *
     * @param templateCode 模板编码
     * @param templateName 模板名称
     * @param tenantId     租户ID
     * @return List<TemplateHeader>
     */
    List<TemplateHeader> selectTemplateHeaderList(@Param("templateCode") String templateCode,
                                                  @Param("templateName") String templateName,
                                                  @Param("tenantId") Long tenantId);

    /**
     * 根据模板ID查询头
     *
     * @param templateId 模板ID
     * @param tenantId   租户ID
     * @return TemplateHeader
     */
    TemplateHeader selectHeaderByTemplateId(@Param("templateId") Long templateId,
                                            @Param("tenantId") Long tenantId);

    /**
     * 根据目标Id查询
     *
     * @param targetId 模板id
     * @return TemplateHeader
     */
    TemplateHeader selectByTargetId(Long targetId);
}
