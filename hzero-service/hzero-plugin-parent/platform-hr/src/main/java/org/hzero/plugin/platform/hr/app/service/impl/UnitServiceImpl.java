package org.hzero.plugin.platform.hr.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.Receiver;
import org.hzero.plugin.platform.hr.api.dto.UnitDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitUserDTO;
import org.hzero.plugin.platform.hr.app.service.UnitService;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.plugin.platform.hr.domain.repository.UnitRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 部门应用服务默认实现
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 17:05:02
 */
@Service
public class UnitServiceImpl implements UnitService {
    private UnitRepository unitRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<UnitDTO> treeCompany(Long tenantId) {
        Unit queryParam = new Unit();
        queryParam.setTenantId(tenantId);
        queryParam.setTreeFlag(Unit.FLAG_Y);
        List<UnitDTO> units = this.unitRepository.selectCompany(queryParam);
        return TreeBuilder.buildTree(units, Unit.ROOT_KEY, UnitDTO::getUnitId,
                (unit) -> unit.getParentUnitId() == null ? Unit.ROOT_KEY : unit.getParentUnitId());
    }

    @Override
    public List<UnitDTO> treeDepartment(Long tenantId, Long unitCompanyId, Integer enabledFlag) {
        Unit queryParam = new Unit();
        queryParam.setTenantId(tenantId);
        queryParam.setUnitCompanyId(unitCompanyId);
        queryParam.setTreeFlag(Unit.FLAG_Y);
        queryParam.setEnabledFlag(enabledFlag);
        List<UnitDTO> units = this.unitRepository.selectDepartment(queryParam);
        return TreeBuilder.buildTree(units, unitCompanyId, UnitDTO::getUnitId, UnitDTO::getParentUnitId);
    }


    @Override
    public List<Unit> batchInsertSelective(List<Unit> units) {
        if (CollectionUtils.isEmpty(units)) {
            return units;
        }
        // 校验数据
        units.forEach(unit -> unit.validate(this.unitRepository));
        // 先算出所属层级值，再插入
        List<Unit> selectList = new ArrayList<>();
        // 最上级公司或组织参数集合
        List<Unit> companyOrGroupList = new ArrayList<>();
        units.forEach(unit -> {
            if (unit.getParentUnitId() != null) {
                // 将存在父级Id的参数查询出来 用于获取父级levelPath
                selectList.add(unit);
            } else {
                // 设置levelPath
                unit.setLevelPath(unit.getUnitCode());
                companyOrGroupList.add(unit);
            }
        });
        if (CollectionUtils.isNotEmpty(selectList)) {
            List<Unit> resultUnits = unitRepository.selectAndSetCurrentLevelPath(selectList);
            // 组装levelPath， parentUnitId作为Key
            Map<Long, Unit> resultMap = resultUnits.stream().collect(Collectors.toMap(Unit::getUnitId, result -> result));
            this.generateLevelPath(selectList, resultMap);
        }
        selectList.addAll(companyOrGroupList);
        unitRepository.batchInsertSelective(selectList);
        return units;
    }

    /**
     * 生成levelPath
     *
     * @param selectList list
     * @param resultMap  map
     */
    private void generateLevelPath(List<Unit> selectList, Map<Long, Unit> resultMap) {
        selectList.forEach(lst -> {
            if (resultMap.containsKey(lst.getParentUnitId())) {
                // 生成当前参数的levelPath
                String join = StringUtils.join(resultMap.get(lst.getParentUnitId()).getLevelPath(),
                        BaseConstants.Symbol.VERTICAL_BAR, lst.getUnitCode());
                lst.setLevelPath(join);
            }
        });
    }

