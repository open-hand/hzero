package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.commontemplate.CommonTemplateDTO;
import org.hzero.platform.api.dto.commontemplate.CommonTemplateQueryDTO;
import org.hzero.platform.domain.entity.CommonTemplate;

import java.util.List;

/**
 * 通用模板Mapper
 *
 * @author bo.he02@hand-china.com 2020-08-04 09:49:14
 */
public interface CommonTemplateMapper extends BaseMapper<CommonTemplate> {
    /**
     * 按照查询条件查询通用模板数据
     *
     * @param queryDTO 查询参数对象
     * @return 查询结果信息
     */
    List<CommonTemplateDTO> list(CommonTemplateQueryDTO queryDTO);

    /**
     * 查询通用模板的数据
     *
     * @param organizationId 租户ID
     * @param templateId     通用模板主键
     * @return 通用模板数据
     */
    CommonTemplateDTO detail(@Param("organizationId") Long organizationId, @Param("templateId") Long templateId);
}
