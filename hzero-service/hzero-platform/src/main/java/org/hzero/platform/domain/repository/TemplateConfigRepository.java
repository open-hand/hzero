package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.vo.TemplateConfigVO;

import java.util.List;

/**
 * 模板配置资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-06-28 10:26:29
 */
public interface TemplateConfigRepository extends BaseRepository<TemplateConfig> {

    /**
     * 分页查询模板配置信息列表
     * 
     * @param templateConfig 查询条件
     * @param pageRequest    分页参数
     * @return Page<TemplateConfig>
     */
    Page<TemplateConfig> selectTemplateConfigs(TemplateConfig templateConfig, PageRequest pageRequest);

    /**
     * 查询模板配置详细信息
     * 
     * @param configId 模板配置Id
     * @return 明细
     */
    TemplateConfig selectTemplateConfigDetails(Long configId);

    /**
     * 缓存模板配置信息
     *
     * @param createCacheVO 创建或更新缓存数据所需VO
     * @param removeCacheVO 删除旧缓存所需VO
     */
    void addOrUpdateTemplateConfigCache(TemplateConfigVO createCacheVO, TemplateConfigVO removeCacheVO);

    /**
     * 清除模板配置缓存信息
     *
     * @param cacheKey 需要清除缓存的Key
     */
    void clearCache(String cacheKey);

    /**
     * 生成缓存key
     *
     * @param templateConfig 查询参数
     * @return 缓存Key
     */
    String generateCacheKey(TemplateConfigVO templateConfig);

    /**
     * 删除分配模板时 关联删除模板配置信息及缓存信息
     *
     * @param templateConfig 模板配置
     */
    void removeTemplateConfigsWithCache(TemplateConfig templateConfig);

    /**
     * 生成默认模板缓存
     *
     * @param templateAssignId 模板分配id
     */
    void generateDefaultTplCache(Long templateAssignId);

    /**
     * 生成默认模板缓存
     *
     * @param templateConfig 模板配置
     */
    void generateDefaultTplCache(TemplateConfig templateConfig);

    /**
     * 清除默认模板缓存
     *
     * @param templateAssignId 模板分配Id
     */
    void clearDefaultTplCache(Long templateAssignId);

    /**
     * 获取默认模板缓存Key
     *
     * @param templateConfigVO 默认模板
     * @return 默认模板缓存Key
     */
    String generateDefaultTplCacheKey(TemplateConfigVO templateConfigVO);
}
