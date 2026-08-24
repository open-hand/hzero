package org.hzero.plugin.platform.hr.domain.repository;

import java.util.Date;
import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Position;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工表资源库
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:43
 */
public interface EmployeeRepository extends BaseRepository<Employee> {

    /**
     * 获取员工表
     *
     * @param id Employee ID
     * @return Employee
     */
    Employee get(Long id);

    /**
     * 删除员工表
     *
     * @param employeeId 员工表
     */
    void remove(Long employeeId);

    /**
     * 通过员工编号查询员工
     *
     * @param tenantId 租户ID
     * @param employeeNum 员工编号
     * @return 员工
     */
    Employee queryEmployeeByEmployeeNum(Long tenantId, String employeeNum);

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
     * 查询当前员工的所有上级领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前员工的所有上级领导的员工编号
     */
    List<String> listEmployeeSuperiorNum(long tenantId, String employeeNum);

    /**
     * 查询当前员工的所有部门领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前当前员工的所有部门领导的员工编号
     */
    List<String> listDepartmentSuperiorNum(long tenantId, String employeeNum);

    /**
     * 查询当前员工的所有上级部门领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前当前员工的所有部门领导的员工编号
     */
    List<String> listSuperiorDepartmentSuperiorNum(long tenantId, String employeeNum);

    /**
     * 查询岗位下的员工编号列表
     *
     * @param tenantId     租户ID
     * @param positionCode 岗位编号
     * @return 当前岗位下的员工编号列表
     */
    List<String> listPositionEmployeeNum(long tenantId, String positionCode);

    /**
     * 查询员工分配的岗位列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工分配的岗位列表
     */
    List<String> listEmployeePositionCode(long tenantId, String employeeNum);

    /**
     * 查询员工详情
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工详情
     */
    EmployeeDTO queryEmployeeDetail(long tenantId, String employeeNum);

    /**
     * 根据员工编号查询员工详情信息，包含员工所在岗位，部门
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工详情
     */
    EmployeeDTO queryEmployeeAllDetail(long tenantId, String employeeNum);

    /**
     * 根据员工编号获取岗位信息列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 岗位信息列表
     */
    List<Position> listEmployeePosition(long tenantId, String employeeNum);

    /**
     * 条件查询员工详细信息，支持分页
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页
     * @param employee    查询条件
     * @return 员工信息
     */
    Page<EmployeeDTO> pageEmployeeAllDetail(long tenantId, PageRequest pageRequest, EmployeeDTO employee);

    /**
     * 查询部门下的员工
     *
     * @param tenantId               租户ID
     * @param departmentId           部门ID
     * @param departmentCode         部门编码
     * @param includeChildDepartment 是否包含子部门
     * @param pageRequest            分页
     * @return 查询部门下的员工
     */
    Page<EmployeeDTO> pageDepartmentEmployee(long tenantId, Long departmentId, String departmentCode, boolean includeChildDepartment, PageRequest pageRequest);

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
     * @param after    这个时间点之后
     * @return 最近更新过的记录
     */
    List<Employee> listRecentEmployee(Long tenantId, Date after);

    /**
     * 更新最后更新时间
     *
     * @param employeeIds 员工ID集合
     * @return 更新记录数量
     */
    int updateLastUpdateTime(List<Long> employeeIds);

    /**
     * 根据多个员工编号查询对应的员工详情
     *
     * @param tenantId     租户ID
     * @param employeeNums 员工编号
     * @return 员工详情
     */
    List<EmployeeDTO> selectEmployeesByEmployeeNum(Long tenantId, List<String> employeeNums);

    /**
     * 缓存员工信息，仅员工编码和员工名称
     *
     * @param employee 员工信息
     */
    void saveCache(Employee employee);

    /**
     * 删除缓存
     *
     * @param employee 员工信息
     */
    void deleteCache(Employee employee);

    /**
     * 模糊分页查询
     * @param pageRequest 分页请求对象
     * @param queryParam 查询条件对象
     * @return 分页员工集合
     */
    Page<Employee> doPageAndSort(PageRequest pageRequest, Employee queryParam);
    
    /**
     * 查询员工详情
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工详情
     */
    EmployeeDTO selectEmployeeByEmployeeId(Long tenantId, Long employeeId);

    /**
     * 查询指定角色关联的员工列表
     * @param tenantId
     * @param roleId
     * @return
     */
    List<String> listEmployeeByRoleId(Long tenantId, Long roleId);
}
