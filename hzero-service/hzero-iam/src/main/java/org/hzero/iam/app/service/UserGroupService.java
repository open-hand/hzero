package org.hzero.iam.app.service;

import org.hzero.iam.domain.entity.UserGroup;

/**
 * 用户组应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-14 13:44:56
 */
public interface UserGroupService {

    /**
     * 创建用户组
     *
     * @param userGroup 用户组实体
     * @return UserGroup
     */
    UserGroup createUserGroup(UserGroup userGroup);

    /**
     * 修改用户组
     *
     * @param userGroup 用户组实体
     * @return UserGroup
     */
    UserGroup updateUserGroup(UserGroup userGroup);

    /**
     * 删除用户组
     *
     * @param userGroup 用户组实体
     */
    void removeUserGroup(UserGroup userGroup);
}
