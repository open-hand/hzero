package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.Ldap;
import org.hzero.mybatis.base.BaseRepository;

/**
 * @author wuguokai
 */
public interface LdapRepository extends BaseRepository<Ldap> {

    /**
     * 查询租户的 Ldap 信息
     */
    Ldap selectLdapByTenantId(Long tenantId);

    /**
     * 查询租户的 Ldap 信息
     */
    Ldap selectLdap(Long tenantId, Long ldapId);

    /**
     * 初始化缓存 Ldap
     */
    void initCacheLdap();

    /**
     * 缓存 Ldap
     * 
     * @param ldap ldap
     */
    void cacheLdap(Ldap ldap);

    /**
     * 删除 Ldap 缓存
     *
     * @param ldap Ldap对象
     */
    void deleteLdap(Ldap ldap);
}
