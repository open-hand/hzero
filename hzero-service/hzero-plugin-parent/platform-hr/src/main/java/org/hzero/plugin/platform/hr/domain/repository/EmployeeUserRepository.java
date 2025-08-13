package org.hzero.plugin.platform.hr.domain.repository;

import org.apache.ibatis.annotations.Param;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.api.dto.EmployeeUserDTO;
import org.hzero.plugin.platform.hr.api.dto.UserDTO;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeUser;

import java.util.List;
import java.util.Set;

/**
 * 员工用户关系资源库
 *
 * @author like.zhang@hand-china.com 2018-09-17 11:20:55
 */
public interface EmployeeUserRepository extends BaseRepository<EmployeeUser> {

    /**
     * 通过当前员工ID，分页查询所有有关联的员工信息
     *
     * @param tenantId   租户id
     * @param employeeId 员工id
     * @return 分页查询结果
     */
    List<EmployeeUserDTO> listByEmployeeId(Long tenantId, Long employeeId);

    /**
     * 查询员工列表
     *
     * @param employeeUserList 员工ID
     * @return 员工列表
     */
    List<EmployeeUserDTO> listEmployeeUser(Set<Long> employeeUserList);

    /**
     * 查询员工列表
     *
     * @param userId      用户ID
     * @param tenantId    租户ID
     * @param enabledFlag 是否启用
     * @return 员工列表
     */
    EmployeeUserDTO getEmployee(long userId, long tenantId, Integer enabledFlag);

    /**
     * 查询员工列表
     *
     * @param userIdList 用户ID
     * @param tenantId   租户ID
     * @return 员工列表
     */
    List<EmployeeUserDTO> listEmployee(List<Long> userIdList, long tenantId);

    /**
     * 通过传入的员工编码查询所有有关联的员工信息
     *
     * @param organizationId 租户id
     * @param employeeNums 员工编码
     * @return List<EmployeeUserDTO>
     */
    List<EmployeeUserDTO> listByEmployeeNum(Long organizationId, List<String> employeeNums);

    /**
     * 员工-用户关系批量新增
     *
     * @param employeeUsers 员工用户关系列表
     * @return 员工列表
     */
    List<EmployeeUser> batchInsertEmployees(List<EmployeeUser> employeeUsers);

    /**
     * 通过用户登录名查询用户信息
     *
     * @param organizationId 租户ID
     * @param loginNames 用户登录名列表
     * @return 用户信息列表
     */
    List<UserDTO> selectUsersByLoginName(@Param("organizationId") Long organizationId,
                                         @Param("loginNames") List<String> loginNames);

    /**
     * 通过用户ID获取员工在第三方平台的userid
     *
     * @param ids               用户ID
     * @param thirdPlatformType 三方平台类型
     * @return userid
     */
    List<String> getOpenUserIdByIds(List<Long> ids, String thirdPlatformType);
}