    @Override
    public Unit updateByPrimaryKey(Unit unit) {
        if (unit == null) {
            return null;
        }
        if (unit.getParentUnitId() == null && unit.getUnitCompanyId() != null){
            // 编辑部门，设置了父级Id为空，此时是要设置当前部门为指定组织下的顶级部门
            unit.setParentUnitId(unit.getUnitCompanyId());
        }
        unit.validate(this.unitRepository);
        Unit unitInDb = this.unitRepository.selectByPrimaryKey(unit);
        Assert.notNull(unitInDb, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 防止equals空指针
        if (unit.getParentUnitId() == null) {
            if (unitInDb.getParentUnitId() == null) {
                // 未改变层级
                this.unitRepository.updateByPrimaryKey(unit);
                return unit;
            } else {
                // 更新下级level_path
                this.refreshLevelPath(unit, unitInDb.getLevelPath(), this.unitRepository);
                // 更新当前组织
                this.unitRepository.updateByPrimaryKey(unit);
                return unit;
            }
        } else {
            // 判断是否更改上级
            if (unit.getParentUnitId().equals(unitInDb.getParentUnitId())) {
                // 未改变层级
                this.unitRepository.updateByPrimaryKey(unit);
                return unit;
            } else {
                // 更新下级level_path
                this.refreshLevelPath(unit, unitInDb.getLevelPath(), this.unitRepository);
                // 更新当前组织
                this.unitRepository.updateByPrimaryKey(unit);
                return unit;
            }
        }
    }

    @Override
    public boolean disableUnit(Long unitId, Long tenantId, Long objectVersionNumber) {
        // 数据校验
        Assert.notNull(unitId, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(objectVersionNumber, BaseConstants.ErrorCode.DATA_INVALID);
        Unit unit = this.unitRepository.selectByPrimaryKey(unitId);
        Assert.notNull(unit, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.isTrue(Objects.equals(unit.getTenantId(), tenantId), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.isTrue(Objects.equals(objectVersionNumber, unit.getObjectVersionNumber()),
                BaseConstants.ErrorCode.OPTIMISTIC_LOCK);
        List<Unit> unitList = new ArrayList<>(BaseConstants.Digital.SIXTEEN);
        // 递归查询节点
        findChildUnit(unitList, unit, tenantId);
        Assert.isTrue(CollectionUtils.isNotEmpty(unitList), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 更新
        unitList.forEach(unitItem -> {
            unitItem.setEnabledFlag(BaseConstants.Flag.NO);
            this.unitRepository.updateEnabledFlag(unitItem);
        });
        return true;
    }

    private List<Unit> findChildUnit(List<Unit> unitList, Unit unit, long tenantId) {
        if (unit != null) {
            // 如果unit不为空，则将unit添加到unitList集合中
            unitList.add(unit);
        }
        // 根据传入的父节点id查出子节点对象，用list接收
        Long unitId = unit == null ? null : unit.getUnitId();
        List<Unit> units = unitRepository.select(new Unit().setTenantId(tenantId).setParentUnitId(unitId));

        // 递归查询，遍历list，调用自己，unitId传入子节点id进行递归
        if (CollectionUtils.isNotEmpty(units)) {
            for (Unit unitItem : units) {
                findChildUnit(unitList, unitItem, tenantId);
            }
        }
        return unitList;
    }

    @Override
    public boolean enableUnit(Long unitId, Long tenantId, Long objectVersionNumber) {
        // 数据校验
        Assert.notNull(unitId, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(objectVersionNumber, BaseConstants.ErrorCode.DATA_INVALID);
        Unit unit = this.unitRepository.selectByPrimaryKey(unitId);
        Assert.notNull(unit, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.isTrue(Objects.equals(unit.getTenantId(), tenantId), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Assert.isTrue(Objects.equals(objectVersionNumber, unit.getObjectVersionNumber()),
                BaseConstants.ErrorCode.OPTIMISTIC_LOCK);
        if (unit.getParentUnitId() != null) {
            Unit parentUnit = this.unitRepository.selectByPrimaryKey(unit.getParentUnitId());
            Assert.notNull(parentUnit, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            Assert.isTrue(Objects.equals(parentUnit.getEnabledFlag(), BaseConstants.Flag.YES),
                    PlatformHrConstants.ErrorCode.ERROR_UNIT_PARENT_DISABLED);
        }

        // 递归查询节点
        List<Unit> unitList = new ArrayList<>(BaseConstants.Digital.SIXTEEN);
        findChildUnit(unitList, unit, tenantId);
        Assert.isTrue(CollectionUtils.isNotEmpty(unitList), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 更新
        unitList.forEach(unitItem -> {
            unitItem.setEnabledFlag(BaseConstants.Flag.YES);
            this.unitRepository.updateEnabledFlag(unitItem);
        });
        return true;
    }

    @Override
    public boolean checkEnabled(Long unitId) {
        Assert.notNull(unitId, BaseConstants.ErrorCode.DATA_INVALID);
        Unit unit = this.unitRepository.selectByPrimaryKey(unitId);
        return unit != null && Objects.equals(BaseConstants.Flag.YES, unit.getEnabledFlag());
    }

    @Override
    public List<UnitDTO> selectCompany(Unit queryParam) {
        List<UnitDTO> resultList = this.unitRepository.selectCompany(queryParam);
        return TreeBuilder.buildTree(resultList, Unit.ROOT_KEY, UnitDTO::getUnitId,
                (unit) -> unit.getParentUnitId() == null ? Unit.ROOT_KEY : unit.getParentUnitId());
    }

    @Override
    public List<UnitDTO> selectDepartment(Unit queryParam) {
        List<UnitDTO> resultList = this.unitRepository.selectDepartment(queryParam);
        return TreeBuilder.buildTree(resultList, queryParam.getUnitCompanyId(), UnitDTO::getUnitId,
                UnitDTO::getParentUnitId);
    }

    @Override
    public List<UnitDTO> selectAllDepartment(Long tenantId, Integer enabledFlag) {
        return unitRepository.selectAllDepartment(tenantId, enabledFlag);
    }

    @Override
    public List<UnitDTO> selectDepartment(Long tenantId, List<Long> parentUnitIds, List<Long> unitIds) {
        return unitRepository.selectDepartment(tenantId, parentUnitIds, unitIds);
    }

    @Override
    public List<UnitDTO> selectByTypeCodes(String[] typeCodes, Long tenantId) {
        return unitRepository.selectByTypeCodes(typeCodes, tenantId);
    }

    @Override
    public List<Unit> listRecentUnit(Long tenantId, long before) {
        return unitRepository.listRecentUnit(tenantId, new Date(System.currentTimeMillis() - before));
    }

    @Override
    public List<UnitDTO> lazyTreeCompany(Long tenantId, Long unitId) {
        // 树形懒加载查询公司级组织, 未传递 unitId 时，先查询最上级公司组织列表
        if (unitId == null) {
            List<UnitDTO> topUnits = unitRepository.selectRootNodeCompany(tenantId);
            return TreeBuilder.buildTree(topUnits, Unit.ROOT_KEY, UnitDTO::getUnitId,
                    (unit) -> unit.getParentUnitId() == null ? Unit.ROOT_KEY : unit.getParentUnitId());
        } else {
            // 传递上级公司Id查询下级参数
            List<UnitDTO> nextUnits = unitRepository.selectSubNodeCompany(tenantId, unitId);
            return TreeBuilder.buildTree(nextUnits, unitId, UnitDTO::getUnitId,
                    (unit) -> unit.getParentUnitId() == null ? unitId : unit.getParentUnitId());
        }
    }

    @Override
    public Page<UnitDTO> pageCompanyUnits(Unit unit, PageRequest pageRequest) {
        // 分页查询组织信息，需要根据组织层级按照顺序进行展示。
        return unitRepository.pageCompanyUnits(unit, pageRequest);
    }

    /**
     * 刷新层级路径(自己和所有子节点)<br/>
     * 更新时请在所有变更都保存到数据库之后使用
     *
     * @param updateUnit     需要更新的Unit
     * @param oldLevelPath   旧层级路径,新增情况下请传null
     * @param unitRepository 部门仓库
     */
    private void refreshLevelPath(Unit updateUnit, String oldLevelPath, UnitRepository unitRepository) {
        Assert.notNull(updateUnit.getUnitId(), BaseConstants.ErrorCode.DATA_INVALID);
        // 查出所有下级组织信息,返回[unitId, unitCode, levelPath, parentUnitId, parentLevelPath, unitCompanyId]
        List<Unit> units = unitRepository.selectChildrenLevelPathById(updateUnit.getUnitId());
        if (updateUnit.getParentUnitId() == null) {
            // 修改为最上级组织
            updateUnit.setLevelPath(updateUnit.getUnitCode());
        } else {
            // 更改上级组织，需要调整层级
            Unit dbUnit = unitRepository.selectAndSetOneCurrentLevelPath(updateUnit);
            Assert.notNull(dbUnit, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            // FIX 20200427 若父级层级为公司或组织且当前unit不为组织或公司，则需更新unitCompanyId的值为当前parentUnitId的值
            List<String> topUnitCodes = unitRepository.getTopUnitCodes(updateUnit.getTenantId());
            if (Objects.isNull(dbUnit.getUnitCompanyId()) && !topUnitCodes.contains(updateUnit.getUnitTypeCode())) {
                // 处理unitCompanyId的值
                updateUnit.setUnitCompanyId(updateUnit.getParentUnitId());
            }
            updateUnit.setLevelPath(StringUtils.join(dbUnit.getLevelPath(), BaseConstants.Symbol.VERTICAL_BAR,
                    updateUnit.getUnitCode()));
        }
        units.stream().peek(unit -> {
            if (updateUnit.getUnitCompanyId() != null) {
                unit.setUnitCompanyId(updateUnit.getUnitCompanyId());
            }
            unit.setLevelPath(StringUtils.join(updateUnit.getLevelPath(), unit.getLevelPath().substring(oldLevelPath.length())));
        }).forEach(unit -> unitRepository.updateOptional(unit, Unit.UNIT_COMPANY_ID, Unit.FIELD_LEVEL_PATH));
    }

    @Override
    public List<UnitDTO> selectPlusCompany(String keyWord, Long tenantId) {
        String lang = LanguageHelper.language();
        List<UnitDTO> resultList = this.unitRepository.selectPlusCompany(keyWord, tenantId, lang);
        return resultList;
        //return TreeBuilder.buildTree(resultList, Unit.ROOT_KEY, UnitDTO::getUnitId,(unit) -> unit.getParentUnitId() == null ? Unit.ROOT_KEY : unit.getParentUnitId());
    }

    @Override
    public List<UnitDTO> selectPlusDepartment(String keyWord, Long unitCompanyId, Long tenantId) {
        String lang = LanguageHelper.language();
        List<UnitDTO> resultList = this.unitRepository.selectPlusDepartment(keyWord, unitCompanyId, tenantId, lang);
        return resultList;
        //return TreeBuilder.buildTree(resultList, Unit.ROOT_KEY, UnitDTO::getUnitId,UnitDTO::getParentUnitId);
    }

    @Override
    public Page<EmployeeDTO> pageUnitUsers(Long tenantId, Long unitId, String keyWord, String status, Integer primaryPositionFlag, PageRequest pageRequest) {
        return unitRepository.pageUnitUsers(tenantId, unitId, keyWord, status, primaryPositionFlag, pageRequest);
    }

    @Override
    public Page<UnitDTO> pageUnitDept(Long tenantId, Long unitId, String keyWord, Integer enabledFlag, PageRequest pageRequest) {
        return unitRepository.pageUnitDept(unitId, tenantId, keyWord, enabledFlag, pageRequest);
    }

    @Override
    public Set<Receiver> listOpenDepartmentUsers(List<UnitUserDTO> units, boolean includeChildDepartment, String thirdPlatformType) {
        return unitRepository.listOpenDepartmentUsers(units, includeChildDepartment, thirdPlatformType);
    }

    @Override
    public Page<Unit> listCompanyUnits(Long tenantId, Long unitId, String unitCode, String unitName, PageRequest pageRequest) {
        return unitRepository.listCompanyUnits(tenantId, unitId, unitCode, unitName, pageRequest);
    }

    @Override
    public Page<Unit> listDepartmentUnits(Long tenantId, Long unitCompanyId, Long unitId, String unitCode, String unitName, PageRequest pageRequest) {
        return unitRepository.listDepartmentUnits(tenantId, unitCompanyId, unitId, unitCode, unitName, pageRequest);
    }
}
