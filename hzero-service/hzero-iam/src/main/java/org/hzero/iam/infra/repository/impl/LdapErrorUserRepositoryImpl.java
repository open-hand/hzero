package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.core.message.MessageAccessor;
import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.iam.domain.repository.LdapErrorUserRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

@Component
public class LdapErrorUserRepositoryImpl extends BaseRepositoryImpl<LdapErrorUser> implements LdapErrorUserRepository {


    @Override
    public Page<LdapErrorUser> pageLdapHistoryErrorUsers(PageRequest pageRequest, Long ldapHistoryId, LdapErrorUser ldapErrorUser) {
        Page<LdapErrorUser> list = PageHelper.doPageAndSort(pageRequest, () -> this.selectByCondition(Condition.builder(LdapErrorUser.class)
                .where(Sqls.custom()
                        .andEqualTo(LdapErrorUser.FIELD_LDAP_HISTORY_ID, ldapHistoryId)
                        .andLike(LdapErrorUser.FIELD_UUID, ldapErrorUser.getUuid(), true)
                        .andLike(LdapErrorUser.FIELD_LOGIN_NAME, ldapErrorUser.getLoginName(), true)
                        .andLike(LdapErrorUser.FIELD_EMAIL, ldapErrorUser.getEmail(), true)
                        .andLike(LdapErrorUser.FIELD_REAL_NAME, ldapErrorUser.getRealName(), true)
                        .andLike(LdapErrorUser.FIELD_PHONE, ldapErrorUser.getPhone(), true)
                ).build()
        ));

        //cause国际化处理
        List<LdapErrorUser> errorUsers = list.getContent();
        errorUsers.forEach(errorUser -> {
            String cause = errorUser.getCause();
            errorUser.setCause(MessageAccessor.getMessage(cause).desc());
        });
        return list;
    }
}
