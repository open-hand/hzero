package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.LdapHistory;
import org.hzero.mybatis.base.BaseRepository;

public interface LdapHistoryRepository extends BaseRepository<LdapHistory> {

    LdapHistory queryLatestHistory(Long ldapId);

    Page<LdapHistory> pageLdapHistories(PageRequest pageRequest, Long ldapId);

}
