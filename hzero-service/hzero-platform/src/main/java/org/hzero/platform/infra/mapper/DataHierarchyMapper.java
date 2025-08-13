package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.domain.entity.DataHierarchy;

import java.util.List;

/**
 * 数据层级配置Mapper
 *
 * @author qingsheng.chen@hand-china.com 2019-08-14 09:12:07
 */
public interface DataHierarchyMapper extends BaseMapper<DataHierarchy> {

    /**
     * 查询数据层级配置树形列表
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级编码
     * @param dataHierarchyName 数据层级名称
     * @return 数据层级配置树形列表
     */
    List<DataHierarchyDTO> listDataHierarchy(@Param("tenantId") Long tenantId,
                                             @Param("dataHierarchyCode") String dataHierarchyCode,
                                             @Param("dataHierarchyName") String dataHierarchyName,
                                             @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询数据层级配置明细
     *
     * @param tenantId        租户ID
     * @param dataHierarchyId 数据层级ID
     * @return 数据层级配置
     */
    DataHierarchyDTO getDataHierarchy(@Param("tenantId") Long tenantId,
                                      @Param("dataHierarchyId") Long dataHierarchyId);

    /**
     * 查询数据层级配置明细
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级配置编码
     * @return 数据层级配置
     */
    DataHierarchyDTO getDataHierarchyByCode(@Param("tenantId") Long tenantId, @Param("dataHierarchyCode") String dataHierarchyCode);

    /**
     * 查询数据层级配置子节点列表
     *
     * @param levelPath 层级路径
     * @return 数据层级配置子节点
     */
    List<DataHierarchy> listDataHierarchyChildren(@Param("tenantId") long tenantId,
                                                  @Param("levelPath") String levelPath);

    /**
     * 查询数据层级配置子节点列表
     *
     * @param tenantId          租户ID
     * @param dataHierarchyCode 数据层级编码
     * @return 数据层级配置子节点
     */
    List<DataHierarchy> listDataHierarchyChildrenWithUnique(@Param("tenantId") long tenantId,
                                                            @Param("dataHierarchyCode") String dataHierarchyCode);
}

