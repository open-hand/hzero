package org.hzero.iam.domain.service.ldap;

import org.hzero.iam.domain.entity.LdapSyncConfig;
import org.hzero.iam.infra.constant.LdapSyncType;

/**
 * Ldap 同步配置 领域服务
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/09 16:09
 */
public interface LdapSyncConfigDomainService {

    /**
     * 创建 Ldap 同步配置
     *
     * @param tenantId 租户Id
     * @param ldapSyncConfig Ldap 同步配置
     * @return 同步配置新增结果
     */
    LdapSyncConfig createLdapSyncConfig(Long tenantId, LdapSyncConfig ldapSyncConfig);

    /**
     * 修改 Ldap 同步配置
     *
     * @param tenantId 租户Id
     * @param ldapSyncConfig Ldap 同步配置
     * @return LdapSyncConfig
     */
    LdapSyncConfig updateLdapUserConfig(Long tenantId, LdapSyncConfig ldapSyncConfig);

    /**
     * 查询当前租户 Ldap 同步配置
     *
     * @param tenantId 租户Id
     * @param ldapSyncType LDAP配置类型
     * @return LdapSyncConfig
     */
    LdapSyncConfig queryLdapUserConfig(Long tenantId, LdapSyncType ldapSyncType);

    /**
     * LDAP 同步
     *
     * @param ldapSyncConfigId LDAP同步配置ID
     */
    void syncLdap(Long ldapSyncConfigId);
}
