package org.hzero.plugin.platform.hr.infra.repository.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.PositionDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitPositionDTO;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.plugin.platform.hr.infra.mapper.PositionMapper;
import org.hzero.plugin.platform.hr.infra.utils.CollectionSubUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 组织架构岗位仓库实现类
 * </p>
 *
 * @author qingsheng.chen 2018/6/20 星期三 14:54
 */
@Component
public class PositionRepositoryImpl extends BaseRepositoryImpl<Position> implements PositionRepository {
    private PositionMapper positionMapper;

    @Autowired
    public PositionRepositoryImpl(PositionMapper positionMapper) {
        this.positionMapper = positionMapper;
    }

    @Override
    public int querySupervisorPositionFlagCount(Long tenantId, Long unitId, Long positionId) {
        Integer supervisorPositionFlagCnt = positionMapper.querySupervisorPositionFlagCount(tenantId, unitId, positionId);
        return supervisorPositionFlagCnt == null ? 0 : supervisorPositionFlagCnt;
    }

    @Override
    public List<PositionDTO> selectByPositionCodeAndName(long tenantId, long unitId, String positionCode, String positionName) {
        List<PositionDTO> positionDTOS =
                positionMapper.selectByPositionCodeAndName(tenantId, unitId, positionCode, positionName);
        if (!StringUtils.isEmpty(positionCode) || !StringUtils.isEmpty(positionName)) {
            processQueryPositionWithCondition(tenantId, unitId, positionDTOS);
        }
        return positionDTOS;
    }

    @Override
    public List<Position> selectWithChild(Long tenantId, String levelPath) {
        return positionMapper.selectWithChild(tenantId, levelPath);
    }

    @Override
    public List<UnitPositionDTO> listUnit(long tenantId, String type, String name, List<Long> unitIdList) {
        if (!Objects.equals(PlatformHrConstants.UnitType.POSITION, type) && StringUtils.hasText(name)) {
            // 查询符合条件的记录
            List<UnitPositionDTO> unitPositionList = positionMapper.listUnit(tenantId, type, name, null);
            if (!CollectionUtils.isEmpty(unitPositionList)) {
                // 查询长辈及子辈
                unitPositionList.addAll(positionMapper.listKinsfolk(tenantId, unitPositionList.stream().map(UnitPositionDTO::getLevelPath).collect(Collectors.toList())));
            }
            return unitPositionList.stream().distinct().collect(Collectors.toList());
        } else if (Objects.equals(PlatformHrConstants.UnitType.POSITION, type) && StringUtils.hasText(name)) {
            // 查询符合条件的记录
            List<UnitPositionDTO> unitPositionList = positionMapper.listUnit(tenantId, null, null, unitIdList);
            if (!CollectionUtils.isEmpty(unitPositionList)) {
                // 查询长辈
                unitPositionList.addAll(positionMapper.listParents(tenantId, unitPositionList.stream().map(UnitPositionDTO::getLevelPath).collect(Collectors.toList())));
            }
            return unitPositionList.stream().distinct().collect(Collectors.toList());
        } else {
            return positionMapper.listUnit(tenantId, null, null, null).stream().distinct().collect(Collectors.toList());
        }
    }

    @Override
    public List<UnitPositionDTO> listUnitPosition(long tenantId, long employeeId, String type, String name, List<Long> departmentIdList) {
        if (PlatformHrConstants.UnitType.POSITION.equals(type) && StringUtils.hasText(name)) {
            // 查询条件为岗位
            List<UnitPositionDTO> positionList = positionMapper.listUnitPosition(tenantId, employeeId, type, name, null);
            if (!CollectionUtils.isEmpty(positionList)) {
                // 查询父子结点
                positionList.addAll(positionMapper.listKinsfolkPosition(tenantId, employeeId, positionList.stream().map(UnitPositionDTO::getLevelPath).collect(Collectors.toList())));
            }
            return positionList.stream().distinct().collect(Collectors.toList());
        } else if (!PlatformHrConstants.UnitType.POSITION.equals(type) && StringUtils.hasText(name)) {
            // 查询条件为部门或无条件
            List<UnitPositionDTO> positionList = positionMapper.listUnitPosition(tenantId, employeeId, null, null, departmentIdList);
            if (!CollectionUtils.isEmpty(positionList)) {
                // 查询子节点
                positionList.addAll(positionMapper.listChildrenPosition(tenantId, employeeId, positionList.stream().map(UnitPositionDTO::getLevelPath).collect(Collectors.toList())));
            }
            return positionList.stream().distinct().collect(Collectors.toList());
        } else {
            return positionMapper.listUnitPosition(tenantId, employeeId, null, null, null).stream().distinct().collect(Collectors.toList());
        }
    }

