package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.domain.entity.DataHierarchy;

import java.util.List;

/**
 * 数据层级配置持久层
 *
 * @author qingsheng.chen@hand-china.com
 */
public interface DataHierarchyRepository extends BaseRepository<DataHierarchy> {
    /**
     * 查询数据层级配置树形列表
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级编码
     * @param dataHierarchyName 数据层级名称
     * @param enabledFlag       是否启用标记
     * @return 数据层级配置树形列表
     */
    List<DataHierarchyDTO> listDataHierarchy(Long tenantId, String dataHierarchyCode, String dataHierarchyName, Integer enabledFlag);

    /**
     * 查询数据层级配置明细
     *
     * @param tenantId        租户ID
     * @param dataHierarchyId 数据层级ID
     * @return 数据层级配置
     */
    DataHierarchyDTO getDataHierarchy(Long tenantId, Long dataHierarchyId);

    /**
     * 查询数据层级配置明细
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级配置编码
     * @return 数据层级配置
     */
    DataHierarchyDTO getDataHierarchy(Long tenantId, String dataHierarchyCode);

    /**
     * 查询数据层级配置子节点列表
     *
     * @param levelPath 层级路径
     * @return 数据层级配置子节点
     */
    List<DataHierarchy> listDataHierarchyChildrenByLevelPath(long tenantId, String levelPath);

    /**
     * 查询数据层级配置子节点列表
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级编码
     * @return 数据层级配置子节点
     */
    List<DataHierarchy> listDataHierarchyChildren(long tenantId, String dataHierarchyCode);

    /**
     * 刷新缓存
     *
     * @param tenantId          租户ID
     * @param dataHierarchyList 数据层级列表
     */
    void refreshCache(long tenantId, List<DataHierarchyDTO> dataHierarchyList);
}
