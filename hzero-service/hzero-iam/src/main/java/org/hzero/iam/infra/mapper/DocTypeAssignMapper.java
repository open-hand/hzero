package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.DocTypeAssign;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 单据类型分配Mapper
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
public interface DocTypeAssignMapper extends BaseMapper<DocTypeAssign> {

    /**
     * 查询单据类型定义分配列表
     *
     * @param tenantId  租户ID
     * @param docTypeId 单据类型定义ID
     * @return 单据类型定义分配列表
     */
    List<DocTypeAssign> listAssign(@Param("tenantId") Long tenantId,
                                   @Param("docTypeId") long docTypeId);

    /**
     * 删除订单类型下租户分配信息
     *
     * @param docTypeId
     * @return
     */
    int deleteByDocTypeId(@Param("docTypeId") Long docTypeId);


    /**
     * 查询订单类型下的租户分配信息
     *
     * @param docTypeId
     * @return
     */
    List<DocTypeAssign> selectByDocTypeId(@Param("docTypeId") Long docTypeId);

    /**
     * 获取租户级单据权限下分配的租户Ids
     *
     * @param tenantDocIds 租户级仅配置单据权限Ids
     * @return Set<Long> 分配的租户Ids
     */
    Set<Long> getAssignTenantIdsByDocIds(@Param("tenantDocIds") List<Long> tenantDocIds);
}
