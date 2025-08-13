package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.plugin.platform.hr.app.service.HrSyncEmployeeService;
import org.hzero.plugin.platform.hr.domain.entity.*;
import org.hzero.plugin.platform.hr.domain.repository.*;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.plugin.platform.hr.infra.enums.HrSyncTypeEnum;
import org.hzero.starter.integrate.constant.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HR员工数据同步应用服务默认实现
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
@Service
public class HrSyncEmployeeServiceImpl implements HrSyncEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(HrSyncEmployeeServiceImpl.class);

    @Autowired
    private HrSyncEmployeeRepository hrSyncEmployeeRepository;
    @Autowired
    private HrSyncDeptEmployeeRepository hrSyncDeptEmployeeRepository;
    @Autowired
    private HrSyncDeptRepository hrSyncDeptRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private EmployeeAssignRepository employeeAssignRepository;
    @Autowired
    private CodeRuleBuilder codeRuleBuilder;

    @Override
    public List<HrSyncEmployee> getSyncEmployee(String syncTypeCode, Long tenantId) {
        List<HrSyncEmployee> syncEmployeeList = new ArrayList<>();
        List<Long> parentids = new ArrayList<>();
        List<HrSyncEmployee> createEmployee = hrSyncEmployeeRepository.getCreateEmployee(syncTypeCode, tenantId);
        createEmployee.stream().collect(Collectors.groupingBy(HrSyncEmployee::getUserid)).forEach((k, v) -> {
            HrSyncEmployee hrSyncEmployee = v.get(0);
            Set<Long> departmentIds = new HashSet<>();
            v.forEach(e -> {
                departmentIds.add(e.getDepartmentId());
                if (Objects.equals(e.getPrimaryPositionFlag(), BaseConstants.Flag.YES)) {
                    // 用户岗位名称为主岗名称
                    hrSyncEmployee.setPosition(e.getPosition());
                }
            });
            hrSyncEmployee.setDepartmentIds(new ArrayList<>(departmentIds));
            parentids.addAll(departmentIds);
            // 更改性别代表数字
            hrSyncEmployee.setSyncType(SyncType.CREATE);
            hrSyncEmployee.setSyncTypeCode(syncTypeCode);
            // 企业微信用户id不允许有 / ，平台不允许有@，因此特殊处理将员工编码中的 / 替换成 @
            hrSyncEmployee.setEmployeeNum(hrSyncEmployee.getUserid());
            hrSyncEmployee.setUserid(hrSyncEmployee.getUserid().replaceAll(PlatformHrConstants.SLASH, PlatformHrConstants.AT));
            convertGender(hrSyncEmployee, syncTypeCode);
            syncEmployeeList.add(hrSyncEmployee);
        });
        List<HrSyncEmployee> deleteEmployee = hrSyncEmployeeRepository.getDeleteEmployee(syncTypeCode, tenantId);
        deleteEmployee.forEach(e -> e.setSyncType(SyncType.DELETE));
        syncEmployeeList.addAll(deleteEmployee);
        List<HrSyncEmployee> updateEmployee = hrSyncEmployeeRepository.getUpdateEmployee(syncTypeCode, tenantId);
        updateEmployee.stream().collect(Collectors.groupingBy(HrSyncEmployee::getUserid)).forEach((k, v) -> {
            HrSyncEmployee hrSyncEmployee = v.get(0);
            Set<Long> departmentIds = new HashSet<>();
            v.forEach(e -> {
                if (Objects.equals(e.getPrimaryPositionFlag(), BaseConstants.Flag.YES)) {
                    hrSyncEmployee.setPosition(e.getPosition());
                }
                departmentIds.add(e.getDepartmentId());
            });
            parentids.addAll(departmentIds);
            hrSyncEmployee.setDepartmentIds(new ArrayList<>(departmentIds));
            hrSyncEmployee.setSyncType(SyncType.UPDATE);
            // 企业微信用户id不允许有 / ，平台不允许有@，因此特殊处理将员工编码中的 / 替换成 @
            hrSyncEmployee.setEmployeeNum(hrSyncEmployee.getUserid());
            hrSyncEmployee.setUserid(hrSyncEmployee.getUserid().replaceAll(PlatformHrConstants.SLASH, PlatformHrConstants.AT));
            convertGender(hrSyncEmployee, syncTypeCode);
            syncEmployeeList.add(hrSyncEmployee);
        });
        if (CollectionUtils.isNotEmpty(parentids)) {
            // 映射部门
            Map<Long, Long> deptIdMap = hrSyncDeptRepository.getByUnitIds(syncTypeCode, tenantId, parentids).stream()
                    .collect(Collectors.toMap(HrSyncDept::getUnitId, HrSyncDept::getDepartmentId));
            syncEmployeeList.stream().filter(e -> !SyncType.DELETE.equals(e.getSyncType()))
                    .forEach(e -> {
                        // 新增及更新操作需要映射部门
                        List<Long> departmentIds = e.getDepartmentIds();
                        List<Boolean> isParentidsMap = new ArrayList<>();
                        for (int i = 0; i < departmentIds.size(); i++) {
                            if (deptIdMap.get(departmentIds.get(i)) != null) {
                                departmentIds.set(i, deptIdMap.get(departmentIds.get(i)));
                                // 记录该部门已经映射
                                isParentidsMap.add(true);
                            } else {
                                // 记录该部门未映射
                                isParentidsMap.add(false);
                            }
                        }
                        e.setDepartmentIds(departmentIds);
                        e.setIsDepartIdsMap(isParentidsMap);
                    });
        }
        return syncEmployeeList;
    }

    @Override
    public void updateSyncEmployee(List<HrSyncEmployee> hrSyncEmployees) {
        hrSyncEmployees.forEach(employee -> {
            if (SyncType.CREATE.equals(employee.getSyncType())) {
                try {
                    hrSyncEmployeeRepository.insertSelective(employee);
                    List<HrSyncDeptEmployee> hrSyncDeptEmployees = new ArrayList<>();
                    // 新建员工和部门关系
                    List<HrSyncDept> hrSyncDeptList = hrSyncDeptRepository.getByDeptIds(employee.getSyncTypeCode(),
                            employee.getTenantId(), employee.getDepartmentIds());
                    hrSyncDeptList.forEach(dept -> {
                        HrSyncDeptEmployee hrSyncDeptEmployee = new HrSyncDeptEmployee();
                        hrSyncDeptEmployee.setTenantId(employee.getTenantId());
                        hrSyncDeptEmployee.setSyncEmployeeId(employee.getSyncEmployeeId());
                        hrSyncDeptEmployee.setSyncDeptId(dept.getSyncDeptId());
                        hrSyncDeptEmployees.add(hrSyncDeptEmployee);
                    });
                    hrSyncDeptEmployeeRepository.batchInsertSelective(hrSyncDeptEmployees);
                } catch (Exception e) {
                    logger.error("create HrSyncEmployee failed,e:{}", e.getMessage());
                }
            }
            if (SyncType.UPDATE.equals(employee.getSyncType())) {
                try {
                    hrSyncEmployeeRepository.updateByPrimaryKeySelective(employee);
                    // 变更员工部门关系,1.删除现有关系 2.创建新关系
                    List<HrSyncDeptEmployee> hrSyncDeptEmployees = hrSyncDeptEmployeeRepository
                            .selectByCondition(Condition.builder(HrSyncDeptEmployee.class)
                                    .andWhere(Sqls.custom().andEqualTo(
                                            HrSyncDeptEmployee.FIELD_SYNC_EMPLOYEE_ID,
                                            employee.getSyncEmployeeId()))
                                    .build());
                    hrSyncDeptEmployeeRepository.batchDeleteByPrimaryKey(hrSyncDeptEmployees);
                    hrSyncDeptEmployees.clear();
                    List<HrSyncDept> hrSyncDeptList = hrSyncDeptRepository.getByDeptIds(employee.getSyncTypeCode(),
                            employee.getTenantId(), employee.getDepartmentIds());
                    hrSyncDeptList.forEach(dept -> {
                        HrSyncDeptEmployee hrSyncDeptEmployee = new HrSyncDeptEmployee();
                        hrSyncDeptEmployee.setSyncEmployeeId(employee.getSyncEmployeeId());
                        hrSyncDeptEmployee.setTenantId(employee.getTenantId());
                        hrSyncDeptEmployee.setSyncDeptId(dept.getSyncDeptId());
                        hrSyncDeptEmployees.add(hrSyncDeptEmployee);
                    });
                    hrSyncDeptEmployeeRepository.batchInsertSelective(hrSyncDeptEmployees);
                } catch (Exception e) {
                    logger.error("update HrSyncEmployee failed,e:{}", e.getMessage());
                }
            }
            if (SyncType.DELETE.equals(employee.getSyncType())) {
                try {
                    hrSyncEmployeeRepository.deleteByPrimaryKey(employee);
                    // 删除部门员工关系表
                    List<HrSyncDeptEmployee> hrSyncDeptEmployees = hrSyncDeptEmployeeRepository
                            .selectByCondition(Condition.builder(HrSyncDeptEmployee.class)
                                    .andWhere(Sqls.custom().andEqualTo(
                                            HrSyncDeptEmployee.FIELD_SYNC_EMPLOYEE_ID,
                                            employee.getSyncEmployeeId()))
                                    .build());
                    hrSyncDeptEmployeeRepository.batchDeleteByPrimaryKey(hrSyncDeptEmployees);
                } catch (Exception e) {
                    logger.error("delete HrSyncEmployee failed,e:{}", e.getMessage());
                }
            }
        });
    }

    @Override
    public void syncEmployee(HrSyncDept hrSyncDept, List<HrSyncEmployee> hrSyncEmployeeList, Long tenantId,
                             String syncTypeCode, StringBuilder log) {
        logger.debug("***Start sync employee***");
        if (CollectionUtils.isNotEmpty(hrSyncEmployeeList)) {
            hrSyncEmployeeList.forEach(hrSyncEmployee -> {
                // 与副本对比
                HrSyncEmployee copyEmployee = hrSyncEmployeeRepository
                        .selectOne(new HrSyncEmployee().setUserid(hrSyncEmployee.getUserid()).setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
                if (copyEmployee == null) {
                    // 副本没有员工 需要新建员工 员工副本
                    Employee employee = createEmployee(hrSyncEmployee, tenantId, syncTypeCode, log);
                    // 保存部门-员工副本
                    saveHrSyncDeptEmployee(hrSyncDept.getDepartmentId(), syncTypeCode, hrSyncEmployee.getSyncEmployeeId(), tenantId);
                    // 新建的员工要挂到岗位下
                    // 找到本地部门id
                    HrSyncDept copyDept = hrSyncDeptRepository.selectOne(new HrSyncDept()
                            .setDepartmentId(hrSyncDept.getDepartmentId()).setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
                    // 寻找部门下的岗位
                    List<Position> positionList = positionRepository.select(new Position()
                            .setUnitId(copyDept.getUnitId()).setTenantId(tenantId));
                    // 看有没有同名的岗位
                    List<Position> positionByName = positionList.stream().filter(position ->
                            hrSyncEmployee.getPosition().equals(position.getPositionName())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(positionByName)) {
                        // 有岗位直接分给员工
                        assignUnitPositionToEmployee(copyDept.getUnitId(), positionByName.get(0).getPositionId(),
                                hrSyncEmployee.getPosition(), employee.getEmployeeId(), tenantId);
                    } else {
                        assignUnitPositionToEmployee(copyDept.getUnitId(), null, hrSyncEmployee.getPosition(),
                                employee.getEmployeeId(), tenantId);
                    }
                } else {
                    // 更新员工
                    updateEmployee(hrSyncEmployee, copyEmployee, hrSyncDept.getDepartmentId(), tenantId, syncTypeCode, log);
                }
            });
            // 筛选出部门下需要禁用的员工
            List<HrSyncDeptEmployee> copyDeptEmployeeList = hrSyncDeptEmployeeRepository.select(new HrSyncDeptEmployee()
                    .setSyncDeptId(hrSyncDept.getDepartmentId()).setTenantId(tenantId));
            List<HrSyncDeptEmployee> disableEmployeeList = copyDeptEmployeeList.stream().filter(copyDeptEmployee ->
                    hrSyncEmployeeList.stream().noneMatch(emp ->
                            copyDeptEmployee.getSyncEmployeeId().equals(emp.getSyncEmployeeId()))).collect(Collectors.toList());
            disableEmployeeList.forEach(deptEmployee -> {
                // 从副本找到平台员工
                HrSyncEmployee hrSyncEmployee = hrSyncEmployeeRepository.selectByPrimaryKey(deptEmployee.getSyncEmployeeId());
                Employee employee = employeeRepository.selectOne(new Employee().setEmployeeNum(hrSyncEmployee.getEmployeeNum()).setTenantId(tenantId));
                // 禁用
                employee.setEnabledFlag(BaseConstants.Flag.NO);
                log.append("start disabling employee: ").append(employee.toString()).append(StringUtils.LF);
                employeeRepository.updateByPrimaryKey(employee);
                // 删除副本
                hrSyncDeptEmployeeRepository.deleteByPrimaryKey(deptEmployee.getSyncAssignId());
            });
        }
    }

    /**
     * 更新员工
     *
     * @param thirdEmployee 三方平台员工
     * @param copyEmployee  副本员工
     * @param departmentId  三方部门ID
     * @param tenantId      租户ID
     * @param syncTypeCode  同步类型
     * @param log           日志
     */
    private void updateEmployee(HrSyncEmployee thirdEmployee, HrSyncEmployee copyEmployee, Long departmentId,
                                Long tenantId, String syncTypeCode, StringBuilder log) {
        Employee employee = employeeRepository.selectOne(new Employee().setEmployeeNum(copyEmployee.getEmployeeNum()).setTenantId(tenantId));
        if (employee == null) {
            createEmployee(thirdEmployee, tenantId, syncTypeCode, log);
            return;
        }
        employee.setGender(getConvertGenderFromThird(thirdEmployee, syncTypeCode)).setName(thirdEmployee.getName())
                .setMobile(thirdEmployee.getMobile() == null ? employee.getMobile() : thirdEmployee.getMobile()).setEmail(thirdEmployee.getEmail());
        if (!thirdEmployee.getPosition().equals(copyEmployee.getPosition())) {
            // 岗位变更需要重新分配
            // 找到本地部门id
            HrSyncDept copyDept = hrSyncDeptRepository.selectOne(new HrSyncDept().setDepartmentId(departmentId)
                    .setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
            assignUnitPositionToEmployee(copyDept.getUnitId(), null, thirdEmployee.getPosition(), employee.getEmployeeId(), tenantId);
        }
        logger.debug("start update employee: {}", employee.toString());
        log.append("start update employee: ").append(employee.toString()).append(StringUtils.LF);
        try {
            //　更新员工
            employeeRepository.updateByPrimaryKey(employee);
            employeeRepository.saveCache(employeeRepository.selectByPrimaryKey(employee.getEmployeeId()));
        } catch (Exception e) {
            logger.error("error update employee, employee: {}", employee.toString());
            log.append("error update employee, employee: ").append(employee.toString()).append(StringUtils.LF);
        }
        thirdEmployee.setSyncEmployeeId(copyEmployee.getSyncEmployeeId()).setSyncTypeCode(syncTypeCode).setTenantId(tenantId)
                .setEmployeeNum(employee.getEmployeeNum()).setGender(getConvertGenderFromThird(thirdEmployee, syncTypeCode))
                .setObjectVersionNumber(copyEmployee.getObjectVersionNumber());
        // 更新副本
        hrSyncEmployeeRepository.updateByPrimaryKey(thirdEmployee);
    }

    /**
     * 创建员工与员工副本
     *
     * @param hrSyncEmployee 员工副本
     * @param tenantId       租户ID
     * @param syncTypeCode   同步类型
     * @param log            日志
     * @return 平台员工
     */
    private Employee createEmployee(HrSyncEmployee hrSyncEmployee, Long tenantId, String syncTypeCode, StringBuilder log) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(hrSyncEmployee, employee);
        // 创建之前先查重
        List<Employee> employeeList = employeeRepository.select(new Employee()
                .setEmployeeNum(hrSyncEmployee.getUserid().replaceAll(PlatformHrConstants.AT, PlatformHrConstants.SLASH))
                .setTenantId(tenantId));
        if (CollectionUtils.isNotEmpty(employeeList)) {
            employee = employeeList.get(0);
        } else {
            employee.setEmployeeNum(hrSyncEmployee.getUserid().replaceAll(PlatformHrConstants.AT, PlatformHrConstants.SLASH))
                    .setTenantId(tenantId)
                    .setGender(getConvertGenderFromThird(hrSyncEmployee, syncTypeCode))
                    .generateQuickIndexAndPinyin();
            logger.debug("start create employee: {}", employee.toString());
            log.append("start create employee: ").append(employee.toString()).append(StringUtils.LF);
            try {
                employeeRepository.insertSelective(employee);
                employeeRepository.saveCache(employeeRepository.selectByPrimaryKey(employee.getEmployeeId()));
            } catch (Exception e) {
                logger.error("error create employee: {}", employee.toString());
                log.append("error create employee: ").append(employee.toString()).append(StringUtils.LF);
            }
        }
        // 保存副本
        try {
            hrSyncEmployee.setSyncTypeCode(syncTypeCode).setEmployeeNum(employee.getEmployeeNum()).setTenantId(tenantId);
            hrSyncEmployeeRepository.insertSelective(hrSyncEmployee);
        } catch (Exception e) {
            logger.warn("failed to save employee copy, the copy may already exist");
        }
        return employee;
    }

    /**
     * 给员工分配岗位，没有就新建再分配
     *
     * @param unitId       组织DI
     * @param positionId   岗位ID
     * @param positionName 岗位名
     * @param employeeId   员工ID
     */
    private void assignUnitPositionToEmployee(Long unitId, Long positionId, String positionName, Long employeeId, Long tenantId) {
        Unit unit = unitRepository.selectByPrimaryKey(unitId);
        if (positionId == null) {
            // 没有岗位，新建一个岗位
            Position position = new Position();
            position.setTenantId(tenantId)
                    .setUnitCompanyId(unit.getUnitCompanyId())
                    .setUnitId(unitId)
                    .setPositionCode(codeRuleBuilder.generateCode(PlatformHrConstants.HPFM_POSITION, null))
                    .setPositionName(positionName)
                    .setSupervisorFlag(BaseConstants.Flag.NO)
                    .setOrderSeq(PlatformHrConstants.DEFAULT_ORDER_SEQ)
                    .setLevelPath(position.getPositionCode());
            positionRepository.insertSelective(position);
            positionId = position.getPositionId();
        }
        // 分配
        EmployeeAssign employeeAssign = new EmployeeAssign();
        employeeAssign.setEmployeeId(employeeId);
        employeeAssign.setPositionId(positionId);
        employeeAssign.setUnitId(unitId);
        employeeAssign.setUnitCompanyId(unit.getUnitCompanyId());
        employeeAssign.setTenantId(tenantId);
        employeeAssignRepository.insertSelective(employeeAssign);
    }

    /**
     * 保存第三方部门员工关系表
     *
     * @param departmentId   部门ID
     * @param syncTypeCode   同步类型
     * @param syncEmployeeId 员工ID
     * @param tenantId       租户ID
     */
    private void saveHrSyncDeptEmployee(Long departmentId, String syncTypeCode, Long syncEmployeeId, Long tenantId) {
        HrSyncDept hrSyncDept = hrSyncDeptRepository.selectOne(new HrSyncDept().setDepartmentId(departmentId)
                .setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
        HrSyncDeptEmployee copySyncDeptEmployee = new HrSyncDeptEmployee().setSyncDeptId(hrSyncDept.getSyncDeptId())
                .setSyncEmployeeId(syncEmployeeId).setTenantId(tenantId);
        hrSyncDeptEmployeeRepository.insertSelective(copySyncDeptEmployee);
    }

    /**
     * 性别转换
     *
     * @param hrSyncEmployee 同步的员工
     * @param syncTypeCode   同步的类型
     */
    private void convertGender(HrSyncEmployee hrSyncEmployee, String syncTypeCode) {
        if (HrSyncTypeEnum.DD.value().equals(syncTypeCode)) {
            // 钉钉没有gender
            hrSyncEmployee.setGender(null);
        }
        if (HrSyncTypeEnum.WX.value().equals(syncTypeCode)) {
            // 平台性别是0：男，1：女，微信性别为1：男，2：女
            if (hrSyncEmployee.getGender() == BaseConstants.Digital.ZERO) {
                hrSyncEmployee.setGender(BaseConstants.Digital.ONE);
            } else {
                hrSyncEmployee.setGender(BaseConstants.Digital.TWO);
            }
        }
    }

    /**
     * 获取第三方平台员工性别转换到平台的值
     *
     * @param hrSyncEmployee 三方平台员工
     * @param syncTypeCode   同步类型
     * @return 性别
     */
    private Integer getConvertGenderFromThird(HrSyncEmployee hrSyncEmployee, String syncTypeCode) {
        if (HrSyncTypeEnum.WX.value().equals(syncTypeCode)) {
            if (hrSyncEmployee.getGender() != null) {
                return hrSyncEmployee.getGender().equals(BaseConstants.Digital.ONE) ? BaseConstants.Digital.ZERO : BaseConstants.Digital.ONE;
            }
        }
        return BaseConstants.Digital.ZERO;
    }

}
