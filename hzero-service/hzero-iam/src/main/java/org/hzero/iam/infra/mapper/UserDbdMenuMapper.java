package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.UserDbdMenu;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 
 * 工作台用户级常用功能Mapper
 * 
 * @author xianzhi.chen@hand-china.com 2020年4月8日上午10:08:38
 */
public interface UserDbdMenuMapper extends BaseMapper<UserDbdMenu> {
    /**
     *
     * @param dbdUserMenu
     * @return List<UserDbdMenu> 用户常用功能
     */
    List<UserDbdMenu> queryMenu(UserDbdMenu userDbdMenu);

    /**
     * 
     * 删除用户角色常用功能
     * 
     * @param userDbdMenu 删除条件
     * @return 影响条数
     */
    int deleteDbdMenu(UserDbdMenu userDbdMenu);

}
