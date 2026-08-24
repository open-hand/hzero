package org.hzero.imported.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.imported.domain.entity.TemplateTarget;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 导入目标Mapper
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
public interface TemplateTargetMapper extends BaseMapper<TemplateTarget> {

    /**
     * 查询模板目标
     *
     * @param headerId 头ID
     * @return 模板目标
     */
    List<TemplateTarget> listTemplateTarget(long headerId);

    /**
     * 删除模板数据目标
     *
     * @param templateId 模板ID
     * @return 删除数量
     */
    int deleteByHeaderId(@Param("templateId")Long templateId);
}

