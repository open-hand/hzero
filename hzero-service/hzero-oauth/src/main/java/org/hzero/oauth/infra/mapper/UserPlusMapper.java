package org.hzero.oauth.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.vo.UserRoleDetails;
import org.hzero.oauth.domain.vo.UserVO;

/**
 * UserMapper
 *
 * @author bojiangzhou 2019/02/26
 */
public interface UserPlusMapper extends BaseMapper<User> {

    /**
     * 查询登录必要信息
     *
     * @param params 参数
     */
    User selectLoginUser(User params);

    /**
     * 查询登录必要信息
     *
     * @param params 参数
     */
    UserVO selectSelf(UserVO params);

    /**
     * 查询用户角色信息
     *
     * @param userId 用户ID
     * @return 用户角色信息
     */
    List<UserRoleDetails> selectRoleDetails(@Param("userId") Long userId);

    List<UserRoleDetails> selectRootUserRoleDetails(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    /**
     * 查询用户分配的角色数量
     *
     * @param userId 用户ID
     */
    int countUserMemberRole(@Param("userId") Long userId);

    List<Long> findUserLegalOrganization(@Param("userId") Long userId);

    /**
     * 查询用户所有角色ID
     *
     * @param userId 用户ID
     * @return 角色ID
     */
    List<Long> selectUserRole(@Param("userId") Long userId);

    /**
     * 查询角色标签
     */
    Set<String> selectRoleLabels(@Param("roleIds") List<Long> roleIds);
}
