package org.hzero.plugin.platform.hr.domain.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeAssignRepository;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.domain.service.IEmployeeAssignDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import java.util.Collections;
import java.util.List;

/**
 * description
 *
 * @author liang.jin@hand-china.com 2018/06/26 9:22
 */
@Service
public class EmployeeAssignDomainService implements IEmployeeAssignDomainService {
    private EmployeeAssignRepository employeeAssignRepository;
    private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeAssignDomainService(EmployeeAssignRepository employeeAssignRepository,
            EmployeeRepository employeeRepository) {
        this.employeeAssignRepository = employeeAssignRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * 返回不在该岗位下的员工列表
     *
     * @param employeeNum 员工编号
     * @param name        姓名
     * @param unitId      组织ID
     * @param positionId  岗位ID
     */
    @Override
    public Page<Employee> employeeNotInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest) {
        return employeeAssignRepository.employeeNotInPosition(tenantId, employeeNum, name, unitId, positionId, pageRequest);
    }

    /**
     * 返回在该岗位下的员工列表
     *
     * @param employeeNum 员工编号
     * @param name        姓名
     * @param unitId      组织ID
     * @param positionId  岗位ID
     */
    @Override
    public Page<Employee> employeeInPosition(Long tenantId, String employeeNum, String name, Long unitId, Long positionId, PageRequest pageRequest) {
        return employeeAssignRepository.employeeInPosition(tenantId, employeeNum, name, unitId, positionId, pageRequest);
    }


    @Override
    public void validatePrimaryPositionFlagRepeat(Long tenantId, Long employeeId) {
        EmployeeAssign temp = new EmployeeAssign(employeeId, tenantId, BaseConstants.Flag.YES, BaseConstants.Flag.YES);
        List<EmployeeAssign> employeeAssignList = employeeAssignRepository.select(temp);
        if (CollectionUtils.isEmpty(employeeAssignList)) {
            // 查询非主岗的数据，若存在则设置第一个岗位为主管岗位
            temp.setPrimaryPositionFlag(BaseConstants.Flag.NO);
            List<EmployeeAssign> employeeAssigns = employeeAssignRepository.select(temp);
            if (CollectionUtils.isNotEmpty(employeeAssigns)) {
                //设置第一个岗位为主岗
                EmployeeAssign employeeAssign = employeeAssigns.get(0);
                employeeAssign.setPrimaryPositionFlag(BaseConstants.Flag.YES);
                employeeRepository.updateLastUpdateTime(Collections.singletonList(employeeId));
                employeeAssignRepository.updateByPrimaryKeySelective(employeeAssign);
                // 恢复查询条件
                temp.setPrimaryPositionFlag(BaseConstants.Flag.YES);
                employeeAssignList = employeeAssignRepository.select(temp);
            } else {
                return;
            }
        }
        if (employeeAssignList.size() != 1) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

}
