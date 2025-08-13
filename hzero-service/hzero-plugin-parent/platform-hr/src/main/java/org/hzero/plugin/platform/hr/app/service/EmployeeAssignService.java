package org.hzero.plugin.platform.hr.app.service;

import java.util.List;

import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工岗位分配表应用服务
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:44
 */
public interface EmployeeAssignService {
    /**
     * 查询员工分配的岗位列表
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     * @return 员工分配的岗位列表
     */
    List<EmployeeAssignDTO> listEmployeeAssign(long tenantId, long employeeId);

    /**
     * 查询当前员工岗位列表
     *
     * @param tenantId            租户id
     * @param primaryPositionFlag 主岗位标示（非必须）
     * @return 当前员工岗位列表
     */
    List<EmployeeAssignDTO> listEmployeeAssign(Long tenantId, Integer primaryPositionFlag, Long userId, String language);


    /**
     * 更新员工岗位分配表
     *
     * @param employeeAssign 员工岗位分配表
     */
    void update(EmployeeAssign employeeAssign);

    /**
     * 删除事件
     *
     * @param employeeAssignId 员工岗位分配表ID not null.
     */
    void remove(Long employeeAssignId);

    /**
     * 批量更新员工岗位分配信息
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     * @param assignList 分配列表
     */
    void updateEmployeeAssign(Long tenantId, Long employeeId, List<EmployeeAssign> assignList);

    /**
     * 返回不在该岗位下的员工列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @param name        员工姓名
     * @param unitId      组织ID
     * @param positionId  岗位ID
     * @param pageRequest 分页条件
     * @return 员工列表
     */
    Page<Employee> employeeNotInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest);

    /**
     * 返回不在该岗位下的员工列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @param name        员工姓名
     * @param unitId      组织ID
     * @param positionId  岗位ID
     * @param pageRequest 分页条件
     * @return 员工列表
     */
    Page<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest);

    /**
     * 根据组织ID，岗位ID，添加员工
     *
     * @param tenantId       租户ID
     * @param unitId         部门ID
     * @param positionId     岗位ID
     * @param employeeIdList 员工ID列表
     */
    void batchCreateEmployee(Long tenantId, Long unitId, Long positionId, List<Long> employeeIdList);

    /**
     * 根据组织ID，岗位ID，删除员工
     *
     * @param tenantId       租户ID
     * @param unitId         部门ID
     * @param positionId     岗位ID
     * @param employeeIdList 员工ID列表
     */
    void batchDeleteEmployee(Long tenantId, Long unitId, Long positionId, List<Long> employeeIdList);

}
