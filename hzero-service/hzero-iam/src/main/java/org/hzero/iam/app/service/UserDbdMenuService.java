package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.UserDbdMenu;

/**
 * 
 * 工作台用户级常用功能应用服务
 * 
 * @author xianzhi.chen@hand-china.com 2020年4月8日上午9:51:37
 */
public interface UserDbdMenuService {
    /**
     * 查询常用功能菜单
     * 
     * @return List<UserDbdMenu> 用户工作台菜单集合
     */
    List<UserDbdMenu> queryMenu();

    /**
     * 新增常用功能菜单
     * 
     * @param userDbdMenus 用户工作台菜单集合
     */
    void addMenu(List<UserDbdMenu> userDbdMenus);

}
