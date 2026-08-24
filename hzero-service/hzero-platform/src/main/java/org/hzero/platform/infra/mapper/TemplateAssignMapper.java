package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.TemplateAssignDTO;
import org.hzero.platform.domain.entity.TemplateAssign;

import java.util.List;

/**
 * 分配模板Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-03 10:57:26
 */
public interface TemplateAssignMapper extends BaseMapper<TemplateAssign> {

    /**
     * 查询获取分配模板信息
     *
     * @param sourceType 来源类型
     * @param sourceKey 来源key
     * @param tenantId 租户Id
     * @return List<TemplateAssignDTO>
     */
    List<TemplateAssignDTO> selectTemplateAssigns(@Param("sourceType") String sourceType,
                    @Param("sourceKey") String sourceKey, @Param("tenantId") Long tenantId);

    /**
     * 查询排除已经分配的模板列表
     *
     * @param templates 查询条件
     * @return List<TemplateAssignDTO>
     */
    List<TemplateAssignDTO> selectAssignableTemplates(@Param("templates") TemplateAssignDTO templates);
}
