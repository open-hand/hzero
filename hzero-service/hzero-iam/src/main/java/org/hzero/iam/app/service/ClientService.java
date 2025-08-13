package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.vo.RoleVO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


public interface ClientService {

    /**
     * 创建客户端
     */
    Client create(Client client);

    /**
     * 修改客户端
     */
    void update(Client client);

    /**
     * 删除客户端
     */
    void delete(Client client);

    /**
     * 创建时校验 client
     */
    void createCheck(Client client);

    /**
     * 启用客户端
     *
     * @param client 客户端
     */
    void enable(Client client);

    /**
     * 禁用客户端
     *
     * @param client 客户端
     */
    void disable(Client client);

    /**
     * 查询客服端可访问角色
     *
     * @param organizationId 组织ID
     * @param clientId       客户端ID
     * @param pageRequest    分页
     * @return 角色列表
     */
    Page<RoleVO> listClientAccessRoles(Long organizationId, Long clientId, PageRequest pageRequest);

    /**
     * 新增客户端可访问角色
     *
     * @param organizationId 组织ID
     * @param memberRoleList 角色列表
     * @param clientId       客户端ID
     * @return 角色列表
     */
    List<MemberRole> createAccessRoles(Long organizationId, List<MemberRole> memberRoleList, Long clientId);

    /**
     * 删除客户端可访问角色
     *
     * @param organizationId 组织ID
     * @param memberRoleList 角色列表
     * @param clientId       客户端ID
     */
    void deleteAccessRoles(Long organizationId, List<MemberRole> memberRoleList, Long clientId);

    /**
     * 查询客户端明细
     *
     * @param tenantId 租户
     * @param clientId 客户端Id
     * @return 客户端明细
     */
    Client detailClient(Long tenantId, Long clientId);

    /**
     * 分页查询客户端列表
     *
     * @param organizationId 组织Id
     * @param name           名称
     * @param enabledFlag    启用标识
     * @param pageRequest    分页参数
     * @return Page<Client>
     */
    Page<Client> pageClient(Long organizationId, String name, Integer enabledFlag, PageRequest pageRequest);
}
