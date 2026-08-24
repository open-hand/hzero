package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.TemplateAssignDTO;
import org.hzero.platform.domain.entity.TemplateAssign;

import java.util.List;

/**
 * 分配模板资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-03 10:57:26
 */
public interface TemplateAssignRepository extends BaseRepository<TemplateAssign> {

    /**
     * 查询分配的模板列表
     *
     * @param sourceType 来源类型
     * @param sourceKey  来源编码
     * @param tenantId   租户Id
     * @return 模板列表
     */
    List<TemplateAssignDTO> listTemplateAssigns(String sourceType, String sourceKey, Long tenantId);

    /**
     * 查询模板信息(排除已经分配的模板信息)
     *
     * @param pageRequest  分页参数
     * @param templates    查询条件
     * @return Page<TemplateAssignDTO>
     */
    Page<TemplateAssignDTO> selectAssignableTemplates(PageRequest pageRequest, TemplateAssignDTO templates);

    /**
     * 判断是否是默认模板
     *
     * @param templateAssignId 模板分配Id
     * @return 是/否
     */
    boolean checkDefaultTpl(Long templateAssignId);

    /**
     * 判断是否是默认模板
     *
     * @param sourceKey 来源Key，存储来源数据的id
     * @param sourceType sourceType 来源类型，默认SSO，可自定义
     * @param tenantId 租户Id
     * @return 是/否
     */
    boolean checkDefaultTpl(String sourceKey, String sourceType, Long tenantId);
}
