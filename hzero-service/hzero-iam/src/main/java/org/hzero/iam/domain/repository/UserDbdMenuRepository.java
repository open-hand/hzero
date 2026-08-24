package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.domain.entity.UserDbdMenu;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 
 * 工作台用户常用功能资源库
 * 
 * @author xianzhi.chen@hand-china.com 2020年4月8日上午10:02:40
 */
public interface UserDbdMenuRepository extends BaseRepository<UserDbdMenu> {
    /**
     *
     * @param userDbdMenu 用户常用功能菜单
     * @return List<UserDbdMenu> 用户常用功能集合
     */
    List<UserDbdMenu> queryMenu(UserDbdMenu userDbdMenu);

    /**
     * 
     * 删除已有的常用功能
     * 
     * @param userDbdMenu 用户常用功能菜单
     * @return 影响条数
     */
    int deleteDbdMenu(UserDbdMenu userDbdMenu);

}
