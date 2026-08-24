package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.export.vo.ExportParam;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserConfig;
import org.hzero.iam.domain.entity.UserInfo;
import org.hzero.iam.domain.vo.CompanyVO;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.mybatis.base.BaseRepository;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author bojiangzhou 2019/04/17 代码优化
 * @author allen 2018/6/26
 */
public interface UserRepository extends BaseRepository<User> {

    /**
     * 分页查询用户简要信息，用于列表展示用户基本信息
     */
    Page<UserVO> selectSimpleUsers(UserVO params, PageRequest pageRequest);

    /**
     * 分页查询用户简要信息，跨租户查询所有用户
     */
    Page<UserVO> selectAllocateUsers(UserVO params, PageRequest pageRequest);

    /**
     * 查询用户信息，必须包含 userId
     *
     * @param params 用户
     * @return List UserVO
     */
    UserVO selectUserDetails(UserVO params);

    /**
     * 查询当前登录用户
     *
     * @return 返回当前登录用户
     */
    UserVO selectSelf();

    /**
     * 查询当前登录用户详情
     *
     * @return 当前登陆用户详细信息
     */
    UserVO selectSelfDetails();

    /**
     * 查询用户简要信息
     *
     * @param userId 用户ID
     */
    User selectSimpleUserById(Long userId);

    /**
     * 查询用户密码
     *
     * @param userId 用户ID
     * @return 包含用户密码
     */
    User selectUserPassword(Long userId);

    /**
     * 根据用户ID和租户ID查询用户
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return User
     */
    User selectSimpleUserByIdAndTenantId(Long userId, Long tenantId);

    /**
     * 查询角色下的用户列表
     *
     * @param roleId              角色ID
     * @param memberRoleSearchDTO 查询参数
     * @param pageRequest         分页
     */
    Page<UserVO> selectRoleUsers(Long roleId, MemberRoleSearchDTO memberRoleSearchDTO, PageRequest pageRequest);

    /**
     * 更新UserInfo
     *
     * @param userInfo 用户信息
     * @return 1/0
     */
    int updateUserInfoByPrimaryKey(UserInfo userInfo);


    /**
     * 更新UserConfig
     *
     * @param userConfig 用户信息
     * @return 1/0
     */
    int updateUserConfigByPrimaryKey(UserConfig userConfig);

    /**
     * 查询UserInfo
     *
     * @param userId 用户ID
     * @return UserInfo
     */
    UserInfo selectUserInfoByPrimaryKey(Long userId);

    /**
     * 插入用户信息
     *
     * @param userInfo 用户信息
     * @return 1/0
     */
    int insertUserInfoSelective(UserInfo userInfo);

    /**
     * 插入用户默认配置信息
     *
     * @param userConfig 用户配置信息
     * @return 1/0
     */
    int insertUserConfigSelective(UserConfig userConfig);

    /**
     * 查询账号 或 手机 或 邮箱 是否存在
     */
    boolean existsUser(String loginName, String phone, String email, String userType);

    /**
     * 查询账号是否存在
     */
    boolean existsByLoginName(String loginName);

    /**
     * 查询手机是否存在
     */
    boolean existsByPhone(String phone, String userType);

    /**
     * 查询邮箱是否存在
     */
    boolean existsByEmail(String email, String userType);

    /**
     * 根据用户名查询用户简单信息
     */
    UserVO selectByLoginNameOrEmailOrPhone(UserVO params);

    /**
     * 查询用户列表
     *
     * @param params 用户姓名
     * @return 用户列表
     */
    List<UserVO> selectByRealNameOrEmail(UserVO params);

    /**
     * 导出用户信息
     */
    List<UserExportDTO> exportUserInfo(UserVO params, String authorityTypeQueryParams, PageRequest pageRequest, ExportParam exportParam);

    /**
     * 根据公司名称查询是否存在
     *
     * @param companyName 公司名称
     */
    CompanyVO countCompanyByName(String companyName);

    /**
     * 分页查询多租户下的用户
     */
    Page<UserVO> selectMultiTenantUsers(UserVO params, PageRequest pageRequest);

    /**
     * 查询公司信息
     */
    UserVO selectCompanyName(Long userId);

    /**
     * 拉取最近更新过的记录
     *
     * @param tenantId 租户ID
     * @param after    这个时间点之后
     * @return 最近更新过的记录
     */
    List<User> listRecentUser(Long tenantId, Date after);

    /**
     * 查询用户租户
     *
     * @param id 用户ID
     * @return User，包含租户信息
     */
    User selectSimpleUserWithTenant(Long id);

    /**
     * 查询用户租户
     */
    List<User> selectSimpleUsersWithTenant(User params);

    /**
     * 查询租户下的用户数量
     *
     * @param organizationId 租户ID
     * @return 用户数量
     */
    int countTenantUser(Long organizationId);

    /**
     * 缓存用户基本信息
     */
    void initUsers();

    /**
     * 根据用户名匹配
     *
     * @param nameSet loginName set
     * @return 匹配的用户登录名
     */
    Set<String> matchLoginName(Set<String> nameSet);

    /**
     * 根据邮箱匹配
     *
     * @param emailSet email set
     * @return 匹配的用户邮箱
     */
    Set<String> matchEmail(Set<String> emailSet, String userType);

    /**
     * 根据手机匹配
     *
     * @param phoneSet phone set
     * @return 匹配的用户手机
     */
    Set<String> matchPhone(Set<String> phoneSet, String userType);

    User selectByLoginName(String loginName);

    Set<Long> getIdsByMatchLoginName(Set<String> nameSet);

    void disableByIdList(Set<Long> ids);

    void batchCacheUser(List<User> users);

    /**
     * 缓存用户信息
     *
     * @param userId 用户ID
     */
    void cacheUser(Long userId);

    /**
     * 分页查询用户员工分配信息
     *
     * @param pageRequest           分页参数
     * @param userEmployeeAssignDTO 用户员工分配DTO
     * @return Page<UserEmployeeAssignDTO>
     */
    Page<UserEmployeeAssignDTO> pageUserEmployeeAssign(PageRequest pageRequest,
                                                       UserEmployeeAssignDTO userEmployeeAssignDTO);

    /**
     * 根据用户ID获取消息接收人
     *
     * @param userIds 用户ID集合
     * @return 用户信息
     */
    List<Receiver> listReceiverByUserIds(List<Long> userIds);

    /**
     * 根据用户ID获取第三方消息接收人
     *
     * @param userIds           用户ID集合
     * @param thirdPlatformType 第三方平台类型
     * @return 用户信息
     */
    List<Receiver> listOpenReceiverByUserIds(List<Long> userIds, String thirdPlatformType);

    /**
     * 查询二次校验的用户列表
     *
     * @param searchDTO 查询条件对象
     * @return 查询结果
     */
    List<UserSecCheckDTO> listSecondaryCheck(UserSecCheckSearchDTO searchDTO);

    /**
     * 包裹或验证 self-user
     */
    interface UserSelfWrapper {
        void wrap(UserVO user);
    }
}
