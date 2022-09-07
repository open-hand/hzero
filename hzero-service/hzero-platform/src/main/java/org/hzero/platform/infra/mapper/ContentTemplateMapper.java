package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.ContentTemplate;

import java.util.List;

/**
 * 内容模板Mapper
 *
 * @author yunxiang.zhou01@hand-china.com 2018-08-13 15:37:15
 */
public interface ContentTemplateMapper extends BaseMapper<ContentTemplate> {

    /**
     * 查询模板
     *
     * @param templates 模板条件
     * @return 模板list
     */
    List<ContentTemplate> selectTemplates(ContentTemplate templates);

    /**
     * 使用租户Id和模板编码查询唯一模板
     *
     * @param templateCode 模板编码
     * @param tenantId 租户Id
     * @return 唯一模板
     */
    int selectTemplateByTenantAndCode(@Param("templateCode") String templateCode, @Param("tenantId") Long tenantId);
}
