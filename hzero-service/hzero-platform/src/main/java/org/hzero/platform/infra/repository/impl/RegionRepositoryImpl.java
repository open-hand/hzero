package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.RegionDTO;
import org.hzero.platform.domain.entity.Region;
import org.hzero.platform.domain.repository.RegionRepository;
import org.hzero.platform.infra.mapper.RegionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 地区定义仓库接口实现类
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 10:30
 */
@Component
public class RegionRepositoryImpl extends BaseRepositoryImpl<Region> implements RegionRepository {
    private RegionMapper regionMapper;

    @Autowired
    public RegionRepositoryImpl(RegionMapper regionMapper) {
        this.regionMapper = regionMapper;
    }

    @Override
    public int batchDeleteRegion(List<Region> regionList) {
        int cnt = 0;
        for (Region region : regionList) {
            cnt += regionMapper.deleteByPrimaryKey(region);
        }
        return cnt;
    }

    @Override
    public List<RegionDTO> listRegionWithParent(long countryId, String condition, Integer enabledFlag) {
        Set<RegionDTO> regionSet = regionMapper.selectRegion(countryId, condition, enabledFlag);
        if (!CollectionUtils.isEmpty(regionSet) && StringUtils.hasText(condition)) {
            Set<Long> parentIds = regionSet.stream().filter(item -> item.getParentRegionId() != null).map(RegionDTO::getParentRegionId).collect(Collectors.toSet());
            boolean hasFather = !parentIds.isEmpty();
            while (hasFather) {
                Set<RegionDTO> fatherList = regionMapper.selectRegionParent(parentIds, enabledFlag);
                regionSet.addAll(fatherList);
                parentIds = fatherList.stream().filter(item -> item.getParentRegionId() != null).map(RegionDTO::getParentRegionId).collect(Collectors.toSet());
                hasFather = !parentIds.isEmpty();
            }
        }
        return new ArrayList<>(regionSet).stream().sorted(Comparator.comparingLong(RegionDTO::getRegionId)).collect(Collectors.toList());
    }

    @Override
    public List<RegionDTO> listRegion(long countryId, Integer enabledFlag) {
        return regionMapper.listRegion(countryId, enabledFlag);
    }

    @Override
    public List<Region> listRegionWithPossibleParent(long countryId, long regionId) {
        return regionMapper.listRegionWithPossibleParent(countryId, regionId);
    }

    @Override
    public List<Region> listRegionByLevelPath(List<String> levelPathList) {
        return regionMapper.listRegionByLevelPath(levelPathList);
    }

    @Override
    public List<Region> listRegionChildrenByLevelPath(String levelPath) {
        return regionMapper.listRegionChildrenByLevelPath(levelPath);
    }

    @Override
    public List<RegionDTO> queryRegionByPrimaryKeys(Set<Long> regionIds) {
        return regionMapper.queryRegionByPrimaryKeys(regionIds);
    }

    @Override
    public List<RegionDTO> selectSubNodeRegion(Long tenantId, long countryId, Long regionId, Integer enabledFlag) {
        return regionMapper.selectSubNodeRegion(tenantId, countryId, regionId, enabledFlag);
    }

    @Override
    public Page<RegionDTO> pageTreeRegion(Long tenantId, long countryId, String condition, Integer enabledFlag, PageRequest pageRequest) {
        Page<RegionDTO> pageRegions = PageHelper.doPage(pageRequest, () -> regionMapper.selectPageRegion(tenantId, countryId, condition, enabledFlag));
        //获取地区的直接父级ID
        Set<Long> parentIds = pageRegions.stream().filter(regionDTO -> regionDTO.getParentRegionId() != null)
                .map(RegionDTO::getParentRegionId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(parentIds)) {
            List<String> parentNames = new ArrayList<>();
            pageRegions.forEach(regionDTO -> {
                //本级名称
                parentNames.add(regionDTO.getRegionName());
                regionDTO.setNameLevelPaths(parentNames);
            });
            return pageRegions;
        }
        Map<Long, RegionDTO> mapRegionIds = new HashMap<>();
        while (!CollectionUtils.isEmpty(parentIds)) {
            //查询所有作为父级地区的名称和父级信息
            List<RegionDTO> regionDTOS = regionMapper.selectNameByParentIds(parentIds, tenantId);
            //提取地区ID
            regionDTOS.forEach(regionDTO -> {
                mapRegionIds.put(regionDTO.getRegionId(),regionDTO);
            });
            //获取上一级父级ID
            parentIds = regionDTOS.stream().filter(regionDTO -> regionDTO.getParentRegionId() != null)
                    .map(RegionDTO::getParentRegionId).collect(Collectors.toSet());
        }
        pageRegions.forEach(regionDTO -> {
            List<String> parentNames = new ArrayList<>();
            Long parentRegionId = regionDTO.getParentRegionId();
            while (parentRegionId != null) {
                //ID 唯一 取值唯一
                RegionDTO parentRegionDTO = mapRegionIds.get(parentRegionId);
                //存储直接父级名称
                parentNames.add(parentRegionDTO.getRegionName());
                //下一级
                parentRegionId = parentRegionDTO.getParentRegionId();
            }
            //倒序
            Collections.reverse(parentNames);
            //本级名称
            parentNames.add(regionDTO.getRegionName());
            regionDTO.setNameLevelPaths(parentNames);
        });
        return pageRegions;
    }
}
