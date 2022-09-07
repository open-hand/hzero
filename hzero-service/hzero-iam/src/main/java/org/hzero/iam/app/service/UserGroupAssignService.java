package org.hzero.iam.app.service;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.domain.entity.UserGroupAssign;

import java.util.List;
import java.util.Set;

/**
 * 用户组分配应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
public interface UserGroupAssignService {

    /**
     * 创建用户组分配
     *
     * @param userGroupAssignList 用户组分配实体集合
     * @return UserGroupAssign
     */
    List<UserGroupAssign> createOrUpdateUserGroupAssign(List<UserGroupAssign> userGroupAssignList);

    /**
     * 删除用户组分配信息
     *
     * @param userGroupAssignList 用户组实体集合
     */
    void removeUserGroupAssign(List<UserGroupAssign> userGroupAssignList);

    /**
     * 获取用户组对应第三方平台用户ID
     *
     * @param userGroupAssigns  用户组分配
     * @param thirdPlatformType 三方平台类型
     * @return 三方平台用户ID
     */
    Set<Receiver> listOpenUserGroupAssign(List<UserGroupAssign> userGroupAssigns, String thirdPlatformType);
}
