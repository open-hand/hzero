package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.vo.TemplateConfigCacheVO;
import org.hzero.platform.domain.vo.TemplateConfigVO;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 模板配置Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-06-28 10:26:29
 */
public interface TemplateConfigMapper extends BaseMapper<TemplateConfig> {

    /**
     * 查询模板配置信息列表
     * @param templateConfig  模板配置查询条件
     * @return 结果集
     */
    List<TemplateConfig> selectTemplateConfigs(@Param("templateConfig") TemplateConfig templateConfig);

    /**
     * 查询模板配置信息列表
     * @param templateAssignId  模板分配Id
     * @return 结果集
     */
    List<TemplateConfigVO> selectTemplateConfigVOs(@Param("templateAssignId") Long templateAssignId);

    /**
     * 查询模板配置详细信息
     *
     * @param configId 模板配置Id
     * @return 明细信息
     */
    TemplateConfig selectTemplateConfigDetails(Long configId);

    /**
     * 查询获取缓存配置项
     *
     * @param templateConfig 查询条件
     * @return 缓存所需Key
     */
    TemplateConfigCacheVO selectTemplateConfigWithCacheData(TemplateConfigVO templateConfig);
}
