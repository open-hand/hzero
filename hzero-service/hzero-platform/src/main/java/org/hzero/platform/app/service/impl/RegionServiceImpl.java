package org.hzero.platform.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.api.dto.RegionDTO;
import org.hzero.platform.app.service.RegionService;
import org.hzero.platform.domain.entity.Region;
import org.hzero.platform.domain.repository.RegionRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.hzero.core.base.BaseConstants.ErrorCode.DATA_NOT_EXISTS;

/**
 * <p>
 * 地区定义接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 9:17
 */
@Service
public class RegionServiceImpl extends BaseServiceImpl<Region> implements RegionService {
    public static final String SPLIT = "|";
    private static final long ROOT_ID = -1L;
    private RegionRepository regionRepository;

    @Autowired
    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public List<RegionDTO> treeRegion(long countryId, Integer enabledFlag) {
        return recursiveBuildRegionTree(
                regionRepository.listRegion(countryId, enabledFlag).stream().collect(Collectors.groupingBy(
                        item -> item.getParentRegionId() == null ? ROOT_ID : item.getParentRegionId())),
                ROOT_ID, new ArrayList<>());
    }

    @Override
    public List<RegionDTO> treeRegionWithParent(long countryId, String condition, Integer enabledFlag) {
        return recursiveBuildRegionTree(
                regionRepository.listRegionWithParent(countryId, condition, enabledFlag).stream().collect(Collectors.groupingBy(
                        item -> item.getParentRegionId() == null ? ROOT_ID : item.getParentRegionId())),
                ROOT_ID, new ArrayList<>());
    }

    @Override
    public List<Region> listRegion(long countryId) {
        return regionRepository.select(new Region().setCountryId(countryId));
    }

    @Override
    public List<Region> listRegionWithPossibleParent(long countryId, long regionId) {
        return regionRepository.listRegionWithPossibleParent(countryId, regionId);
    }

    @Override
    public List<Region> listRegion(Long countryId, Long regionId) {
        return regionRepository.selectByCondition(Condition.builder(Region.class)
                .andWhere(Sqls.custom().andEqualTo(Region.FIELD_PARENT_REGION_ID, regionId)
                        .andEqualTo(Region.FIELD_COUNTRY_ID, countryId, true))
                .build());
    }

    @Override
    public Region queryRegion(long regionId) {
        return regionRepository.selectByPrimaryKey(regionId);
    }

