package org.hzero.plugin.platform.hr.infra.repository.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hzero.boot.platform.plugin.hr.EmployeeHelper;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.infra.mapper.EmployeeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工表 资源库实现
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:43
 */
@Component
public class EmployeeRepositoryImpl extends BaseRepositoryImpl<Employee> implements EmployeeRepository {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public Employee get(Long id) {
        return this.selectByPrimaryKey(id);
    }

    @Override
    public void remove(Long employeeId) {
        this.deleteByPrimaryKey(employeeId);
    }

    @Override
    public Employee queryEmployeeByEmployeeNum(Long tenantId, String employeeNum) {
        Employee employee = new Employee();
        employee.setTenantId(tenantId);
        employee.setEmployeeNum(employeeNum);
        employee.setEnabledFlag(BaseConstants.Flag.YES);
        return selectOne(employee);
    }

    @Override
    public Page<Employee> listEmployee(Long tenantId, String employeeNum, String name, Integer enabledFlag, PageRequest pageRequest, Long userId) {

        return PageHelper.doPageAndSort(pageRequest, () -> employeeMapper.listEmployee(tenantId, employeeNum, name, enabledFlag, userId));
    }

    @Override
    public List<String> listEmployeeSuperiorNum(long tenantId, String employeeNum) {
        return employeeMapper.listEmployeeSuperiorNum(tenantId, employeeNum);
    }

    @Override
    public List<String> listDepartmentSuperiorNum(long tenantId, String employeeNum) {
        return employeeMapper.listDepartmentSuperiorNum(tenantId, employeeNum);
    }

    @Override
    public List<String> listSuperiorDepartmentSuperiorNum(long tenantId, String employeeNum) {
        return employeeMapper.listSuperiorDepartmentSuperiorNum(tenantId, employeeNum);
    }

    @Override
    public List<String> listPositionEmployeeNum(long tenantId, String positionCode) {
        return employeeMapper.listPositionEmployeeNum(tenantId, positionCode);
    }

    @Override
    public List<String> listEmployeePositionCode(long tenantId, String employeeNum) {
        return employeeMapper.listEmployeePositionCode(tenantId, employeeNum);
    }

    @Override
    public EmployeeDTO queryEmployeeDetail(long tenantId, String employeeNum) {
        return employeeMapper.queryEmployeeDetail(tenantId, employeeNum);
    }

    @Override
    public EmployeeDTO queryEmployeeAllDetail(long tenantId, String employeeNum) {
        return employeeMapper.queryEmployeeAllDetail(tenantId, employeeNum);
    }

    @Override
    public List<Position> listEmployeePosition(long tenantId, String employeeNum) {
        return employeeMapper.listEmployeePosition(tenantId, employeeNum);
    }

    @Override
    public Page<EmployeeDTO> pageEmployeeAllDetail(long tenantId, PageRequest pageRequest, EmployeeDTO employee) {
        return PageHelper.doPageAndSort(pageRequest, () -> employeeMapper.listEmployeeAllDetail(tenantId, employee.getEnabledFlag(), employee.getPositionId(), employee.getUnitId(), employee.getEmployeeCode(), employee.getName(), employee.getIdList()));
    }

    @Override
    public Page<EmployeeDTO> pageDepartmentEmployee(long tenantId, Long departmentId, String departmentCode, boolean includeChildDepartment, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> employeeMapper.listDepartmentEmployee(tenantId, departmentId, departmentCode, includeChildDepartment));
    }

    @Override
    public List<Employee> selectEmployee(Long tenantId) {
        Employee employee = new Employee();
        employee.setEnabledFlag(1);
        employee.setTenantId(tenantId);
        return employeeMapper.select(employee);
    }

    @Override
    public List<Employee> listRecentEmployee(Long tenantId, Date after) {
        return employeeMapper.selectByCondition(Condition.builder(Employee.class)
                .andWhere(Sqls.custom().andEqualTo(Employee.FIELD_TENANT_ID, tenantId, true)
                        .andGreaterThan(Employee.FIELD_LAST_UPDATE_DATE, after))
                .build());
    }

    @Override
    public int updateLastUpdateTime(List<Long> employeeIds) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return 0;
        }
        return employeeMapper.updateLastUpdateTime(employeeIds, new Date());
    }

    @Override
    public List<EmployeeDTO> selectEmployeesByEmployeeNum(Long tenantId, List<String> employeeNums) {
        if (CollectionUtils.isEmpty(employeeNums)) {
            return Collections.emptyList();
        } else {
            return employeeMapper.selectEmployeesByEmployeeNum(tenantId, employeeNums);
        }
    }

    @Override
    public void saveCache(Employee employee) {
        EmployeeHelper.setRedisHelper(redisHelper);
        if (BaseConstants.Flag.YES.equals(employee.getEnabledFlag())) {
            org.hzero.boot.platform.plugin.hr.entity.Employee cache = new org.hzero.boot.platform.plugin.hr.entity.Employee();
            BeanUtils.copyProperties(employee, cache);
            EmployeeHelper.storeEmployee(cache);
        } else {
            deleteCache(employee);
        }
    }

    @Override
    public void deleteCache(Employee employee) {
        EmployeeHelper.deleteEmployee(employee.getEmployeeId(), employee.getTenantId());
    }

    @Override
    public Page<Employee> doPageAndSort(PageRequest pageRequest, Employee queryParam) {
        return PageHelper.doPageAndSort(pageRequest, () -> employeeMapper.queryEmployeeByCondition(queryParam));
    }

    @Override
    public EmployeeDTO selectEmployeeByEmployeeId(Long tenantId, Long employeeId) {
        return employeeMapper.selectEmployeeByEmployeeId(tenantId, employeeId);
    }

    @Override
    public List<String> listEmployeeByRoleId(Long tenantId, Long roleId) {
        return employeeMapper.queryEmployeeByRoleId(tenantId, roleId);
    }
}
