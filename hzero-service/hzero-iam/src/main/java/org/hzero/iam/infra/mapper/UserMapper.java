package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.vo.CompanyVO;
import org.hzero.iam.domain.vo.UserVO;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author bojiangzhou 2019/04/17 优化代码、SQL
 * @author allen 2018/6/26
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 查询用户简要信息
     */
    List<UserVO> selectSimpleUsers(UserVO params);

    /**
     * 查询某个角色可分配的用户
     *
     * @param params 必须包含 roleId
     */
    List<UserVO> selectAllocateUsers(UserVO params);

    /**
     * 查询用户信息，合并User和UserInf信息，关联租户信息
     *
     * @param params 用户参数
     * @return 用户详细信息
     */
    List<UserVO> selectUserDetails(UserVO params);

    /**
     * 查询当前登录信息
     *
     * @param params 用户参数
     * @return 查询当前登录用户信息
     */
    UserVO selectSelf(UserVO params);

    /**
     * 查询用户最近访问的租户
     */
    List<Tenant> selectTenantAccess(UserVO params);

    /**
     * 查询当前登录用户详细信息
     *
     * @param param 用户参数
     * @return 详细信息
     */
    UserVO selectSelfDetails(UserVO param);

    /**
     * 查询角色下的用户
     *
     * @param memberRoleSearchDTO 查询参数，roleId 不能为空
     */
    List<UserVO> selectRoleUsers(MemberRoleSearchDTO memberRoleSearchDTO);

    /**
     * 根据用户名查询
     *
     * @return 用户简要信息
     */
    UserVO selectByLoginNameOrEmailOrPhone(UserVO params);

    /**
     * 查询用户列表
     *
     * @param params 用户姓名
     * @return 用户列表
     */
    List<UserVO> selectByRealName(UserVO params);

    /**
     * 查询公司名称
     */
    CompanyVO countCompanyByName(@Param("companyName") String companyName);

    /**
     * 查询多租户下的用户列表
     */
    List<UserVO> selectMultiTenantUsers(UserVO params);

    /**
     * 查询公司名称
     */
    UserVO selectCompanyName(@Param("userId") Long userId);

    /**
     * 查询用户租户信息
     */
    List<User> selectUserTenant(User params);

    /**
     * 查询导出的用户信息
     *
     * @param params 参数
     */
    List<UserExportDTO> selectExportUsers(UserVO params);

    Set<String> matchLoginName(@Param("nameSet") Set<String> nameSet);

    Set<String> matchEmail(@Param("emailSet") Set<String> emailSet, @Param("userType") String userType);

    Set<String> matchPhone(@Param("phoneSet") Set<String> phoneSet, @Param("userType") String userType);

    Set<Long> getIdsByMatchLoginName(@Param("nameSet") Set<String> nameSet);

    void disableByIdList(@Param("idSet") Set<Long> idSet);

    /**
     * 查询用户员工分配列表
     *
     * @param userEmployeeAssignDTO 用户员工分配DTO
     * @return List<UserEmployeeAssignDTO>
     */
    List<UserEmployeeAssignDTO> selectUserEmployeeAssignList(UserEmployeeAssignDTO userEmployeeAssignDTO);

    /**
     * 查询缓存用户信息
     */
    List<User> selectCacheUseInfo(User params);

    /**
     * 根据用户ID获取消息接收人
     *
     * @param userIds 用户ID集合
     * @return 用户信息
     */
    List<Receiver> listReceiverByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 根据用户ID获取第三方消息接收人
     *
     * @param userIds           用户ID集合
     * @param thirdPlatformType 第三方平台类型
     * @return 用户信息
     */
    List<Receiver> listOpenReceiverByUserIds(@Param("userIds") List<Long> userIds,
                                             @Param("thirdPlatformType") String thirdPlatformType);

    /**
     * 查询二次校验的用户列表
     *
     * @param searchDTO 查询条件对象
     * @return 查询结果
     */
    List<UserSecCheckDTO> listSecondaryCheck(UserSecCheckSearchDTO searchDTO);
    /**
     * 查询返回语言名称信息
     *
     * @param language 默认语言
     * @return 查询结果
     */
    UserVO getLanguageNameByLanguage(@Param("language") String language, @Param("userId") Long userId);
}
