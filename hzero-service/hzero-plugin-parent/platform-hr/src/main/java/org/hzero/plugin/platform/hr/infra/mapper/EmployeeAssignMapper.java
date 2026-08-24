package org.hzero.plugin.platform.hr.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;
import org.hzero.plugin.platform.hr.domain.vo.EmployeeAssignVO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 员工岗位分配表Mapper
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:44
 */
public interface EmployeeAssignMapper extends BaseMapper<EmployeeAssign> {
    /**
     * 查询员工分配的岗位列表
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     * @return 员工分配的岗位列表
     */
    List<EmployeeAssignDTO> selectEmployeeAssign(@Param("tenantId") long tenantId, @Param("employeeId") long employeeId);

    /**
     * 查询当前员工岗位列表
     *
     * @param userId              用户id
     * @param language            当前语言环境
     * @param tenantId            租户ID
     * @param primaryPositionFlag 主岗位标示
     * @return 当前员工岗位列表
     */
    List<EmployeeAssignDTO> hcbmSelectEmployeeAssign(@Param("language") String language,
                                                     @Param("tenantId") Long tenantId,
                                                     @Param("primaryPositionFlag") Integer primaryPositionFlag,
                                                     @Param("userId") Long userId);

    /**
     * 返回不在该岗位下的员工列表
     *
     * @param employeeNum 员工编号
     * @param name        姓名
     * @param positionId  岗位ID
     * @param tenantId    租户ID
     * @return 员工列表
     */
    List<Employee> employeeNotInPosition(@Param("employeeNum") String employeeNum, @Param("name") String name, @Param("positionId") Long positionId, @Param("tenantId") Long tenantId);

    /**
     * 返回在该岗位下的员工列表
     *
     * @param employeeNum 员工编号
     * @param name        姓名
     * @param positionId  岗位ID
     * @param tenantId    租户ID
     * @return 员工列表
     */
    List<Employee> employeeInPosition(@Param("employeeNum") String employeeNum, @Param("name") String name, @Param("positionId") Long positionId, @Param("tenantId") Long tenantId);


    /**
     * 根据组织ID和员工ID批量禁用岗位
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     */
    void batchDisableEmployeeAssign(@Param("tenantId") Long tenantId, @Param("employeeId") Long employeeId);


    /**
     * 根据员工租户ID和员工ID查询该用户的所有岗位分配信息
     *
     * @param tenantId    租户ID
     * @param enabledFlag 启用标记
     * @param employeeId  员工ID
     * @return 岗位分配信息
     */
    List<EmployeeAssign> selectEmployeeAssignList(@Param("tenantId") Long tenantId, @Param("employeeId") Long employeeId, @Param("enabledFlag") Integer enabledFlag);


    /**
     * 根据员工租户ID和员工ID查询该用户的所有岗位ID
     *
     * @param tenantId   租户ID
     * @param employeeId 员工ID
     * @return 岗位ID
     */
    List<Long> selectUnitIdList(@Param("tenantId") Long tenantId, @Param("employeeId") Long employeeId);

    /**
     * 查询员工分配
     *
     * @param employeeIds 员工ID集合
     * @return 员工分配
     */
    List<EmployeeAssignDTO> listRecentEmployeeAssign(@Param("employeeIds") Set<Long> employeeIds);

    /**
     * 获取缓存所需参数
     *
     * @param employeeId 员工Id
     * @param tenantId 租户Id
     * @return List<EmployeeAssignVO>
     */
    List<EmployeeAssignVO> selectEmployeeAssignForCache(@Param("employeeId") Long employeeId,
            @Param("tenantId") Long tenantId);

    /**
     * 查询部门下的员工
     *
     * @param tenantId    组织Id
     * @param unitIds     部门Id集合
     * @param enableFlag  启用标识
     * @return
     */
    List<Employee> employeeInDept(@Param("tenantId") Long tenantId,
                                  @Param("unitIds") List<Long> unitIds,
                                  @Param("enableFlag") Integer enableFlag);

    /**
     * 查询公司下的员工
     *
     * @param tenantId          组织Id
     * @param unitCompanyId     公司Id
     * @param enableFlag        启用标识
     * @return
     */
    List<Employee> employeeInCompany(@Param("tenantId") Long tenantId,
                                  @Param("unitCompanyId") Long unitCompanyId,
                                  @Param("enableFlag") Integer enableFlag);
}
