package org.hzero.platform.app.service;

import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.domain.entity.DataHierarchy;

import java.util.List;

/**
 * 数据层级配置服务接口
 *
 * @author qingsheng.chen@hand-china.com
 */
public interface DataHierarchyService {

    /**
     * 查询数据层级配置列表
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级编码
     * @param dataHierarchyName 数据层级名称
     * @param enabledFlag       是否启用标记
     * @return 数据层级配置列表
     */
    List<DataHierarchyDTO> listDataHierarchy(Long tenantId, String dataHierarchyCode, String dataHierarchyName, Integer enabledFlag);

    /**
     * 查询数据层级配置树形列表
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级编码
     * @param dataHierarchyName 数据层级名称
     * @param enabledFlag       是否启用标记
     * @return 数据层级配置树形列表
     */
    List<DataHierarchyDTO> treeDataHierarchy(Long tenantId, String dataHierarchyCode, String dataHierarchyName, Integer enabledFlag);

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
     * 创建数据层级配置
     *
     * @param dataHierarchy 数据层级配置
     * @return 数据层级配置
     */
    DataHierarchy createDataHierarchy(DataHierarchy dataHierarchy);

    /**
     * 修改数据层级配置
     *
     * @param dataHierarchy 数据层级配置
     * @return 数据层级配置
     */
    DataHierarchy updateDataHierarchy(DataHierarchy dataHierarchy);

    /**
     * 删除数据层级配置
     *
     * @param dataHierarchy 数据层级
     */
    void deleteDataHierarchy(DataHierarchy dataHierarchy);
}

