package org.hzero.platform.app.service;

import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.api.dto.DataHierarchyDisplayStyleDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据层级配置 切换
 *
 * @author qingsheng.chen@hand-china.com
 */
public interface DataHierarchySwitchService {
    /**
     * 树形查询当前用户的数据层级值
     *
     * @param tenantId 租户ID
     * @return 树形数据层级值
     */
    List<DataHierarchyDTO> treeDataHierarchyValue(long tenantId);

    /**
     * 按照显示风格分组查询数据层级值
     *
     * @param tenantId 租户ID
     * @return 按照显示风格分组查询数据层级值
     */
    DataHierarchyDisplayStyleDTO displayStyleDataHierarchyValue(Long tenantId);

    /**
     * 查询指定数据层级配置值
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级配置编码
     * @return
     */
    DataHierarchyDTO queryDataHierarchyValue(long tenantId, String dataHierarchyCode);

    /**
     * 查询当前用户对应数据层级编码的数据层级值
     *
     * @param dataHierarchyCodeList 数据层级编码列表
     * @return 对应数据层级编码的数据层级值
     */
    Map<String, Object> queryDataHierarchyValue(Set<String> dataHierarchyCodeList);

    /**
     * 切换当前用户对应数据层级编码的数据层级值
     *
     * @param tenantId             租户ID
     * @param dataHierarchyCode    数据层级编码
     * @param dataHierarchyValue   数据层级值
     * @param dataHierarchyMeaning 数据层级值展示值
     */
    void saveDataHierarchyValue(long tenantId,
                                String dataHierarchyCode,
                                String dataHierarchyValue,
                                String dataHierarchyMeaning);
}
