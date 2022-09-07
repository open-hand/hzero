package org.hzero.plugin.platform.hr.infra.repository.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.algorithm.tree.Node;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.Receiver;
import org.hzero.plugin.platform.hr.api.dto.UnitDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitUserDTO;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.plugin.platform.hr.domain.repository.UnitRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.plugin.platform.hr.infra.mapper.UnitMapper;
import org.hzero.plugin.platform.hr.infra.utils.CollectionSubUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 部门 资源库实现
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 17:05:02
 */
@Component
public class UnitRepositoryImpl extends BaseRepositoryImpl<Unit> implements UnitRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitRepositoryImpl.class);

    @Autowired
    private UnitMapper unitMapper;
    @Autowired
    private LovAdapter lovAdapter;

    @Override
    @ProcessLovValue
    public List<UnitDTO> selectDepartment(Unit queryParam) {
        Assert.notNull(queryParam, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(queryParam.getTenantId(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(queryParam.getUnitCompanyId(), BaseConstants.ErrorCode.DATA_INVALID);
        List<String> topUnitCodes = getTopUnitCodes(queryParam.getTenantId());
        List<UnitDTO> unitDTOS = this.unitMapper.selectDepartment(queryParam, topUnitCodes);
        processQueryUnitWithCondition(queryParam, unitDTOS, topUnitCodes);
        return unitDTOS;
    }

    @Override
    @ProcessLovValue
    public List<UnitDTO> selectCompany(Unit queryParam) {
        Assert.notNull(queryParam, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(queryParam.getTenantId(), BaseConstants.ErrorCode.DATA_INVALID);
        return this.unitMapper.selectCompany(queryParam, getTopUnitCodes(queryParam.getTenantId()));
    }

    @Override
    public int selectRepeatCodeCount(Unit queryParam) {
        return this.unitMapper.selectRepeatCodeCount(queryParam);
    }

    @Override
    public int updateEnabledFlag(Unit unit) {
        return this.unitMapper.updateEnabledFlag(unit);
    }

    @Override
    public int selectSupervisorCountByParentUnitId(Unit unit) {
        return this.unitMapper.selectSupervisorCountByParentUnitId(unit);
    }

    @Override
    public List<Unit> selectChildrenLevelPathById(Long unitId) {
        return this.unitMapper.selectChildrenLevelPathById(unitId);
    }

    /**
     * 获取所有部门
     *
     * @param tenantId 租户id
     * @return 部门
     */
    @Override
    @ProcessLovValue
    public List<UnitDTO> selectAllDepartment(Long tenantId, Integer enabledFlag) {
        return unitMapper.selectAllDepartment(tenantId, enabledFlag, getTopUnitCodes(tenantId));
    }

    /**
     * 查询部门
     *
     * @param tenantId 租户id
     * @param parentUnitIds 父部门id集合
     * @param unitIds 部门id集合
     * @return 部门
     */
    @Override
    @ProcessLovValue
    public List<UnitDTO> selectDepartment(Long tenantId, List<Long> parentUnitIds, List<Long> unitIds) {
        Optional<List<Long>> optPIds = Optional.ofNullable(parentUnitIds);
        Optional<List<Long>> optUIds = Optional.ofNullable(unitIds);
        if (!optPIds.isPresent() && !optUIds.isPresent()) {
            return new ArrayList<>();
        }
        return unitMapper.hcbmSelectDepartment(tenantId, optPIds.filter(CollectionUtils::isNotEmpty).orElse(null),
                        optUIds.filter(CollectionUtils::isNotEmpty).orElse(null), getTopUnitCodes(tenantId));
    }

    @Override
    public List<UnitDTO> selectByTypeCodes(String[] typeCodes, Long tenantId) {
        return unitMapper.selectByTypeCodes(typeCodes, tenantId);
    }

    @Override
    public List<Unit> listRecentUnit(Long tenantId, Date after) {
        return unitMapper.selectByCondition(Condition.builder(Unit.class)
                        .andWhere(Sqls.custom().andEqualTo(Unit.FIELD_TENANT_ID, tenantId, true)
                                        .andGreaterThan(Unit.FIELD_LAST_UPDATE_DATE, after))
                        .build());
    }

    @Override
    public Set<Receiver> listDepartmentUsers(List<UnitUserDTO> units, boolean includeChildDepartment,
                    List<String> typeCode) {
        Set<Receiver> resultSet = new HashSet<>();
        for (UnitUserDTO unit : units) {
            List<Receiver> receivers = unitMapper.selectDepartmentUsers(unit, includeChildDepartment, typeCode);
            if (CollectionUtils.isNotEmpty(receivers)) {
                resultSet.addAll(receivers);
            }
        }
        return resultSet;
    }

    @Override
    public List<Unit> selectAndSetCurrentLevelPath(List<Unit> units) {
        return unitMapper.selectAndSetCurrentLevelPath(units);
    }

    @Override
    public Unit selectAndSetOneCurrentLevelPath(Unit unit) {
        return unitMapper.selectAndSetOneCurrentLevelPath(unit);
    }

    @Override
    @ProcessLovValue
    public List<UnitDTO> selectRootNodeCompany(Long tenantId) {
        return unitMapper.selectRootNodeCompany(tenantId, getTopUnitCodes(tenantId));
    }

    @Override
    public List<UnitDTO> selectSubNodeCompany(Long tenantId, Long unitId) {
        return unitMapper.selectSubNodeCompany(tenantId, unitId, getTopUnitCodes(tenantId));
    }

    @Override
    @ProcessLovValue
    public Page<UnitDTO> pageCompanyUnits(Unit unit, PageRequest pageRequest) {
        Page<UnitDTO> pages = PageHelper.doPage(pageRequest, () -> unitMapper.selectPageCompany(unit, getTopUnitCodes(unit.getTenantId())));
        List<UnitDTO> content = pages.getContent();
        // 拼接名称层级
        content.forEach(unitItem -> {
            // 解析level_path获取code
            List<String> nameLevelPaths = this.parseLevelPath(unitItem);
            // 设置名称levelPath
            unitItem.setNameLevelPaths(nameLevelPaths);
        });
        pages.setContent(content);
        return pages;
    }

    @Override
    public List<UnitDTO> treeDepartmentByCondition(Long tenantId, List<Long> unitIds, Integer enabledFlag) {
        // 初始化传入的集合参数
        if (CollectionUtils.isEmpty(unitIds)) {
            return Collections.emptyList();
        }
        List<UnitDTO> deptList = listDepartmentByCondition(tenantId, unitIds, enabledFlag);
        return TreeBuilder.buildTree(deptList, new Node<Object, UnitDTO>() {
            @Override
            public Object getParentKey(UnitDTO obj) {
                return obj.getParentUnitId();
            }

            @Override
            public Object getKey(UnitDTO obj) {
                return obj.getUnitId();
            }
        });
    }

    @Override
    @ProcessLovValue
    public List<UnitDTO> listDepartmentByCondition(Long tenantId, List<Long> unitIds, Integer enabledFlag) {
        // 初始化传入的集合参数
        if (CollectionUtils.isEmpty(unitIds)) {
            return Collections.emptyList();
        }
        return unitMapper.selectDepartmentByCondition(tenantId, unitIds, enabledFlag, getTopUnitCodes(tenantId));
    }

    /**
     * 解析层级获取unit_code
     * 
     * @param unitItem unit
     * @return nameLevelPath
     */
    private List<String> parseLevelPath(UnitDTO unitItem) {
        String[] unitCodes = StringUtils.split(unitItem.getLevelPath(), BaseConstants.Symbol.VERTICAL_BAR);
        Assert.notNull(unitCodes, BaseConstants.ErrorCode.NOT_NULL);
        return unitMapper.selectUnitNameByTenantAndCode(unitItem.getTenantId(), unitCodes,
                getTopUnitCodes(unitItem.getTenantId()));
    }

    @Override
    public List<UnitDTO> selectAllUnit(Long tenantId) {
        return unitMapper.selectAllUnit(tenantId);
    }

    /**
     * 获取最上级组织类型编码信息
     *
     * @return 最上级组织类型编码
     */
    @Override
    public List<String> getTopUnitCodes(Long tenantId) {
        List<LovValueDTO> lovValues =
                lovAdapter.queryLovValue(PlatformHrConstants.UnitType.TOP_UNIT_CODE, tenantId);
        if (CollectionUtils.isEmpty(lovValues)) {
            // 查询0租户下的值集值信息
            lovValues =
                    lovAdapter.queryLovValue(PlatformHrConstants.UnitType.TOP_UNIT_CODE, BaseConstants.DEFAULT_TENANT_ID);
            if (CollectionUtils.isEmpty(lovValues)) {
                // 0租户下没有该值集信息，打印日志并设置默认值（C,G）
                LOGGER.info("-------------->>>>get unitTypeCodes failed, lovCode is : {}", PlatformHrConstants.UnitType.TOP_UNIT_CODE);
                List<String> initList = new ArrayList<>();
                initList.add("C");
                initList.add("G");
                return initList;
            }
        }
        // 获取所有最上级（公司、集团...）组织编码
        return lovValues.stream()
                .filter(x -> PlatformHrConstants.UnitType.TOP.equals(x.getTag()))
                .map(LovValueDTO::getValue)
                .collect(Collectors.toList());

    }

	@Override
    @ProcessLovValue
	public List<UnitDTO> selectPlusCompany(String keyWord, Long tenantId, String lang) {
		return unitMapper.selectPlusCompany(keyWord, tenantId, lang);
	}
	
	@Override
    @ProcessLovValue
	public List<UnitDTO> selectPlusDepartment(String keyWord,Long unitCompanyId, Long tenantId, String lang) {
		return unitMapper.selectPlusDepartment(keyWord, unitCompanyId, tenantId, lang);
	}

	@Override
	public Page<EmployeeDTO> pageUnitUsers(Long tenantId,Long unitId,String keyWord,String status,Integer primaryPositionFlag,PageRequest pageRequest) {
		return PageHelper.doPage(pageRequest, () -> unitMapper.queryAllUnitEmployees(unitId, tenantId,keyWord,status,primaryPositionFlag));
	}

	@Override
    @ProcessLovValue
	public Page<UnitDTO> pageUnitDept(Long unitId, Long tenantId, String keyWord, Integer enabledFlag,PageRequest pageRequest) {
		return PageHelper.doPage(pageRequest, () -> unitMapper.queryUnitDept(unitId, tenantId,keyWord,enabledFlag));
	}

	@Override
	public List<Unit> queryParentUnitsByLevelPath(String levelPath, Long tenantId) {
		return unitMapper.queryParentUnitsByLevelPath(levelPath, tenantId);
	}

    @Override
    public List<Unit> selectParentUnit(List<Long> unitIds) {
        return unitMapper.selectParentUnit(unitIds);
    }

    @Override
    public Set<Receiver> listOpenDepartmentUsers(List<UnitUserDTO> units, boolean includeChildDepartment, String thirdPlatformType) {
        Set<Receiver> resultSet = new HashSet<>();
        for (UnitUserDTO unit : units) {
            List<Receiver> receivers = unitMapper.selectOpenDepartmentUsers(unit, includeChildDepartment, thirdPlatformType);
            if (CollectionUtils.isNotEmpty(receivers)) {
                resultSet.addAll(receivers);
            }
        }
        return resultSet;
    }

    @Override
    public Page<Unit> listCompanyUnits(Long tenantId, Long unitId, String unitCode, String unitName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> unitMapper.selectCompanyUnits(tenantId, unitId, unitCode, unitName));
    }

    @Override
    public Page<Unit> listDepartmentUnits(Long tenantId, Long unitCompanyId, Long unitId, String unitCode, String unitName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> unitMapper.listDepartmentUnits(tenantId, unitCompanyId, unitId, unitCode, unitName));
    }

    private void processQueryUnitWithCondition(Unit queryParam, List<UnitDTO> units, List<String> topUnitCodes) {
        if (StringUtils.isNotBlank(queryParam.getUnitCode()) || StringUtils.isNotBlank(queryParam.getUnitName())) {
            Set<String> unitCodes = new HashSet<>();
            // 查询levelPath层级值，并解密levelPath再返回
            for (UnitDTO unitDTO : units) {
                String levelPath = unitDTO.getLevelPath();
                String unitCode = unitDTO.getUnitCode();
                String substr =
                        StringUtils.substringBefore(levelPath, StringUtils.join(BaseConstants.Symbol.VERTICAL_BAR, unitCode));
                if (StringUtils.isNotBlank(substr)) {
                    String[] split = StringUtils.split(substr, BaseConstants.Symbol.VERTICAL_BAR);
                    unitCodes.addAll(Arrays.asList(split));
                }
            }
            // 处理oracle数据库 in-list 最大长度1000 的情况
            List<Set<String>> unitCodeSetList = CollectionSubUtils.subSet(unitCodes, 999);
            unitCodeSetList.forEach(unitCodeSet ->{
                List<UnitDTO> topUnitDTOS =
                        this.unitMapper.getDepartmentsByUnitCodes(queryParam.getTenantId(), queryParam.getUnitCompanyId(), unitCodeSet, topUnitCodes);
                units.addAll(topUnitDTOS);
            });
        }
    }
}
