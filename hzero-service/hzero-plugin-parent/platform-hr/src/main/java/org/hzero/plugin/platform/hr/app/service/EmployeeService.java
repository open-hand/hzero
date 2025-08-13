package org.hzero.plugin.platform.hr.app.service;

import java.util.List;

import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工表应用服务
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:43
 */
public interface EmployeeService {
    /**
     * 批量删除员工
     *
     * @param employeeList 员工列表
     */
    void batchRemove(List<Employee> employeeList);

    /**
     * 根据条件查询员工列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 　员工编号
     * @param name        　姓名
     * @param enabledFlag 有效标识
     * @param pageRequest 　分页条件
     * @param userId 　用户Id
     * @return 所有的员工
     */
    Page<Employee> listEmployee(Long tenantId, String employeeNum, String name, Integer enabledFlag, PageRequest pageRequest, Long userId);

    /**
     * 通过员工编码查询员工
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编码
     * @return 员工
     */
    Employee queryEmployee(Long tenantId, String employeeNum);

    /**
     * 通过员工ID查询员工
     *
     * @param tenantId    租户ID
     * @param employeeId 员工ID
     * @param enabledFlag 是否启用
     * @return 员工
     */
    Employee queryEmployee(Long tenantId, long employeeId, Integer enabledFlag);

    /**
     * 根据员工ID获取员工信息列表
     *
     * @param organizationId 租户ID
     * @param employeeIds    员工ID列表
     * @return 员工信息列表
     */
    List<Employee> listEmployeeByIds(Long organizationId, List<Long> employeeIds);

    /**
     * 批量创建员工
     *
     * @param tenantId     租户ID
     * @param employeeList 员工列表
     * @return 员工列表
     */
    List<Employee> createOrUpdateEmployee(Long tenantId, List<Employee> employeeList);

    /**
     * 更新员工信息和主岗信息
     *
     * @param tenantId   租户ID
     * @param positionId 岗位ID
     * @param employee   员工
     * @return 员工
     */
    Employee updateEmployeeAndAssign(Long tenantId, Long positionId, Employee employee);

    /**
     * 员工信息查询
     *
     * @param tenantId 租户ID
     * @return List<EmployeeDTO>
     */
    List<Employee> selectEmployee(Long tenantId);

    /**
     * 拉取最近更新过的记录
     *
     * @param tenantId 租户ID
     * @param before   过去多久内(单位：ms，默认5min)
     * @return 最近更新过的记录
     */
    List<Employee> listRecentEmployee(Long tenantId, long before);
    
    /**
     * 企业通讯录-新增员工信息
     * @param organizationId
     * @param employeeDTO
     * @return
     */
    EmployeeDTO insertEmployeeDetail(Long organizationId,EmployeeDTO employeeDTO);
    
    /**
     * 企业通讯录-编辑员工详情
     * @param organizationId
     * @param employeeId
     * @param employeeDTO (Employee)
     * @return
     */
    EmployeeDTO updateEmployeeDetail(Long organizationId,EmployeeDTO employeeDTO);
    
    /**
     * 企业通讯录-查询员工详情
     * @param organizationId
     * @param employeeId
     * @return
     */
    EmployeeDTO queryEmployeeDetail(Long organizationId,Long employeeId);

    /**
     * 查询指定角色关联的员工列表
     * @param tenantId
     * @param roleId
     * @return
     */
    List<String> listEmployeeByRoleCodeId(Long tenantId, Long roleId);
}
