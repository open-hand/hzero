package org.hzero.plugin.platform.hr.app.service;

import org.hzero.plugin.platform.hr.api.dto.EmployeeUserDTO;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeUser;

import java.util.List;

/**
 * 员工用户关系应用服务
 *
 * @author like.zhang@hand-china.com 2018-09-17 11:20:55
 */
public interface EmployeeUserService {
    /**
     * 查询用户在某个租户下的员工信息
     *
     * @param userId      用户ID
     * @param tenantId    租户ID
     * @param enabledFlag 是否启用
     * @return 员工信息
     */
    EmployeeUserDTO getEmployee(long userId, long tenantId, Integer enabledFlag);

    /**
     * 批量查询查询用户在某个租户下的员工信息
     *
     * @param userIdList 用户ID列表
     * @param tenantId   租户ID
     * @return 员工信息
     */
    List<EmployeeUserDTO> listEmployee(List<Long> userIdList, long tenantId);

    /**
     * 员工-用户关系批量新增
     *
     * @param employeeUsers 员工用户关系列表
     * @return 员工列表
     */
    List<EmployeeUser> batchInsertEmployees(List<EmployeeUser> employeeUsers);

    /**
     * 批量删除员工-用户关系
     *
     * @param organizationId 租户id
     * @param employeeUsers  员工用户关系列表
     */
    void batchRemoveEmployeeUsers(Long organizationId, List<EmployeeUser> employeeUsers);

    /**
     * 通过用户ID获取员工在第三方平台的userid
     *
     * @param ids               用户ID
     * @param thirdPlatformType 三方平台类型
     * @return userid
     */
    List<String> getOpenUserIdByIds(List<Long> ids, String thirdPlatformType);
}
