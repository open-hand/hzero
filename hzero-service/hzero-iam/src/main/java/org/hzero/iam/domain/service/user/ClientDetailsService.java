package org.hzero.iam.domain.service.user;

/**
 * @author qingsheng.chen@hand-china.com
 */
public interface ClientDetailsService {

    /**
     * 查询当前客户端的当前角色
     *
     * @return 当前角色
     */
    Long readClientRole();

    /**
     * 查询当前客户端的当前租户
     *
     * @return 当前租户
     */
    Long readClientTenant();

    /**
     * 更新客户端的当前角色
     *
     * @param roleId 当前角色
     */
    void storeClientRole(Long roleId);

    /**
     * 更新客户端的当前租户
     *
     * @param tenantId 当前租户
     */
    void storeClientTenant(Long tenantId);
}
