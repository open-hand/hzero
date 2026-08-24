package org.hzero.iam.infra.repository.impl;

import org.hzero.iam.domain.entity.LdapSyncConfig;
import org.hzero.iam.domain.repository.LdapSyncConfigRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * Ldap 同步配置 资源库实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/09 15:54
 */
@Component
public class LdapSyncConfigRepositoryImpl extends BaseRepositoryImpl<LdapSyncConfig>
                implements LdapSyncConfigRepository {

}
