package org.hzero.plugin.platform.hr.domain.service;

import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Position;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2019/1/14 星期一 17:37
 */
public interface IEmployeeAssignDomainService {
    /**
     * 返回不在该岗位下的员工列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @param name        姓名
     * @param unitId      组织ID
     * @param positionId  岗位ID
     * @param pageRequest 分页
     * @return 员工列表
     */
    Page<Employee> employeeNotInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest);

    /**
     * 返回在该岗位下的员工列表
     *
     * @param tenantId    租户ID
     * @param employeeNum 员工编号
     * @param name        姓名
     * @param unitId      组织ID
     * @param positionId  岗位ID
     * @param pageRequest 分页
     * @return 员工列表
     */
    Page<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest);

    /**
     * 主岗唯一性校验
     *
     * @param tenantId 租户Id
     * @param employeeId 员工Id
     */
    void validatePrimaryPositionFlagRepeat(Long tenantId, Long employeeId);
}
