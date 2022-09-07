package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.Client;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 客户端资源库
 *
 * @author bojiangzhou 2019/08/02
 */
public interface ClientRepository extends BaseRepository<Client> {

    /**
     * 分页查询客户端
     *
     * @param organizationId 租户
     * @param name           客户端名称
     * @param enabledFlag    启用标识
     * @param pageRequest    分页
     * @return 分页数据
     */
    Page<Client> pageClient(Long organizationId, String name, Integer enabledFlag, PageRequest pageRequest);

    /**
     * 查询客户端明细
     *
     * @param tenantId 租户
     * @param clientId 客户端Id
     * @return 客户端明细
     */
    Client detailClient(Long tenantId, Long clientId);

    /**
     * 查询客户端简略消息。默认只查当前租户的，其他租户的要全匹配
     *
     * @param tenantId    租户Id
     * @param roleId      角色Id
     * @param name        客户端名称
     * @param pageRequest 分页
     * @return 客户端
     */
    Page<Client> pageClientSimple(Long tenantId, Long roleId, String name, PageRequest pageRequest);

    /**
     * 查询角色下的客户端列表
     *
     * @param roleId      角色ID
     * @param name        客户端名称
     * @param pageRequest 分页
     * @return 客户端
     */
    Page<Client> selectRoleClients(Long roleId, String name, PageRequest pageRequest);

    /**
     * 初始化缓存 Client
     */
    void initCacheClient();

    /**
     * 缓存 Client
     *
     * @param client client
     */
    void cacheClient(Client client);

    /**
     * 移除缓存中的客户端
     * @param clientName 名称
     */
    void removeCacheClient(String clientName);
}
