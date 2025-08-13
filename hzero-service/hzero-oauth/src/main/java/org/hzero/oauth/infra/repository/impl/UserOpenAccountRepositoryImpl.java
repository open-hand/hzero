package org.hzero.oauth.infra.repository.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.oauth.domain.entity.UserOpenAccount;
import org.hzero.oauth.domain.repository.UserOpenAccountRepository;

/**
 *
 * @author bojiangzhou 2019/09/01
 */
@Component
public class UserOpenAccountRepositoryImpl extends BaseRepositoryImpl<UserOpenAccount> implements UserOpenAccountRepository {

    @Override
    public List<String> selectUsernameByOpenId(String openAppCode, String openId) {
        if (StringUtils.isAnyBlank(openAppCode, openId)) {
            return Collections.emptyList();
        }
        UserOpenAccount openAccount = new UserOpenAccount();
        openAccount.setOpenAppCode(openAppCode);
        openAccount.setOpenId(openId);
        List<UserOpenAccount> accounts = select(openAccount);
        return accounts.stream().map(UserOpenAccount::getUsername).collect(Collectors.toList());
    }

    @Override
    public List<String> selectUsernameByUnionId(String openAppCode, String unionId) {
        if (StringUtils.isAnyBlank(openAppCode, unionId)) {
            return Collections.emptyList();
        }
        UserOpenAccount openAccount = new UserOpenAccount();
        openAccount.setOpenAppCode(openAppCode);
        openAccount.setUnionId(unionId);
        List<UserOpenAccount> accounts = select(openAccount);
        return accounts.stream().map(UserOpenAccount::getUsername).collect(Collectors.toList());
    }

    @Override
    public List<UserOpenAccount> selectOpenAccountByUsername(String openAppCode, String username) {
        UserOpenAccount openAccount = new UserOpenAccount();
        openAccount.setOpenAppCode(openAppCode);
        openAccount.setUsername(username);
        return select(openAccount);
    }

    @Override
    public UserOpenAccount selectOpenAccount(String openAppCode, String username) {
        UserOpenAccount openAccount = new UserOpenAccount();
        openAccount.setOpenAppCode(openAppCode);
        openAccount.setUsername(username);
        return selectOne(openAccount);
    }
}
