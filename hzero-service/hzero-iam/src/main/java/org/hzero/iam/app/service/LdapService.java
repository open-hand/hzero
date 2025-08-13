package org.hzero.iam.app.service;

import org.hzero.iam.api.dto.LdapAccountDTO;
import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapSyncConfig;

/**
 * Ldap 应用服务
 *
 * @author bojiangzhou 2019/08/02
 */
public interface LdapService {

    /**
     * 创建 Ldap
     */
    Ldap create(Ldap ldap);

    /**
     * 更新 Ldap
     */
    void update(Ldap ldap);

    /**
     * 删除 Ldap
     */
    void delete(Ldap ldap);

    /**
     * 启用 Ldap
     */
    void enableLdap(Ldap ldap);

    /**
     * 禁用 Ldap
     */
    void disableLdap(Ldap ldap);

    /**
     * 测试是否能连接到 ldap
     *
     * @param organizationId 组织id
     * @param ldapId ldapId
     * @return LdapConnectionDTO 连接测试结构体
     */
    LdapConnectionDTO testConnect(Long organizationId, Long ldapId, LdapAccountDTO ldapAccountDTO);

    /**
     * 根据ldap配置同步用户
     */
    void syncLdapUser(Long organizationId, Long ldapId);

    /**
     * 根据 ldap id 更新历史记录的 endTime
     */
    void stop(Long ldapId);

    /**
     * 保存Ldap定时同步用户配置
     *
     * @param tenantId 租户Id
     * @param ldapSyncConfig 定时同步配置
     * @return 新增配置
     */
    LdapSyncConfig saveLdapSyncUserConfig(Long tenantId, LdapSyncConfig ldapSyncConfig);

    /**
     * 查询当前租户Ldap定时同步用户配置
     *
     * @param tenantId 租户Id
     * @return LdapSyncConfig
     */
    LdapSyncConfig queryLdapSyncUserConfig(Long tenantId);

    /**
     * 保存Ldap定时同步离职用户配置
     *
     * @param tenantId 租户Id
     * @param ldapSyncConfig 定时同步配置
     * @return 新增配置
     */
    LdapSyncConfig saveLdapSyncLeaveConfig(Long tenantId, LdapSyncConfig ldapSyncConfig);

    /**
     * 查询当前租户Ldap定时同步离职用户配置
     *
     * @param tenantId 租户Id
     * @return LdapSyncConfig
     */
    LdapSyncConfig queryLdapSyncLeaveConfig(Long tenantId);
}
