package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Client;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * ClientMapper
 *
 * @author bojiangzhou 2019/08/02
 */
public interface ClientMapper extends BaseMapper<Client> {

    /**
     * 查询客户端
     *
     * @param organizationId 租户
     * @param name           客户端名称
     * @param enabledFlag    启用标识
     * @return 客户端
     */
    List<Client> listClient(@Param("organizationId") Long organizationId,
                            @Param("name") String name,
                            @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询客户端明细
     *
     * @param organizationId 租户Id
     * @param clientId       客户端Id
     * @return 客户端明细
     */
    Client detailClient(@Param("organizationId") Long organizationId,
                        @Param("clientId") Long clientId);

    /**
     * 查询客户端简略消息。默认只查当前租户的，其他租户的要全匹配
     *
     * @param organizationId 租户
     * @param roleId         角色Id
     * @param name           客户端名称
     * @return 客户端
     */
    List<Client> listClientSimple(@Param("organizationId") Long organizationId,
                                  @Param("roleId") Long roleId,
                                  @Param("name") String name);

    /**
     * 查询角色下的客户端
     *
     * @param roleId 角色
     * @param name   客户端名称
     * @return 客户端
     */
    List<Client> selectRoleClients(@Param("roleId") Long roleId,
                                   @Param("name") String name);
}
