package org.hzero.iam.infra.repository.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.LdapHistory;
import org.hzero.iam.domain.repository.LdapHistoryRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

@Component
public class LdapHistoryRepositoryImpl extends BaseRepositoryImpl<LdapHistory> implements LdapHistoryRepository {

    @Override
    public LdapHistory queryLatestHistory(Long ldapId) {
        LdapHistory params = new LdapHistory();
        params.setLdapId(ldapId);
        List<LdapHistory> ldapHistories = this.select(params);
        if (ldapHistories.isEmpty()) {
            return null;
        } else {
            ldapHistories.sort(Comparator.comparing(LdapHistory::getId).reversed());
            return ldapHistories.get(0);
        }
    }

    @Override
    public Page<LdapHistory> pageLdapHistories(PageRequest pageRequest, Long ldapId) {
        return PageHelper.doPageAndSort(pageRequest, () -> this.selectByCondition(Condition.builder(LdapHistory.class)
                .where(Sqls.custom()
                        .andEqualTo(LdapHistory.FIELD_LDAP_ID, ldapId)
                        .andIsNotNull(LdapHistory.FIELD_SYNC_END_TIME)
                ).build()
        ));
    }

}
