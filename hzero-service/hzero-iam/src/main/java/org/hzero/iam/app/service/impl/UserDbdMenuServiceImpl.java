package org.hzero.iam.app.service.impl;

import java.util.List;

import org.hzero.core.base.BaseAppService;
import org.hzero.iam.app.service.UserDbdMenuService;
import org.hzero.iam.domain.entity.UserDbdMenu;
import org.hzero.iam.domain.repository.UserDbdMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 工作台用户级常用功能应用服务默认实现
 *
 * @author zhixiang.huang@hand-china.com 2019-02-18 10:59:52
 */
@Service
public class UserDbdMenuServiceImpl extends BaseAppService implements UserDbdMenuService {

    @Autowired
    private UserDbdMenuRepository userDbdMenuRepository;

    @Override
    public List<UserDbdMenu> queryMenu() {
        CustomUserDetails cud  = DetailsHelper.getUserDetails();
        UserDbdMenu userDbdMenu = new UserDbdMenu(cud.getTenantId(), cud.getUserId(), cud.getRoleId());
        return userDbdMenuRepository.queryMenu(userDbdMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMenu(List<UserDbdMenu> dbdUserMenus) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long roleId = DetailsHelper.getUserDetails().getRoleId();
        Long userId = DetailsHelper.getUserDetails().getUserId();
        UserDbdMenu userDbdMenu = new UserDbdMenu(tenantId, userId, roleId);
        userDbdMenuRepository.deleteDbdMenu(userDbdMenu);
        dbdUserMenus.forEach(userDbdMenuSet -> {
            userDbdMenuSet.setTenantId(tenantId);
            userDbdMenuSet.setUserId(userId);
            userDbdMenuSet.setRoleId(roleId);
        });
        userDbdMenuRepository.batchInsertSelective(dbdUserMenus);
    }

}
