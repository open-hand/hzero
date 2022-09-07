package org.hzero.boot.oauth.domain.repository;

import org.hzero.boot.oauth.domain.entity.BaseLdap;
import org.hzero.mybatis.base.BaseRepository;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
public interface BaseLdapRepository extends BaseRepository<BaseLdap> {

    /**
     * 查询租户的Ldap
     *
     * @param tenantId 租户ID
     */
    BaseLdap selectLdap(Long tenantId);

    /**
     * 缓存 Ldap
     * 
     * @param ldap Ldap
     */
    void saveLdap(BaseLdap ldap);

    /**
     * 删除Ldap缓存
     *
     * @param ldap Ldap
     */
    void deleteLdap(BaseLdap ldap);

}
