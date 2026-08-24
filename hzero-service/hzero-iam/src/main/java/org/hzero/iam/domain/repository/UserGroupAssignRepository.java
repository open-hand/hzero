package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Set;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.domain.entity.UserGroup;
import org.hzero.iam.domain.entity.UserGroupAssign;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户组分配资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
public interface UserGroupAssignRepository extends BaseRepository<UserGroupAssign> {

    /**
     * 分页查询用户组分配信息
     *
     * @param pageRequest     分页参数
     * @param userGroupAssign 用户组分配实体
     * @return Page<UserGroupAssign>
     */
    Page<UserGroupAssign> pageUserGroupAssign(PageRequest pageRequest, UserGroupAssign userGroupAssign);

    /**
     * 查询排除已经分配的用户组信息
     *
     * @param pageRequest     分页参数
     * @param userGroupAssign 用户组分配实体
     * @return Page<UserGroup>
     */
    Page<UserGroup> selectExcludeUserGroups(UserGroupAssign userGroupAssign, PageRequest pageRequest);

    /**
     * 查询排除已经分配的用户信息
     *
     * @param userGroupAssign 用户组分配实体
     * @param pageRequest     分页参数
     * @return Page<User>
     */
    Page<UserGroupAssign> selectExcludeUsers(UserGroupAssign userGroupAssign, PageRequest pageRequest);

    /**
     * 用户分配列表
     *
     * @param userGroupAssigns 用户组分配参数
     * @param typeCode         返回参数查询条件
     * @return Set<Receiver>
     */
    Set<Receiver> listUserGroupAssign(List<UserGroupAssign> userGroupAssigns, List<String> typeCode);

    /**
     * 用户分配列表
     *
     * @param userGroupAssigns  用户组分配参数
     * @param thirdPlatformType 三方平台类型
     * @return Set<Receiver>
     */
    Set<Receiver> listOpenUserGroupAssign(List<UserGroupAssign> userGroupAssigns, String thirdPlatformType);
}
