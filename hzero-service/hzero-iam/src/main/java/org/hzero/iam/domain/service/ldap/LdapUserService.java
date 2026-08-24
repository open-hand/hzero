package org.hzero.iam.domain.service.ldap;

import java.util.List;
import java.util.Map;

import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.iam.domain.entity.User;

/**
 * Ldap 用户服务
 *
 * @author bojiangzhou 2019/08/05
 */
public interface LdapUserService {

    /**
     * 批量插入 Ldap 用户
     *
     * @param insertUsers 待新增的用户
     * @return 插入错误的用户
     */
    List<LdapErrorUser> batchCreateUsers(List<User> insertUsers);

    /**
     * 批量更新 Ldap 用户
     *
     * @param updateUsers 待更新的用户
     * @return 更新错误的用户
     */
    List<LdapErrorUser> batchUpdateUsers(List<User> updateUsers);

    /**
     * 验证 Ldap 连接
     *
     * @param ldap LDAP
     * @return (ldapTemplate, ldapConnectionDTO)
     */
    Map<String, Object> validateLdapConnection(Ldap ldap);

    /**
     * 验证 Ldap
     *
     * @param organizationId 租户ID
     * @param ldapId LDAP ID
     * @return LDAP
     */
    Ldap validateLdap(Long organizationId, Long ldapId);
}