    @Override
    public List<Position> listRecentPosition(Long tenantId, Date after) {
        return positionMapper.selectByCondition(Condition.builder(Position.class)
                .andWhere(Sqls.custom().andEqualTo(Position.FIELD_TENANT_ID, tenantId, true)
                        .andGreaterThan(Position.FIELD_LAST_UPDATE_DATE, after))
                .build());
    }

    @Override
    public Position queryPositionByCode(String positionCode, Long tenantId) {
        List<Position> positions = positionMapper.selectByCondition(Condition.builder(Position.class)
                .notSelect(Position.FIELD_CREATED_BY, Position.FIELD_CREATION_DATE, Position.FIELD_LAST_UPDATE_DATE, Position.FIELD_LAST_UPDATED_BY)
                .andWhere(Sqls.custom().andEqualTo(Position.FIELD_POSITION_CODE, positionCode)
                        .andEqualTo(Position.FIELD_TENANT_ID, tenantId))
                .build());
        return CollectionUtils.isEmpty(positions) ? null : positions.get(0);
    }

    @Override
    public List<Position> queryAllPositionsByTenant(Long tenantId) {
        return positionMapper.selectByCondition(Condition.builder(Position.class)
                .notSelect(Position.FIELD_CREATED_BY, Position.FIELD_CREATION_DATE, Position.FIELD_LAST_UPDATE_DATE, Position.FIELD_LAST_UPDATED_BY)
                .andWhere(Sqls.custom()
                        .andEqualTo(Position.FIELD_TENANT_ID, tenantId))
                .build());
    }

    @Override
    public Page<Position> pagePosition(PageRequest pageRequest, Position position) {
        return PageHelper.doPageAndSort(pageRequest, () -> positionMapper.selectPosition(position.getTenantId(), position.getPositionCode(), position.getPositionName(), position.getCompanyName()));
    }

    @Override
    public Page<Position> pagePositionByUnit(PageRequest pageRequest, Long tenantId, Long unitId, String keyWord) {
        return PageHelper.doPage(pageRequest, () -> positionMapper.selectPositionByUnit(tenantId, unitId, keyWord));
    }

    @Override
    public List<PositionDTO> selectPlusPositionTree(Long tenantId, Long unitCompanyId, Long unitId, String keyWord) {
        return positionMapper.selectPlusPositionTree(tenantId, unitCompanyId, unitId, keyWord);
    }

    @Override
    public Page<EmployeeDTO> pagePositionUsers(Long tenantId, Long positionId, String keyWord, String status,
                                               Integer primaryPositionFlag, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> positionMapper.queryEmployeesByPositionId(positionId, tenantId, keyWord, status, primaryPositionFlag));
    }

    private void processQueryPositionWithCondition(long tenantId, long unitId, List<PositionDTO> positions) {
        if (CollectionUtils.isEmpty(positions)) {
            return ;
        }
        Set<String> positionCodes = new HashSet<>();
        for (PositionDTO positionDTO : positions) {
            String levelPath = positionDTO.getLevelPath();
            String positionCode = positionDTO.getPositionCode();
            String substr = org.apache.commons.lang3.StringUtils.substringBefore(levelPath,
                    org.apache.commons.lang3.StringUtils.join(BaseConstants.Symbol.VERTICAL_BAR,
                            positionCode));
            if (!StringUtils.isEmpty(substr) && !substr.equals(levelPath)) {
                String[] split = org.apache.commons.lang3.StringUtils.split(substr, BaseConstants.Symbol.VERTICAL_BAR);
                positionCodes.addAll(Arrays.asList(split));
            }
        }
        if (CollectionUtils.isEmpty(positionCodes)) {
            return ;
        }
        // 处理oracle数据库 in-list 最大长度1000 的情况
        List<Set<String>> positionCodeSetList = CollectionSubUtils.subSet(positionCodes, 999);
        positionCodeSetList.forEach(positionCodeSet ->{
            List<PositionDTO> positionList =
                    this.positionMapper.getPositionsByPositionCodes(tenantId, unitId, positionCodeSet);
            positions.addAll(positionList);
        });
    }
}
