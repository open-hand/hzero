package org.hzero.plugin.platform.hr.domain.repository;

import java.util.List;
import java.util.Set;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工岗位分配表资源库
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:44
 */
public interface EmployeeAssignRepository extends BaseRepository<EmployeeAssign> {
    /**
     * 查询员工分配的岗位列表
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     * @return 员工分配的岗位列表
     */
    List<EmployeeAssignDTO> selectEmployeeAssign(long tenantId, long employeeId);

    /**
     * 查询当前员工岗位列表
     *
     * @param userId              用户id
     * @param language            当前语言环境
     * @param tenantId            租户ID
     * @param primaryPositionFlag 主岗位标示
     * @return 当前员工岗位列表
     */
    List<EmployeeAssignDTO> selectEmployeeAssign(String language, Long tenantId, Integer primaryPositionFlag, Long userId);

    /**
     * 获取员工岗位分配表
     *
     * @param id 员工岗位分配ID
     * @return EmployeeAssign
     */
    EmployeeAssign get(Long id);

    /**
     * 创建员工岗位分配表
     *
     * @param employeeAssign 员工岗位分配
     */
    void create(EmployeeAssign employeeAssign);

    /**
     * 更新员工岗位分配表
     *
     * @param employeeAssign 员工岗位分配
     */
    void update(EmployeeAssign employeeAssign);

    /**
     * 删除员工岗位分配表
     *
     * @param employeeAssignId 员工岗位分配表
     */
    void remove(Long employeeAssignId);


    /**
     * 返回不在该岗位下的员工列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @param name        姓名
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
     * @param name        姓名
     * @param unitId      组织ID
     * @param positionId  岗位ID
     * @param pageRequest 分页条件
     * @return 员工列表
     */
    Page<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest);

    /**
     * 返回不在该岗位下的员工列表
     *
     * @param tenantId      租户ID
     * @param employeeNum   员工编码
     * @param name          员工名称
     * @param positionId    岗位ID
     * @return              返回值
     */
    List<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long positionId);



    /**
     * 根据组织ID和员工ID批量禁用岗位
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     */

    void batchDisableEmployeeAssign(Long tenantId, Long employeeId);

    /**
     * 根据员工租户ID和员工ID查询该用户的所有岗位分配信息
     *
     * @param tenantId    租户ID
     * @param employeeId  员工ID
     * @param enabledFlag 启用标记
     * @return 员工分配的岗位列表
     */
    List<EmployeeAssign> selectEmployeeAssignList(Long tenantId, Long employeeId, Integer enabledFlag);

    /**
     * 根据员工租户ID和员工ID查询该用户的所有岗位ID[启用状态的岗位]
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     * @return 岗位ID
     */
    List<Long> selectUnitIdList(Long tenantId, Long employeeId);

    /**
     * 查询员工分配
     *
     * @param employeeIds 员工ID集合
     * @return 员工分配
     */
    List<EmployeeAssignDTO> listRecentEmployeeAssign(Set<Long> employeeIds);

    /**
     * 更新缓存，先清除原有缓存再新增
     *
     * @param employeeId 员工Id
     * @param tenantId 租户Id
     */
    void updateEmpAssignCache(Long employeeId, Long tenantId);

    /**
     * 删除缓存
     *
     * @param employeeId 员工Id
     * @param tenantId 租户Id
     */
    void removeEmpAssignCache(Long employeeId, Long tenantId);

    /**
     * 查询部门下的员工,用于维护组织数据同步第三方映射关系
     *
     * @param tenantId    组织Id
     * @param unitIds     部门Id集合
     * @param enableFlag  启用标识
     * @return
     */
    List<Employee> employeeInDept(Long tenantId, List<Long> unitIds, Integer enableFlag);

    /**
     * 查询公司下的员工，用于维护组织数据同步第三方映射关系
     *
     * @param tenantId          组织Id
     * @param unitCompanyId     公司Id
     * @param enableFlag        启用标识
     * @return
     */
    List<Employee> employeeInCompany(Long tenantId, Long unitCompanyId, Integer enableFlag);
}
