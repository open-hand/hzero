package org.hzero.plugin.platform.hr.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.api.dto.EmployeeUserDTO;
import org.hzero.plugin.platform.hr.api.dto.UserDTO;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeUser;

import java.util.List;
import java.util.Set;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 员工用户关系Mapper
 *
 * @author like.zhang@hand-china.com 2018-09-17 11:20:55
 */
public interface EmployeeUserMapper extends BaseMapper<EmployeeUser> {

    /**
     * 查询员工用户关系
     *
     * @param tenantId   租户id
     * @param employeeId 员工id
     * @return 员工关系列表
     */
    List<EmployeeUserDTO> selectEmployeeUserDTO(@Param("tenantId") Long tenantId, @Param("employeeId") Long employeeId);

    /**
     * 员工用户关联
     *
     * @param ids 主键
     * @return 员工用户
     */
    List<EmployeeUserDTO> selectByPrimaryKeys(@Param("ids") Set<Long> ids);

    /**
     * 查询员工
     *
     * @param userId      用户ID
     * @param tenantId    租户ID
     * @param enabledFlag 是否启用
     * @return 员工
     */
    EmployeeUserDTO getEmployee(@Param("userId") long userId, @Param("tenantId") long tenantId, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询员工
     *
     * @param userIdList 用户ID
     * @param tenantId   租户ID
     * @return 员工
     */
    List<EmployeeUserDTO> listEmployee(@Param("userIdList") List<Long> userIdList, @Param("tenantId") long tenantId);

    /**
     * 通过传入的员工编码查询所有有关联的员工信息
     *
     * @param tenantId 租户id
     * @param employeeNums 员工编码
     * @return List<EmployeeUserDTO>
     */
    List<EmployeeUserDTO> selectEmpUsersByEmpNum(@Param("tenantId") Long tenantId, @Param("employeeNums") List<String> employeeNums);

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
    List<String> selectOpenUserIdsByUserIds(@Param("ids") List<Long> ids,@Param("thirdPlatformType") String thirdPlatformType);
}
