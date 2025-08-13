package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.domain.entity.UserGroup;
import org.hzero.iam.domain.entity.UserGroupAssign;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 用户组分配Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
public interface UserGroupAssignMapper extends BaseMapper<UserGroupAssign> {

    /**
     * 分页查询用户组分配信息
     *
     * @param userGroupAssign 用户组分配实体
     * @return List<UserGroupAssign>
     */
    List<UserGroupAssign> selectUserGroupAssignList(UserGroupAssign userGroupAssign);

    /**
     * 查询排除已经分配的用户组列表
     *
     * @param userGroupAssign 用户组分配实体
     * @return List<UserGroup>
     */
    List<UserGroup> selectExcludeUserGroups(UserGroupAssign userGroupAssign);

    /**
     * 查询排除已经分配的用户组列表
     *
     * @param userGroupAssign 用户组分配实体
     * @return List<User>
     */
    List<UserGroupAssign> selectExcludeUsers(UserGroupAssign userGroupAssign);

    /**
     * 查询用户组分配用户的id信息
     *
     * @param userGroupAssign 查询条件
     * @param typeCode        类型编码
     * @return 用户组分配的用户Id集合
     */
    List<Receiver> selectAssignUserIds(UserGroupAssign userGroupAssign, List<String> typeCode);

    /**
     * 查询用户组分配用户对应第三方平台的id信息
     *
     * @param userGroupAssign   查询条件
     * @param thirdPlatformType 三方平台类型
     * @return 用户组分配的用户Id集合
     */
    List<Receiver> selectOpenAssignUserIds(@Param("userGroupAssign") UserGroupAssign userGroupAssign,@Param("thirdPlatformType") String thirdPlatformType);
}
