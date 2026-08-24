package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.plugin.platform.hr.app.service.HrSyncDeptService;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncDept;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncDeptEmployee;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncDeptEmployeeRepository;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncDeptRepository;
import org.hzero.plugin.platform.hr.domain.repository.UnitRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.starter.integrate.constant.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HR部门数据同步应用服务默认实现
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
@Service
public class HrSyncDeptServiceImpl implements HrSyncDeptService {

    private static final Logger logger = LoggerFactory.getLogger(HrSyncDeptServiceImpl.class);
    private static final Long ROOT_DEPT_ID = 1L;

    @Autowired
    private HrSyncDeptRepository hrSyncDeptRepository;
    @Autowired
    private HrSyncDeptEmployeeRepository hrSyncDeptEmployeeRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private CodeRuleBuilder codeRuleBuilder;

    @Override
    public List<HrSyncDept> getSyncDept(String syncTypeCode, Long tenantId) {
        List<Long> parentUnitId = new ArrayList<>();
        List<HrSyncDept> createDept = hrSyncDeptRepository.getCreateDept(syncTypeCode, tenantId);
        createDept.forEach(e -> {
            if (e.getParentUnitId() == null) {
                // 将该部门父部门设置为根部门，企业微信、钉钉根部门id都为1
                e.setParentid(ROOT_DEPT_ID);
            } else {
                parentUnitId.add(e.getParentUnitId());
            }
            e.setSyncType(SyncType.CREATE);
            e.setSyncTypeCode(syncTypeCode);
        });
        List<HrSyncDept> hrSyncDeptList = new ArrayList<>(createDept);
        List<HrSyncDept> deleteDept = hrSyncDeptRepository.getDeleteDept(syncTypeCode, tenantId);
        deleteDept.forEach(e -> e.setSyncType(SyncType.DELETE));
        hrSyncDeptList.addAll(deleteDept);
        List<HrSyncDept> updateDept = hrSyncDeptRepository.getUpdateDept(syncTypeCode, tenantId);
        updateDept.forEach(e -> {
            if (e.getParentUnitId() == null) {
                // 将该部门父部门设置为根部门，企业微信、钉钉根部门id都为1
                e.setParentid(ROOT_DEPT_ID);
            } else {
                parentUnitId.add(e.getParentUnitId());
            }
            e.setSyncType(SyncType.UPDATE);
        });
        hrSyncDeptList.addAll(updateDept);
        if (CollectionUtils.isNotEmpty(parentUnitId)) {
            // 第三方平台已经存在的父部门
            Map<Long, Long> deptIdMap = hrSyncDeptRepository.getByUnitIds(syncTypeCode, tenantId, parentUnitId).stream()
                    .collect(Collectors.toMap(HrSyncDept::getUnitId, HrSyncDept::getDepartmentId));
            hrSyncDeptList.stream().filter(e -> !SyncType.DELETE.equals(e.getSyncType()))
                    .forEach(e -> {
                        // 设置第三方父部门
                        if (deptIdMap.get(e.getParentUnitId()) != null) {
                            e.setParentid(deptIdMap.get(e.getParentUnitId()));
                        }
                    });
        }
        return hrSyncDeptList;
    }

    @Override
    public void updateSyncDept(List<HrSyncDept> hrSyncDeptList) {
        hrSyncDeptList.forEach(dept -> {
            if (SyncType.CREATE.equals(dept.getSyncType())) {
                try {
                    hrSyncDeptRepository.insertSelective(dept);
                } catch (Exception e) {
                    logger.error("create HrSyncDept failed,e:{}", e.getMessage());
                }
            }
            if (SyncType.UPDATE.equals(dept.getSyncType())) {
                try {
                    hrSyncDeptRepository.updateByPrimaryKeySelective(dept);
                } catch (Exception e) {
                    logger.error("update HrSyncDept failed,e:{}", e.getMessage());
                }
            }
            if (SyncType.DELETE.equals(dept.getSyncType())) {
                try {
                    hrSyncDeptRepository.deleteByPrimaryKey(dept);
                    List<HrSyncDeptEmployee> hrSyncDeptEmployees = hrSyncDeptEmployeeRepository
                            .selectByCondition(Condition.builder(HrSyncDeptEmployee.class)
                                    .andWhere(Sqls.custom().andEqualTo(
                                            HrSyncDeptEmployee.FIELD_SYNC_DEPT_ID,
                                            dept.getSyncDeptId()))
                                    .build());
                    hrSyncDeptEmployeeRepository.batchDeleteByPrimaryKey(hrSyncDeptEmployees);
                } catch (Exception e) {
                    logger.error("delete HrSyncDept failed,e:{}", e.getMessage());
                }
            }
        });
    }

