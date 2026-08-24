package org.hzero.plugin.platform.hr.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.domain.entity.Position;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 工作流组织架构接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/13 星期一 15:46
 */
public interface HrService {

    /**
     * 查询员工的所有上级领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前员工的所有上级领导的员工编号
     */
    List<String> listEmployeeSuperiorNum(long tenantId, String employeeNum);

    /**
     * 查询员工的所有部门领导的员工编号
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @return 当前当前员工的所有部门领导的员工编号
     */
    List<String> listDepartmentSuperiorNum(long tenantId, String employeeNum);

    /**
     * 查询员工的所有上级部门领导的员工编号
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
    Map<String, Object> mapEmployeePositionCode(long tenantId, String employeeNum);

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
     * 根据岗位编码获取岗位信息
     *
     * @param tenantId     租户ID
     * @param positionCode 岗位编码
     * @return 岗位信息
     */
    Position queryPositionDetails(long tenantId, String positionCode);

    /**
     * 条件查询员工基本信息，支持分页
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页
     * @param employee    查询条件
     * @return 员工信息
     */
    Page<EmployeeDTO> pageEmployee(long tenantId, PageRequest pageRequest, EmployeeDTO employee);

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
     * 条件查询岗位基本信息，支持分页
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页
     * @param position    查询条件
     * @return 岗位信息
     */
    Page<Position> pagePosition(long tenantId, PageRequest pageRequest, Position position);

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
     * 根据多个员工编号查询对应的员工详情
     *
     * @param tenantId      租户ID
     * @param employeeNums  员工编号
     * @return 员工详情
     */
    List<EmployeeDTO> queryEmployeesByEmployeeNum(Long tenantId, List<String> employeeNums);
}
