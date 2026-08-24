package org.hzero.platform.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.api.dto.commontemplate.*;

import java.util.Map;

/**
 * 通用模板应用服务
 *
 * @author bo.he02@hand-china.com 2020-08-04 09:49:14
 */
public interface CommonTemplateService {
    /**
     * 按照条件分页查询通用模板数据
     *
     * @param queryDTO    查询条件对象
     * @param pageRequest 分页对象
     * @return 查询结果分页对象
     */
    Page<CommonTemplateDTO> list(CommonTemplateQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询通用模板的详细信息
     *
     * @param organizationId 租户ID
     * @param templateId     通用模板主键
     * @return 通用模板详细信息
     */
    CommonTemplateDTO detail(Long organizationId, Long templateId);

    /**
     * 创建通用模板
     *
     * @param creationDTO 通用模板创建参数对象
     * @return 创建后的通用模板信息对象
     */
    CommonTemplateDTO creation(CommonTemplateCreationDTO creationDTO);

    /**
     * 更新通用模板信息
     *
     * @param organizationId 租户ID
     * @param templateId     通用模板ID
     * @param updateDTO      通用模板更新参数对象
     * @return 更新后的通用模板信息对象
     */
    CommonTemplateDTO update(Long organizationId, Long templateId, CommonTemplateUpdateDTO updateDTO);

    /**
     * 渲染模板
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         模板语言
     * @param args         模板参数
     * @return 渲染结果
     */
    RenderResult render(Long tenantId, String templateCode, String lang, Map<String, Object> args);
}
