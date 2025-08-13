package org.hzero.oauth.domain.repository;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.core.user.UserType;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.vo.UserRoleDetails;
import org.hzero.oauth.domain.vo.UserVO;

/**
 * 用户资源库
 *
 * @author bojiangzhou 2018/12/04
 */
public interface UserRepository extends BaseRepository<User> {

    List<UserRoleDetails> selectRoleDetails(Long userId);

    List<UserRoleDetails> selectRootUserRoleDetails(Long userId, Long tenantId);

    /**
     * 根据登录名查询登录用户信息
     *
     * @param loginName 登录账号
     */
    User selectLoginUserByLoginName(String loginName);

    /**
     * 根据登录名查询登录用户信息
     *
     * @param loginName 登录账号
     */
    User selectLoginUserByLoginName(String loginName, UserType userType);

    /**
     * 根据手机号查询登录用户信息
     *
     * @param phone 手机号
     */
    User selectLoginUserByPhone(String phone, UserType userType);

    /**
     * 根据手机号查询登录用户信息
     *
     * @param internationalTelCode 国际冠码
     * @param phone                手机号
     */
    @Nullable
    User selectLoginUserByPhone(@Nonnull String internationalTelCode, @Nonnull String phone, @Nonnull UserType userType);

    /**
     * 根据邮箱查询登录用户信息
     *
     * @param email 邮箱
     */
    User selectLoginUserByEmail(String email, UserType userType);

    /**
     * 根据用户手机或邮箱查询
     *
     * @param account 邮箱或手机
     * @return User
     */
    User selectUserByPhoneOrEmail(String account, UserType userType);

    /**
     * 查询用户的角色数量
     *
     * @param userId 用户ID
     * @return 用户角色数量
     */
    int countUserMemberRole(Long userId);

    /**
     * 查询当前登录用户
     *
     * @return 返回当前登录用户
     */
    UserVO selectSelf(CustomUserDetails customUserDetails);

    /**
     * 查询用户所属租户
     *
     * @param userId 用户ID
     */
    List<Long> findUserLegalOrganization(Long userId);

    /**
     * 查询用户所有角色
     *
     * @param userId 用户ID
     * @return 角色ID
     */
    List<Long> selectUserRole(Long userId);

    /**
     * 查询角色标签
     *
     * @param roleIds 角色ID
     */
    Set<String> selectRoleLabels(List<Long> roleIds);
}

