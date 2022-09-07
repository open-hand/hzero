package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Position;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 员工表Mapper
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:43
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 根据条件查询所有员工
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @param name        　姓名
     * @param enabledFlag 有效标识
     * @return 所有的员工
     */
    Page<Employee> listEmployee(@Param("tenantId") Long tenantId,
                                @Param("employeeNum") String employeeNum,
                                @Param("name") String name,
                                @Param("enabledFlag") Integer enabledFlag,
                                @Param("userId") Long userId);

    /**
     * 查询当前员工的所有上级领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前员工的所有上级领导的员工编号
     */
    List<String> listEmployeeSuperiorNum(@Param("tenantId") long tenantId, @Param("employeeNum") String employeeNum);

    /**
     * 查询当前员工的所有部门领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前当前员工的所有部门领导的员工编号
     */
    List<String> listDepartmentSuperiorNum(@Param("tenantId") long tenantId, @Param("employeeNum") String employeeNum);

    /**
     * 查询当前员工的所有上级部门领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前当前员工的所有部门领导的员工编号
     */
    List<String> listSuperiorDepartmentSuperiorNum(@Param("tenantId") long tenantId, @Param("employeeNum") String employeeNum);

    /**
     * 查询岗位下的员工编号列表
     *
     * @param tenantId     租户ID
     * @param positionCode 岗位编号
     * @return 当前岗位下的员工编号列表
     */
    List<String> listPositionEmployeeNum(@Param("tenantId") long tenantId, @Param("positionCode") String positionCode);

    /**
     * 查询员工分配的岗位列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工分配的岗位列表
     */
    List<String> listEmployeePositionCode(@Param("tenantId") long tenantId, @Param("employeeNum") String employeeNum);

    /**
     * 查询员工详情
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工详情
     */
    EmployeeDTO queryEmployeeDetail(@Param("tenantId") long tenantId, @Param("employeeNum") String employeeNum);

    /**
     * 根据员工编号查询员工详情信息，包含员工所在岗位，部门
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工详情
     */
    EmployeeDTO queryEmployeeAllDetail(@Param("tenantId") long tenantId, @Param("employeeNum") String employeeNum);

    /**
     * 根据员工编号获取岗位信息列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 岗位信息列表
     */
    List<Position> listEmployeePosition(@Param("tenantId") long tenantId, @Param("employeeNum") String employeeNum);

    /**
     * 查询员工所有信息
     *
     * @param tenantId    租户ID
     * @param enabledFlag 启用标记
     * @param positionId  岗位ID
     * @param unitId      部门ID
     * @param employeeNum 员工编码
     * @param name        员工名称
     * @param idList      员工ID列表
     * @return 员工所有信息
     */
    List<EmployeeDTO> listEmployeeAllDetail(@Param("tenantId") long tenantId,
                                            @Param("enabledFlag") String enabledFlag,
                                            @Param("positionId") Long positionId,
                                            @Param("unitId") Long unitId,
                                            @Param("employeeNum") String employeeNum,
                                            @Param("name") String name,
                                            @Param("idList") List<Long> idList);

    /**
     * 查询部门下的员工
     *
     * @param tenantId               租户ID
     * @param departmentId           部门ID
     * @param departmentCode         部门编码
     * @param includeChildDepartment 是否包含子部门
     * @return 部门下的员工
     */
    List<EmployeeDTO> listDepartmentEmployee(@Param("tenantId") long tenantId,
                                             @Param("departmentId") Long departmentId,
                                             @Param("departmentCode") String departmentCode,
                                             @Param("includeChildDepartment") boolean includeChildDepartment);

    /**
     * 更新最后更新时间
     *
     * @param employeeIds 员工ID集合
     * @param now         当前时间
     * @return 更新记录数量
     */
    int updateLastUpdateTime(@Param("employeeIds") List<Long> employeeIds, @Param("now") Date now);

    /**
     * 根据多个员工编号查询对应的员工详情
     *
     * @param tenantId     租户ID
     * @param employeeNums 员工编号
     * @return 员工详情
     */
    List<EmployeeDTO> selectEmployeesByEmployeeNum(@Param("tenantId") Long tenantId, @Param("employeeNums") List<String> employeeNums);

    /**
     * 查询员工详情
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 员工详情
     */
    EmployeeDTO selectEmployeeByEmployeeId(@Param("tenantId") Long tenantId, @Param("employeeId") Long employeeId);

    /**
     * 查询指定角色关联的员工列表
     *
     * @param tenantId
     * @param roleId
     * @return
     */
    List<String> queryEmployeeByRoleId(Long tenantId, Long roleId);

    /**
     * 根据条件查询员工
     *
     * @param employee
     * @return 员工详情列表，包含员工所属公司
     */
    List<Employee> queryEmployeeByCondition(Employee employee);
}
