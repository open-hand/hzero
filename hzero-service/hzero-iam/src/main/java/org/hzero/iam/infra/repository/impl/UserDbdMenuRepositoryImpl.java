package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.hzero.iam.domain.entity.UserDbdMenu;
import org.hzero.iam.domain.repository.UserDbdMenuRepository;
import org.hzero.iam.infra.mapper.UserDbdMenuMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 工作台用户级常用功能 资源库实现
 *
 * @author zhixiang.huang@hand-china.com 2019-02-18 10:59:52
 */
@Component
public class UserDbdMenuRepositoryImpl extends BaseRepositoryImpl<UserDbdMenu> implements UserDbdMenuRepository {
    @Autowired
    private UserDbdMenuMapper dbdUserMenuMapper;

    @Override
    public List<UserDbdMenu> queryMenu(UserDbdMenu dbdUserMenu) {
        return dbdUserMenuMapper.queryMenu(dbdUserMenu);
    }

    @Override
    public int deleteDbdMenu(UserDbdMenu userDbdMenu) {
        return dbdUserMenuMapper.deleteDbdMenu(userDbdMenu);
    }

}