    @Override
    public Region createRegion(Region region) {
        if (region.getParentRegionId() != null) {
            Region parentRegion = regionRepository.selectByPrimaryKey(region.getParentRegionId());
            if (parentRegion == null) {
                throw new CommonException(DATA_NOT_EXISTS);
            }
            region.setLevelPath(parentRegion.getLevelPath() + SPLIT + region.getRegionCode());
            region.setLevelNumber(parentRegion.getLevelNumber() + BaseConstants.Digital.ONE);
        } else {
            region.setLevelPath(region.getRegionCode());
            region.setLevelNumber(BaseConstants.Digital.ONE);
        }
        if (regionRepository.insert(region) != 1) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        }
        return region;
    }

    @Override
    public Region updateRegion(Region region) {
        Region exists = regionRepository.selectByPrimaryKey(region.getRegionId());
        if (exists == null) {
            throw new CommonException(DATA_NOT_EXISTS);
        }
        region.setRegionCode(exists.getRegionCode());
        regionRepository.updateOptional(region, Region.FIELD_QUICK_INDEX,
                Region.FIELD_REGION_NAME, Region.FIELD_ENABLE_FLAG);
        return region;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Region> disableOrEnable(Region region) {
        // 默认启用
        int enableFlag = region.getEnabledFlag() == null ? BaseConstants.Flag.NO : region.getEnabledFlag();
        Region exists = regionRepository.selectByPrimaryKey(region.getRegionId());
        if (exists == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        // 默认父级未启用禁止启用下级
        if (enableFlag == BaseConstants.Flag.YES && exists.getParentRegionId() != null) {
            // 验证父级是否开启
            Region parentRegion = regionRepository.selectByPrimaryKey(exists.getParentRegionId());
            if (Objects.equals(BaseConstants.Flag.NO, parentRegion.getEnabledFlag())) {
                throw new CommonException(HpfmMsgCodeConstants.ERROR_PARENT_DISABlE);
            }
        }
        List<Region> allRegionList = new LinkedList<>();
        allRegionList.add(exists);
        this.listRegionChildrenByParentId(exists.getRegionId(), allRegionList);
        for (Region item : allRegionList) {
            item.setEnabledFlag(enableFlag);
        }
        return regionRepository.batchUpdateOptional(allRegionList, Region.FIELD_ENABLE_FLAG);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteRegion(List<Region> regionList) {
        if (regionRepository.batchDeleteRegion(regionList) != regionList.size()) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR);
        }
    }

    @Override
    public List<RegionDTO> queryRegionByPrimaryKeys(Set<Long> regionIds) {
        return regionRepository.queryRegionByPrimaryKeys(regionIds);
    }

    @Override
    public List<RegionDTO> lazyTreeRegion(Long tenantId, long countryId, Long regionId, Integer enabledFlag) {
        //懒加载查询地区列表，当未传regionId(为null)时，查询最上级地区列表
        List<RegionDTO> regionList = regionRepository.selectSubNodeRegion(tenantId, countryId, regionId, enabledFlag);
        return regionList;
    }

    @Override
    public Page<RegionDTO> pageTreeRegion(Long tenantId, long countryId, String condition, Integer enabledFlag, PageRequest pageRequest) {
        return regionRepository.pageTreeRegion(tenantId, countryId, condition, enabledFlag, pageRequest);
    }

    @Override
    public List<Region> batchCreateRegion(List<Region> regions) {
        regions.forEach(this::createRegion);
        return regions;
    }

    /**
     * 递归构建地区定义树
     *
     * @param regionMap  地区定义Map
     * @param parentId   父地区定义ID
     * @param regionList 地区定义List
     * @return 地区定义树
     */
    private List<RegionDTO> recursiveBuildRegionTree(Map<Long, List<RegionDTO>> regionMap, long parentId,
                                                     List<RegionDTO> regionList) {
        if (regionMap.containsKey(parentId)) {
            for (RegionDTO region : regionMap.get(parentId)) {
                if (regionMap.containsKey(region.getRegionId())) {
                    regionList.add(region.setChildren(
                            recursiveBuildRegionTree(regionMap, region.getRegionId(), new ArrayList<>())));
                } else {
                    regionList.add(region);
                }
            }
        }
        return regionList;
    }

    /**
     * 获取所有父级路径
     *
     * @param levelPath 当前等级路径
     * @return 所有父级路径
     */
    private List<String> resolveParentLevelPath(String levelPath) {
        List<String> levelPathList = new ArrayList<>();
        while (levelPath.contains(SPLIT)) {
            levelPath = levelPath.substring(0, levelPath.lastIndexOf(SPLIT));
            levelPathList.add(levelPath);
        }
        return levelPathList;
    }

    /**
     * 通过regionId遍历获取所有下级地区信息
     *
     * @param regionId 地区Id
     * @param allRegionList 全部下级地区信息集合（包含根层级）
     */
    private void listRegionChildrenByParentId(Long regionId, List<Region> allRegionList) {
        Region selectRegion = new Region().setParentRegionId(regionId);
        List<Region> subRegions = regionRepository.select(selectRegion);
        if (CollectionUtils.isNotEmpty(subRegions)) {
            allRegionList.addAll(subRegions);
            for (Region subRegion : subRegions) {
                listRegionChildrenByParentId(subRegion.getRegionId(), allRegionList);
            }
        }
    }
}
