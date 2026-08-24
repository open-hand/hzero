package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.mybatis.base.BaseRepository;

public interface LdapErrorUserRepository extends BaseRepository<LdapErrorUser> {

    Page<LdapErrorUser> pageLdapHistoryErrorUsers(PageRequest pageRequest, Long ldapHistoryId, LdapErrorUser ldapErrorUser);
}
