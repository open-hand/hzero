package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.UserOpenAccount;
import org.hzero.iam.domain.repository.UserOpenAccountRepository;
import org.hzero.iam.infra.mapper.UserOpenAccountMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 用户第三方账号资源库实现
 *
 * @author jiaxu.cui@hand-china.com 2018/9/29 16:27
 */
@Component
public class UserOpenAccountRepositoryImpl extends BaseRepositoryImpl<UserOpenAccount> implements UserOpenAccountRepository {
    @Autowired
    private UserOpenAccountMapper userOpenAccountMapper;

    @Override
    public List<UserOpenAccount> selectOpenAppAndBindUser(String username) {
        return userOpenAccountMapper.selectOpenAppAndBindUser(username);
    }
}
