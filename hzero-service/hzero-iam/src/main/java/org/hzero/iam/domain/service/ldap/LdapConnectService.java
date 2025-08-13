package org.hzero.iam.domain.service.ldap;

import java.util.Map;

import org.hzero.iam.domain.entity.Ldap;

/**
 * Ldap 连接服务
 *
 * @author bojiangzhou 2019/08/04
 */
public interface LdapConnectService {

    Map<String, Object> testConnect(Ldap ldap);

}
