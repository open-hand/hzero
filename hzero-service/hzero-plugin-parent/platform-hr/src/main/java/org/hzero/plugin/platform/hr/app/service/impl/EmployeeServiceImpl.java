package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.EmployeeUserDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitAssignDTO;
import org.hzero.plugin.platform.hr.app.service.EmployeeAssignService;
import org.hzero.plugin.platform.hr.app.service.EmployeeService;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.plugin.platform.hr.domain.repository.*;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.plugin.platform.hr.infra.feign.HiamUserRemoteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 员工表应用服务默认实现
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:43
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository;
    private EmployeeAssignRepository employeeAssignRepository;
    private EmployeeAssignService employeeAssignService;
    private UnitRepository unitRepository;
    private PositionRepository positionRepository;
    private HiamUserRemoteService userRemoteService;
    private EmployeeUserRepository userRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeAssignRepository employeeAssignRepository,
            EmployeeAssignService employeeAssignService, UnitRepository unitRepository,
            PositionRepository positionRepository, HiamUserRemoteService userRemoteService,
            EmployeeUserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeAssignRepository = employeeAssignRepository;
        this.employeeAssignService = employeeAssignService;
        this.unitRepository = unitRepository;
        this.positionRepository = positionRepository;
        this.userRemoteService = userRemoteService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemove(List<Employee> employeeList) {
        if (employeeRepository.batchDeleteByPrimaryKey(employeeList) != employeeList.size()) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        }
        employeeList.forEach(employee -> employeeRepository.deleteCache(employee));
    }

    @Override
    public Page<Employee> listEmployee(Long tenantId, String employeeNum, String name, Integer enabledFlag, PageRequest pageRequest, Long userId) {
        return employeeRepository.listEmployee(tenantId, employeeNum, name, enabledFlag, pageRequest, userId);
    }

    @Override
    public Employee queryEmployee(Long tenantId, String employeeNum) {
        return employeeRepository.queryEmployeeByEmployeeNum(tenantId, employeeNum);
    }

    @Override
    public Employee queryEmployee(Long tenantId, long employeeId, Integer enabledFlag) {
        Employee employee = new Employee();
        employee.setTenantId(tenantId);
        employee.setEmployeeId(employeeId);
        employee.setEnabledFlag(enabledFlag);
        return employeeRepository.selectOne(employee);
    }

    @Override
    public List<Employee> listEmployeeByIds(Long organizationId, List<Long> employeeIds) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }
        return employeeRepository.selectByCondition(Condition.builder(Employee.class)
                .notSelect(Employee.FIELD_LAST_UPDATED_BY, Employee.FIELD_LAST_UPDATE_DATE, Employee.FIELD_CREATION_DATE, Employee.FIELD_CREATED_BY)
                .andWhere(Sqls.custom().andEqualTo(Employee.FIELD_TENANT_ID, organizationId)
                        .andIn(Employee.FIELD_EMPLOYEE_ID, employeeIds)
                        .andEqualTo(Employee.FIELD_ENABLE_FLAG, BaseConstants.Flag.YES))
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Employee updateEmployeeAndAssign(Long tenantId, Long positionId, Employee employee) {
        // 查询当前数据库员工信息
        Employee currentEmployee = employeeRepository.get(employee.getEmployeeId());
        if (PlatformHrConstants.EmployeeStatus.LEAVE.equals(currentEmployee.getStatus())) {
            // 如果当前为离职状态则只能修改状态
            if (!PlatformHrConstants.EmployeeStatus.LEAVE.equals(employee.getStatus())) {
                currentEmployee.setStatus(employee.getStatus());
                employeeRepository.updateOptional(currentEmployee, Employee.FIELD_STATUS);
                employeeRepository.saveCache(employeeRepository.selectByPrimaryKey(currentEmployee.getEmployeeId()));
            }
            return currentEmployee;
        }

        // 检查快速索引和拼音信息是否存在，不存在则生成
        employee.generateQuickIndexAndPinyin();
        //更新员工信息
        employeeRepository.updateOptional(employee, Employee.FIELD_EMAIL, Employee.FIELD_NAME,
                Employee.FIELD_MOBILE, Employee.FIELD_ENABLE_FLAG, Employee.FIELD_QUICT_INDEX,
                Employee.FIELD_PHONETICIZE, Employee.FIELD_STATUS, Employee.FIELD_ENTRY_DATE);
        employeeRepository.saveCache(employeeRepository.selectByPrimaryKey(employee.getEmployeeId()));
        if (positionId != null) {
            // 处理更新主岗信息逻辑
            //更新主岗信息
            EmployeeAssign employeeAssign = new EmployeeAssign();
            employeeAssign.setEmployeeId(employee.getEmployeeId());
            employeeAssign.setPrimaryPositionFlag(BaseConstants.Flag.YES);
            employeeAssign.setEnabledFlag(BaseConstants.Flag.YES);
            List<EmployeeAssign> employeeAssigns = employeeAssignRepository.select(employeeAssign);
            if (CollectionUtils.isNotEmpty(employeeAssigns) && !employeeAssigns.get(0).getPositionId().equals(positionId)) {
                //如果主岗信息变更则更新主岗信息
                //更新主岗信息
                employeeAssigns.forEach(em -> em.setPrimaryPositionFlag(BaseConstants.Flag.NO));
                EmployeeAssign temp = new EmployeeAssign();
                temp.setEmployeeId(employee.getEmployeeId());
                temp.setTenantId(tenantId);
                temp.setPositionId(positionId);
                EmployeeAssign assign = employeeAssignRepository.selectOne(temp);
                assign.setPrimaryPositionFlag(BaseConstants.Flag.YES);
                List<EmployeeAssign> employeeAssignList = new ArrayList<>(employeeAssigns);
                employeeAssignList.add(assign);
                employeeAssignRepository.batchUpdateByPrimaryKeySelective(employeeAssignList);

            }
        }
        // 20200317 添加员工离职时锁定关联用户
        if (PlatformHrConstants.EmployeeStatus.LEAVE.equals(employee.getStatus())) {
            List<EmployeeUserDTO> userList = userRepository.listByEmployeeId(employee.getTenantId(), employee.getEmployeeId());
            if (CollectionUtils.isNotEmpty(userList)) {
                // 锁定员工下关联的账号
                userList.forEach(x -> {
                    // 非当前租户下的用户不允许锁定
                    if (tenantId.equals(x.getOrganizationId())) {
                        userRemoteService.lockUser(x.getOrganizationId(), x.getUserId());
                    }
                });
            }
        }
        return employee;
    }

    @Override
    public List<Employee> selectEmployee(Long tenantId) {
        return employeeRepository.selectEmployee(tenantId);
    }

    @Override
    public List<Employee> listRecentEmployee(Long tenantId, long before) {
        List<Employee> employeeList = employeeRepository.listRecentEmployee(tenantId, new Date(System.currentTimeMillis() - before));
        Map<Long, List<EmployeeAssignDTO>> employeeAssignMap = employeeAssignRepository.listRecentEmployeeAssign(employeeList
                .stream().map(Employee::getEmployeeId).collect(Collectors.toSet()))
                .stream().collect(Collectors.groupingBy(EmployeeAssignDTO::getEmployeeId));
        for (Employee employee : employeeList) {
            employee.setEmployeeAssignList(employeeAssignMap.getOrDefault(employee.getEmployeeId(), Collections.emptyList()));
        }
        return employeeList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Employee> createOrUpdateEmployee(Long tenantId, List<Employee> employeeList) {
        if (!CollectionUtils.isEmpty(employeeList)) {
            for (Employee employee : employeeList) {
                // 检查拼音和快速索引是否为空，为空则自动生成拼音和快速索引数据
                employee.generateQuickIndexAndPinyin();
                if (employee.getEmployeeId() == null) {
                    employee.setTenantId(tenantId);
                    this.validateEmployeeNum(employee);
                    employeeRepository.insertSelective(employee);
                } else {
                    employeeRepository.updateByPrimaryKeySelective(employee);
                }
                employeeRepository.saveCache(employeeRepository.selectByPrimaryKey(employee.getEmployeeId()));
            }
        }
        return employeeList;
    }


    private void validateEmployeeNum(Employee employee) {
        Employee employeeTemp = new Employee();
        employeeTemp.setEmployeeNum(employee.getEmployeeNum());
        employeeTemp.setTenantId(employee.getTenantId());
        List<Employee> employeeList = employeeRepository.select(employeeTemp);
        if (!employeeList.isEmpty()) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
	public EmployeeDTO insertEmployeeDetail(Long organizationId, EmployeeDTO employeeDTO) {
		//      1. 新增员工信息
		Employee employee = new Employee();
		BeanUtils.copyProperties(employeeDTO, employee);
		employee.setEmployeeNum(employeeDTO.getEmployeeCode());
		if (StringUtils.isEmpty(employee.getStatus())){
			employee.setStatus("ON");
		}
		if (employee.getGender()==null){
			employee.setGender(1);
		}
		List<Employee> employeeList = new ArrayList<>();
		employeeList.add(employee);
		employeeList = this.createOrUpdateEmployee(organizationId, employeeList);
		Long employeeId = null;
		if (CollectionUtils.isNotEmpty(employeeList)) {
			employeeId = employeeList.get(0).getEmployeeId();
		}
		//    2.更新岗位
		this.updateEmployeeAssign(organizationId, employeeId, employeeDTO.getList());

		employeeDTO.setEmployeeId(employeeId);
		return employeeDTO;
	}
	
	/**
     * 更新员工岗位信息
     *
     * @param organizationId
     * @param employeeId
     * @param unitAssignDTOS
     */
    private void updateEmployeeAssign(Long organizationId, Long employeeId, List<UnitAssignDTO> unitAssignDTOS) {
        List<EmployeeAssign> employeeAssignList = new ArrayList<>();
        int primaryPositionFlagCount = 0;
        for (UnitAssignDTO unitAssignDTO : unitAssignDTOS) {
            if (unitAssignDTO.getUnitId()==null || unitAssignDTO.getPositionId()==null){
                continue;
            }
            if (unitAssignDTO.getPrimaryPositionFlag()!=null && unitAssignDTO.getPrimaryPositionFlag() == 1) {
                primaryPositionFlagCount++;
            }
            EmployeeAssign employeeAssign = new EmployeeAssign();
            BeanUtils.copyProperties(unitAssignDTO, employeeAssign);
            employeeAssign.setEmployeeId(employeeId);
            employeeAssign.setTenantId(organizationId);
            employeeAssign.setEnabledFlag(BaseConstants.Flag.YES);
            employeeAssignList.add(employeeAssign);
        }
        if (primaryPositionFlagCount != 1) {
            throw new CommonException("error.employee_primary_position_must_have_one");
        }
        employeeAssignService.updateEmployeeAssign(organizationId, employeeId, employeeAssignList);
    }

	@Override
	public EmployeeDTO updateEmployeeDetail(Long organizationId,EmployeeDTO employeeDTO) {
		//1.        更新岗位
        if (employeeDTO.getList()!=null && !employeeDTO.getList().isEmpty()){
            this.updateEmployeeAssign(organizationId, employeeDTO.getEmployeeId(), employeeDTO.getList());
        }
        //2.        更新主题字段
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setEnabledFlag(Integer.parseInt(employeeDTO.getEnabledFlag()));
        // 检查快速索引和拼音信息是否存在，不存在则生成
        employee.generateQuickIndexAndPinyin();
        //更新员工信息
        employeeRepository.updateOptional(employee, Employee.FIELD_EMAIL, Employee.FIELD_NAME,
                Employee.FIELD_MOBILE, Employee.FIELD_ENABLE_FLAG, Employee.FIELD_QUICT_INDEX,
                Employee.FIELD_PHONETICIZE, Employee.FIELD_STATUS, Employee.FIELD_ENTRY_DATE, Employee.FIELD_GENDER);
        employeeRepository.saveCache(employeeRepository.selectByPrimaryKey(employee.getEmployeeId()));

		return employeeDTO;
	}

	@Override
	public EmployeeDTO queryEmployeeDetail(Long organizationId, Long employeeId) {
		 EmployeeDTO employeeDTO = employeeRepository.selectEmployeeByEmployeeId(organizationId, employeeId);
		 if (employeeDTO == null){
	            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
	     }
	   	 List<Long> unitIdList = new ArrayList<>();
         List<UnitAssignDTO> list = employeeDTO.getList();
         for (UnitAssignDTO unitAssignDTO : list) {
             List<Unit> units = unitRepository.queryParentUnitsByLevelPath(unitAssignDTO.getLevelPath(), organizationId);
             unitAssignDTO.setUnits(units);
             units.forEach(unit -> unitIdList.add(unit.getUnitId()));
             // 部门下的岗位
             List<Position> positionList = positionRepository.select(new Position().setTenantId(organizationId)
            				 						.setUnitId(unitAssignDTO.getUnitId())
            				 						.setEnabledFlag(BaseConstants.Flag.YES));
             unitAssignDTO.setPositionList(positionList);
         }
	     return employeeDTO;
	}

    @Override
    public List<String> listEmployeeByRoleCodeId(Long tenantId, Long roleId) {
        return employeeRepository.listEmployeeByRoleId(tenantId, roleId);
    }

}