    @Override
    public void syncDeptToLocal(List<HrSyncDept> hrSyncDeptList, Long tenantId, String syncTypeCode, StringBuilder log) {
        List<HrSyncDept> copyDeptList = hrSyncDeptRepository.select(new HrSyncDept().setTenantId(tenantId).setSyncTypeCode(syncTypeCode));
        // 获取禁用部门
        List<HrSyncDept> disableDeptList = new ArrayList<>();
        Map<Long, HrSyncDept> copyDeptMap = copyDeptList.stream().collect(Collectors.toMap(HrSyncDept::getDepartmentId, Function.identity()));
        Map<Long, HrSyncDept> thirdDeptMap = hrSyncDeptList.stream().collect(Collectors.toMap(HrSyncDept::getDepartmentId, Function.identity()));
        copyDeptMap.forEach((departmentId, dept) -> {
            if (thirdDeptMap.get(departmentId) == null) {
                disableDeptList.add(dept);
            }
        });
        // 禁用部门
        if (!CollectionUtils.isEmpty(disableDeptList)) {
            disableSyncDept(disableDeptList, tenantId, syncTypeCode, log);
        }
        // 根部门
        HrSyncDept rootDept = new HrSyncDept();
        Iterator<HrSyncDept> iterator = hrSyncDeptList.iterator();
        while (iterator.hasNext()) {
            HrSyncDept thirdDept = iterator.next();
            if (ROOT_DEPT_ID.equals(thirdDept.getDepartmentId())) {
                rootDept = thirdDept;
                iterator.remove();
                break;
            }
        }
        buildHrSyncDeptTree(rootDept, hrSyncDeptList);
        HrSyncDept copyDept = hrSyncDeptRepository.selectOne(new HrSyncDept()
                .setDepartmentId(rootDept.getDepartmentId()).setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
        Unit rootUnit;
        if (copyDept == null) {
            rootUnit = createUnit(rootDept, null, tenantId, syncTypeCode, log);
        } else {
            rootUnit = unitRepository.selectByPrimaryKey(copyDept.getUnitId());
        }
        syncChildDept(rootDept, rootUnit, tenantId, syncTypeCode, log);
    }

    /**
     * 同步子部门
     *
     * @param parentDept   三方父部门
     * @param parentUnit   对应父组织
     * @param tenantId     租户ID
     * @param syncTypeCode 同步类型
     * @param log          日志
     */
    private void syncChildDept(HrSyncDept parentDept, Unit parentUnit, Long tenantId, String syncTypeCode, StringBuilder log) {
        if (CollectionUtils.isNotEmpty(parentDept.getChildDept())) {
            parentDept.getChildDept().forEach(childDept -> {
                Unit unit;
                HrSyncDept copyDept = hrSyncDeptRepository.selectOne(new HrSyncDept()
                        .setDepartmentId(childDept.getDepartmentId()).setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
                if (copyDept == null) {
                    unit = createUnit(childDept, parentUnit, tenantId, syncTypeCode, log);
                } else {
                    unit = updateUnit(parentUnit, parentDept, childDept, copyDept, log);
                }
                syncChildDept(childDept, unit, tenantId, syncTypeCode, log);
            });
        }
    }

    private Unit updateUnit(Unit rootUnit, HrSyncDept parentDept, HrSyncDept hrSyncDept, HrSyncDept copyDept, StringBuilder log) {
        // 找到父组织
        Unit parentUnit = unitRepository.selectByPrimaryKey(parentDept.getUnitId());
        if (parentUnit == null) {
            parentUnit = rootUnit;
        }
        Unit unit = unitRepository.selectByPrimaryKey(copyDept.getUnitId());
        unit.setUnitName(hrSyncDept.getName()).setOrderSeq(hrSyncDept.getOrderSeq())
                .setParentUnitId(parentUnit.getUnitId())
                .setLevelPath(parentUnit.getLevelPath() + PlatformHrConstants.SPLIT + unit.getUnitCode())
                .setUnitCompanyId(PlatformHrConstants.UnitType.DEPARTMENT.equals(unit.getUnitTypeCode()) ?
                        parentUnit.getUnitCompanyId() == null ? parentUnit.getUnitId() : parentUnit.getUnitCompanyId() : null)
                .generateQuickIndexAndPinyin();
        // 父部门变化
        copyDept.setParentid(hrSyncDept.getParentid());
        logger.debug("start update unit : {}", unit.toString());
        log.append("start update unit : ").append(unit.toString()).append(StringUtils.LF);
        unitRepository.updateByPrimaryKey(unit);
        copyDept.setName(hrSyncDept.getName());
        copyDept.setOrderSeq(hrSyncDept.getOrderSeq());
        hrSyncDeptRepository.updateByPrimaryKeySelective(copyDept);
        return unit;
    }


    @Override
    public void disableSyncDept(List<HrSyncDept> hrSyncDeptList, Long tenantId, String syncTypeCode, StringBuilder log) {
        if (CollectionUtils.isNotEmpty(hrSyncDeptList)) {
            hrSyncDeptList.forEach(hrSyncDept -> {
                HrSyncDept copyDept = hrSyncDeptRepository.selectOne(new HrSyncDept()
                        .setDepartmentId(hrSyncDept.getDepartmentId()).setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
                Unit unit = unitRepository.selectByPrimaryKey(copyDept.getUnitId());
                if (unit != null) {
                    unit.setEnabledFlag(BaseConstants.Flag.NO);
                    logger.debug("start disable unit : {}", unit.toString());
                    log.append("start disable unit :").append(unit.toString()).append(StringUtils.LF);
                    unitRepository.updateByPrimaryKey(unit);
                    hrSyncDeptRepository.deleteByPrimaryKey(copyDept.getSyncDeptId());
                }
            });
        }
    }

    /**
     * 创建部门
     *
     * @param hrSyncDept   第三方部门
     * @param parentUnit   父组织
     * @param tenantId     租户ID
     * @param syncTypeCode 同步类型；钉钉/微信
     * @return 创建的unit
     */
    private Unit createUnit(HrSyncDept hrSyncDept, Unit parentUnit, Long tenantId, String syncTypeCode, StringBuilder log) {
        Unit unit = new Unit();
        String code = codeRuleBuilder.generateCode(PlatformHrConstants.HPFM_UNIT, null);
        unit.setUnitCode(code)
                .setUnitName(hrSyncDept.getName())
                .setUnitTypeCode(parentUnit == null ? PlatformHrConstants.UnitType.GROUP : PlatformHrConstants.UnitType.DEPARTMENT)
                .setTenantId(tenantId)
                .setOrderSeq(hrSyncDept.getOrderSeq())
                .setParentUnitId(parentUnit == null ? null : parentUnit.getUnitId())
                .setUnitCompanyId(parentUnit == null ? null :
                        parentUnit.getUnitCompanyId() == null ? parentUnit.getUnitId() : parentUnit.getUnitCompanyId())
                .setLevelPath(parentUnit == null ? code : parentUnit.getLevelPath() + PlatformHrConstants.SPLIT + code)
                .generateQuickIndexAndPinyin();
        logger.debug("start create unit : {}", unit.toString());
        log.append("start create unit :").append(unit.toString()).append(StringUtils.LF);
        unitRepository.insertSelective(unit);
        // 保存副本
        int i = hrSyncDeptRepository.selectCount(new HrSyncDept()
                .setDepartmentId(hrSyncDept.getDepartmentId()).setSyncTypeCode(syncTypeCode).setTenantId(tenantId));
        if (i == 0) {
            hrSyncDept.setUnitId(unit.getUnitId());
            hrSyncDept.setSyncTypeCode(syncTypeCode);
            hrSyncDept.setTenantId(tenantId);
            hrSyncDeptRepository.insertSelective(hrSyncDept);
        }
        return unit;
    }

    /**
     * 构建根部门-子部门树结构
     *
     * @param hrSyncDept     父部门
     * @param hrSyncDeptList 部门列表
     */
    private void buildHrSyncDeptTree(HrSyncDept hrSyncDept, List<HrSyncDept> hrSyncDeptList) {
        hrSyncDept.setChildDept(new ArrayList<>());
        if (!CollectionUtils.isEmpty(hrSyncDeptList)) {
            Iterator<HrSyncDept> iterator = hrSyncDeptList.iterator();
            while (iterator.hasNext()) {
                HrSyncDept childDept = iterator.next();
                if (childDept.getParentid().equals(hrSyncDept.getDepartmentId())) {
                    hrSyncDept.getChildDept().add(childDept);
                    iterator.remove();
                    buildHrSyncDeptTree(childDept, new ArrayList<>(hrSyncDeptList));
                }
            }
        }
    }


}
