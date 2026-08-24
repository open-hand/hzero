package org.hzero.boot.oauth.infra.repository.impl;

import org.springframework.stereotype.Component;

import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.repository.BaseUserRepository;
import org.hzero.core.user.UserType;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
@Component
public class BaseUserRepositoryImpl extends BaseRepositoryImpl<BaseUser> implements BaseUserRepository {

    @Override
    public boolean existsByLoginName(String loginName) {
        BaseUser params = new BaseUser();
        params.setLoginName(loginName);
        return selectCount(params) > 0;
    }

    @Override
    public boolean existsByPhone(String phone, UserType userType) {
        BaseUser params = new BaseUser();
        params.setPhone(phone);
        params.setUserType(userType.value());
        return selectCount(params) > 0;
    }

    @Override
    public boolean existsByEmail(String email, UserType userType) {
        BaseUser params = new BaseUser();
        params.setEmail(email);
        params.setUserType(userType.value());
        return selectCount(params) > 0;
    }

    @Override
    public boolean existsUser(String loginName, String phone, String email, UserType userType) {
        return existsByLoginName(loginName) || existsByPhone(phone, userType) || existsByEmail(email, userType);
    }
}
