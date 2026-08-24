package org.hzero.plugin.platform.hr.app.service.impl;

import static org.hzero.core.base.BaseConstants.ErrorCode.DATA_NOT_EXISTS;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hzero.core.algorithm.tree.Node;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.PositionDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitPositionDTO;
import org.hzero.plugin.platform.hr.app.service.PositionService;
import org.hzero.plugin.platform.hr.app.service.UnitService;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.hzero.plugin.platform.hr.domain.service.IPositionDomainService;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;

/**
 * <p>
 * 组织架构岗位管理接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/6/20 星期三 14:44
 */
@Service
public class PositionServiceImpl extends BaseServiceImpl<Position> implements PositionService {
    private static final Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);
    private static final Long ROOT_ID = -1L;
    private PositionRepository positionRepository;
    private UnitService unitService;
    private IPositionDomainService positionDomainService;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository, UnitService unitService,
                    IPositionDomainService positionDomainService) {
        this.positionRepository = positionRepository;
        this.unitService = unitService;
        this.positionDomainService = positionDomainService;
    }

    @Override
    public Position queryPosition(long positionId) {
        return positionRepository.selectByPrimaryKey(positionId);
    }

    @Override
    public List<PositionDTO> treePosition(long tenantId, long unitId, String positionCode, String positionName) {
        return TreeBuilder.recursiveBuildTree(findRoot(positionRepository.selectByPositionCodeAndName(tenantId, unitId, positionCode, positionName)), ROOT_ID, new ArrayList<>(), new Node<Long, PositionDTO>() {
            @Override
            public Long getKey(PositionDTO positionDTO) {
                return positionDTO.getPositionId();
            }

            @Override
            public Long getParentKey(PositionDTO positionDTO) {
                return positionDTO.getParentId() == null ? ROOT_ID : positionDTO.getParentId();
            }
        });
    }

    @Override
    public List<UnitPositionDTO> treeUnitPosition(long tenantId, long employeeId, String type, String name) {
        if (!Objects.equals(PlatformHrConstants.UnitType.POSITION, type)) {
            // 查询条件为部门或空
            // 查询unit表的父子节点
            List<UnitPositionDTO> unitPositionList = positionRepository.listUnit(tenantId, type, name, null);
            if (CollectionUtils.isEmpty(unitPositionList)) {
                return unitPositionList;
            }
            Map<Long, List<UnitPositionDTO>> unitMap = unitPositionList.stream()
                    .collect(Collectors.groupingBy(item -> item.getParentId() == null ? ROOT_ID : item.getParentId()));
            Map<Long, List<UnitPositionDTO>> positionMap = positionRepository.listUnitPosition(tenantId, employeeId, type, name,
                    unitPositionList
                            .stream()
                            .filter(item -> PlatformHrConstants.UnitType.DEPARTMENT.equals(item.getType()))
                            .map(UnitPositionDTO::getId).collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.groupingBy(UnitPositionDTO::getUnitId));
            return recursiveBuildUnitPositionTree(unitMap, ROOT_ID, new LinkedList<>(), positionMap);
        } else {
            // 查询条件为岗位
            // 查询position表父子节点
            List<UnitPositionDTO> list = positionRepository.listUnitPosition(tenantId, employeeId, type, name, null);
            if (CollectionUtils.isEmpty(list)) {
                return list;
            }
            Map<Long, List<UnitPositionDTO>> positionMap = list
                    .stream()
                    .collect(Collectors.groupingBy(UnitPositionDTO::getUnitId));
            // 查询unit表的父子节点
            List<UnitPositionDTO> unitPositionList = positionRepository.listUnit(tenantId, type, name, list.stream().map(UnitPositionDTO::getUnitId).collect(Collectors.toList()));
            Map<Long, List<UnitPositionDTO>> unitMap = unitPositionList.stream()
                    .collect(Collectors.groupingBy(item -> item.getParentId() == null ? ROOT_ID : item.getParentId()));
            return recursiveBuildUnitPositionTree(unitMap, ROOT_ID, new LinkedList<>(), positionMap);
        }
    }

    @Override
    public List<Position> listRecentPosition(Long tenantId, long before) {
        return positionRepository.listRecentPosition(tenantId, new Date(System.currentTimeMillis() - before));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Position createPosition(Position position) {
        if (!unitService.checkEnabled(position.getUnitId()) || !unitService.checkEnabled(position.getUnitCompanyId())) {
            throw new CommonException(PlatformHrConstants.ErrorCode.ERROR_POSITION_NOT_ALLOWED);
        }
        positionDomainService.validatePosition(position);
        position.setLevelPath(generateLevelPath(position));
        if (positionRepository.insertSelective(position) != 1) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        }
        return position;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Position> batchCreatePosition(long tenantId, long unitCompanyId, long unitId, List<Position> positionList) {
        positionList.forEach(item -> createPosition(item.setTenantId(tenantId).setUnitCompanyId(unitCompanyId).setUnitId(unitId)));
        return positionList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Position updatePosition(Position position) {
        Position existed = positionDomainService.validatePosition(position);
        if (existed == null) {
            throw new CommonException(DATA_NOT_EXISTS);
        }
        if (position.getEnabledFlag() == null) {
            // 传入标识为null，则设置数据库中的标识到该对象中
            position.setEnabledFlag(existed.getEnabledFlag());
        }
            position
                .setTenantId(existed.getTenantId())
                .setUnitId(existed.getUnitId())
                .setPositionCode(existed.getPositionCode())
                .setLevelPath(generateLevelPath(position, existed));
        positionRepository.updateByPrimaryKey(position);
        return position;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Position> batchUpdatePosition(long tenantId, long unitCompanyId, long unitId, List<Position> positionList) {
        for (Position position : positionList) {
            this.updatePosition(position.setTenantId(tenantId).setUnitCompanyId(unitCompanyId).setUnitId(unitId));
        }
        return positionList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Position> enablePosition(Position position) {
        position = positionRepository.selectOne(position);
        if (position == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        if (position.getParentPositionId() != null) {
            Position parent = positionRepository.selectByPrimaryKey(position.getParentPositionId());
            if (parent == null) {
                logger.error("Position parent not exists, id = {}", position.getParentPositionId());
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }
            if (Objects.equals(parent.getEnabledFlag(), BaseConstants.Flag.NO)) {
                throw new CommonException(PlatformHrConstants.ErrorCode.ERROR_POSITION_NOT_ALLOWED);
            }
        }
        List<Position> positionList = positionRepository.selectWithChild(
                position.getTenantId(), position.getLevelPath());
        positionList.forEach(item -> item.setEnabledFlag(BaseConstants.Flag.YES));
        return positionRepository.batchUpdateByPrimaryKeySelective(positionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Position> disablePosition(Position position) {
        position = positionRepository.selectOne(position);
        if (position == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        List<Position> positionList = positionRepository.selectWithChild(
                position.getTenantId(), position.getLevelPath());
        positionList.forEach(item -> item.setEnabledFlag(BaseConstants.Flag.NO));
        return positionRepository.batchUpdateByPrimaryKeySelective(positionList);
    }

    /**
     * 递归构建公司部门岗位树
     *
     * @param unitMap          公司部门Map
     * @param parentId         父公司部门ID
     * @param unitPositionList 公司部门岗位List
     * @param positionMap      岗位List
     * @return 公司部门岗位树
     */
    private List<UnitPositionDTO> recursiveBuildUnitPositionTree(Map<Long, List<UnitPositionDTO>> unitMap,
                                                                 long parentId,
                                                                 LinkedList<UnitPositionDTO> unitPositionList,
                                                                 Map<Long, List<UnitPositionDTO>> positionMap) {
        if (unitMap.containsKey(parentId)) {
            List<UnitPositionDTO> parentUnitList = unitMap.get(parentId)
                    .stream()
                    .sorted(Comparator.comparingInt(UnitPositionDTO::getOrderSeq))
                    .collect(Collectors.toList());
            for (UnitPositionDTO unit : parentUnitList) {
                if (!PlatformHrConstants.UnitType.POSITION.equals(unit.getType()) && positionMap.containsKey(unit.getId())) {
                    Set<Long> positionIds = positionMap.get(unit.getId()).stream().map(UnitPositionDTO::getId).collect(Collectors.toSet());
                    unit.addChildren(recursiveBuildUnitPositionTree(positionMap.get(unit.getId())
                                    .stream()
                                    .collect(Collectors.groupingBy(item -> (item.getParentId() == null || !positionIds.contains(item.getParentId()))
                                            ? ROOT_ID : item.getParentId())),
                            ROOT_ID,
                            new LinkedList<>(),
                            null));
                }
                if (unitMap.containsKey(unit.getId())) {
                    unitPositionList.add(unit.addChildren(recursiveBuildUnitPositionTree(unitMap, unit.getId(),
                            new LinkedList<>(), positionMap)));
                } else {
                    unitPositionList.add(unit);
                }
            }
        }
        return unitPositionList;
    }

    private String generateLevelPath(Position position) {
        if (position.getParentPositionId() != null) {
            Position parentPosition = positionRepository.selectByPrimaryKey(position.getParentPositionId());
            if (parentPosition == null) {
                throw new CommonException(DATA_NOT_EXISTS);
            }
            if (Objects.equals(parentPosition.getEnabledFlag(), BaseConstants.Flag.NO)) {
                throw new CommonException(PlatformHrConstants.ErrorCode.ERROR_POSITION_NOT_ALLOWED);
            }
            return parentPosition.getLevelPath() + PlatformHrConstants.SPLIT + position.getPositionCode();
        } else {
            return position.getPositionCode();
        }
    }

    private String generateLevelPath(Position position, final Position exists) {
        if (Objects.equals(position.getParentPositionId(), exists.getParentPositionId())) {
            return exists.getLevelPath();
        }
        final String newParentLevelPath = queryParentLevelPath(exists.getTenantId(), position.getParentPositionId());
        final String oldParentLevelPath = queryParentLevelPath(exists.getTenantId(), exists.getParentPositionId());
        // 父级岗位不允许为当前岗位的子岗位/或者当前岗位
        if (newParentLevelPath.startsWith(exists.getLevelPath() + PlatformHrConstants.SPLIT) || Objects.equals(position.getPositionId(), position.getParentPositionId())) {
            throw new CommonException(PlatformHrConstants.ErrorCode.CHILD_CAN_NOT_BE_PARENT);
        }
        List<Position> positionWhitChildren = positionRepository.selectWithChild(exists.getTenantId(), exists.getLevelPath());
        positionWhitChildren.forEach(item -> {
            if (!Objects.equals(item.getPositionId(), position.getPositionId())) {
                positionRepository.updateByPrimaryKeySelective(item.setLevelPath(levelPath(item.getLevelPath(), oldParentLevelPath, newParentLevelPath)));
            }
        });
        return levelPath(exists.getLevelPath(), oldParentLevelPath, newParentLevelPath);
    }

    private String levelPath(String levelPath, String oldParentLevelPath, String newParentLevelPath) {
        if (!StringUtils.hasText(newParentLevelPath)) {
            return levelPath.substring(levelPath.indexOf(oldParentLevelPath) + oldParentLevelPath.length() + 1);
        } else if (!StringUtils.hasText(oldParentLevelPath)) {
            return newParentLevelPath + PlatformHrConstants.SPLIT + levelPath;
        }
        return newParentLevelPath + levelPath.substring(levelPath.indexOf(oldParentLevelPath) + oldParentLevelPath.length());
    }

    private String queryParentLevelPath(Long tenantId, Long parentPositionId) {
        if (parentPositionId != null) {
            Position parent = positionRepository
                    .selectOne(new Position().setTenantId(tenantId).setPositionId(parentPositionId));
            if (parent == null) {
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            }
            return parent.getLevelPath();
        }
        return "";
    }

    private Map<Long, List<PositionDTO>> findRoot(List<PositionDTO> positionList) {
        Map<Long, PositionDTO> positionMap = positionList
                .stream()
                .collect(Collectors.toMap(PositionDTO::getPositionId, Function.identity()));
        for (Map.Entry<Long, PositionDTO> entry : positionMap.entrySet()) {
            PositionDTO position = entry.getValue();
            if (!positionMap.containsKey(position.getParentId())) {
                position.setParentId(ROOT_ID);
            }
        }
        return positionMap.values().stream().collect(Collectors.groupingBy(PositionDTO::getParentId));
    }

	@Override
	public Page<Position> pagePositionByUnit(PageRequest pageRequest, Long tenantId, Long unitId,String keyWord) {
		return positionRepository.pagePositionByUnit(pageRequest, tenantId, unitId,keyWord);
	}

	@Override
	public List<PositionDTO> selectPlusPositionTree(Long tenantId, Long unitCompanyId,Long unitId,String keyWord) {
		 List<PositionDTO> resultList = positionRepository.selectPlusPositionTree(tenantId, unitCompanyId, unitId,keyWord);
		 return TreeBuilder.buildTree(resultList, null, PositionDTO::getPositionId,PositionDTO::getParentPositionId);
	}

	@Override
	public Page<EmployeeDTO> pagePositionUsers(Long tenantId, Long positionId, String keyWord, String status,
	                Integer primaryPositionFlag, PageRequest pageRequest) {
		return positionRepository.pagePositionUsers(tenantId, positionId, keyWord, status, primaryPositionFlag, pageRequest);
	}
}
