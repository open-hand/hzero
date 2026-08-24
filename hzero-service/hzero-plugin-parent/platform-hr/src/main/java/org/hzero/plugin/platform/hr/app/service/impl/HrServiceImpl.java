package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.app.service.HrService;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 工作流组织架构接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/8/13 星期一 15:47
 */
@Service
public class HrServiceImpl implements HrService {
    private EmployeeRepository employeeRepository;
    private PositionRepository positionRepository;
    private static final String EMPLOY_KEY = "employ";
    private static final String POSITION_KEY = "position";

    @Autowired
    public HrServiceImpl(EmployeeRepository employeeRepository,
                         PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public List<String> listEmployeeSuperiorNum(long tenantId, String employeeNum) {
        return employeeRepository.listEmployeeSuperiorNum(tenantId, employeeNum);
    }

    @Override
    public List<String> listDepartmentSuperiorNum(long tenantId, String employeeNum) {
        return employeeRepository.listDepartmentSuperiorNum(tenantId, employeeNum);
    }

    @Override
    public List<String> listSuperiorDepartmentSuperiorNum(long tenantId, String employeeNum) {
        return employeeRepository.listSuperiorDepartmentSuperiorNum(tenantId, employeeNum);
    }

    @Override
    public List<String> listPositionEmployeeNum(long tenantId, String positionCode) {
        return employeeRepository.listPositionEmployeeNum(tenantId, positionCode);
    }

    @Override
    public Map<String, Object> mapEmployeePositionCode(long tenantId, String employeeNum) {
        Map<String, Object> result = new HashMap<>(2);
        result.put(EMPLOY_KEY, employeeNum);
        result.put(POSITION_KEY, employeeRepository.listEmployeePositionCode(tenantId, employeeNum));
        return result;
    }

    @Override
    public EmployeeDTO queryEmployeeDetail(long tenantId, String employeeNum) {
        return employeeRepository.queryEmployeeDetail(tenantId, employeeNum);
    }

    @Override
    public EmployeeDTO queryEmployeeAllDetail(long tenantId, String employeeNum) {
        return employeeRepository.queryEmployeeAllDetail(tenantId, employeeNum);
    }

    @Override
    public List<Position> listEmployeePosition(long tenantId, String employeeNum) {
        return employeeRepository.listEmployeePosition(tenantId, employeeNum);
    }

    @Override
    public Position queryPositionDetails(long tenantId, String positionCode) {
        return positionRepository.selectOne(new Position().setTenantId(tenantId).setPositionCode(positionCode).setEnabledFlag(BaseConstants.Flag.YES));
    }

    @Override
    public Page<EmployeeDTO> pageEmployee(long tenantId, PageRequest pageRequest, EmployeeDTO employee) {
        Employee condition = new Employee();
        BeanUtils.copyProperties(employee, condition);
        condition.setEmployeeNum(employee.getEmployeeCode());
        condition.setTenantId(tenantId);
        // 只查询启用员工
        condition.setEnabledFlag(BaseConstants.Flag.YES);
        Page<Employee> employeePage = employeeRepository.doPageAndSort(pageRequest, condition);
        Page<EmployeeDTO> result = new Page<>();
        BeanUtils.copyProperties(employeePage, result, "content");
        result.setContent(employeePage.getContent().stream().map(item -> {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            BeanUtils.copyProperties(item, employeeDTO);
            return employeeDTO.setEmployeeCode(item.getEmployeeNum());
        }).collect(Collectors.toList()));
        return result;

    }

    @Override
    public Page<EmployeeDTO> pageEmployeeAllDetail(long tenantId, PageRequest pageRequest, EmployeeDTO employee) {
        return employeeRepository.pageEmployeeAllDetail(tenantId, pageRequest, employee);
    }

    @Override
    public Page<Position> pagePosition(long tenantId, PageRequest pageRequest, Position position) {
        return positionRepository.pagePosition(pageRequest, position.setTenantId(tenantId));
    }

    @Override
    public Page<EmployeeDTO> pageDepartmentEmployee(long tenantId, Long departmentId, String departmentCode, boolean includeChildDepartment, PageRequest pageRequest) {
        return employeeRepository.pageDepartmentEmployee(tenantId, departmentId, departmentCode, includeChildDepartment, pageRequest);
    }


    @Override
    public List<EmployeeDTO> queryEmployeesByEmployeeNum(Long tenantId, List<String> employeeNums) {
        return employeeRepository.selectEmployeesByEmployeeNum(tenantId, employeeNums);
    }
}
