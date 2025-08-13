package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.RegionDTO;
import org.hzero.platform.domain.entity.Region;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 地区定义仓库接口
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 10:29
 */
public interface RegionRepository extends BaseRepository<Region> {

    /**
     * 批量删除地区定义
     *
     * @param regionList 地区列表，包含id和objectVersionNumber
     * @return 删除记录条数
     */
    int batchDeleteRegion(List<Region> regionList);

    /**
     * 查询地区树，返回父级ID
     *
     * @param countryId   国家ID
     * @param condition   查询条件
     * @param enabledFlag 筛选条件
     * @return 当前节点以及父级ID
     */
    List<RegionDTO> listRegionWithParent(long countryId, String condition, Integer enabledFlag);

    /**
     * 查询地区
     *
     * @param countryId   国家ID
     * @param enabledFlag 筛选条件
     * @return 地区列表
     */
    List<RegionDTO> listRegion(long countryId, Integer enabledFlag);

    /**
     * 查询国家下的地区定义以及可能的父亲
     *
     * @param countryId 国家ID
     * @param regionId  地区ID
     * @return 地区定义
     */
    List<Region> listRegionWithPossibleParent(long countryId, long regionId);

    /**
     * 根据 levelPath 查询地区列表
     *
     * @param levelPathList 等级路径列表
     * @return 地区列表
     */
    List<Region> listRegionByLevelPath(List<String> levelPathList);

    /**
     * 查询地区以及子地区
     *
     * @param levelPath 等级路径
     * @return 地区以及子地区
     */
    List<Region> listRegionChildrenByLevelPath(String levelPath);

    /**
     * 批量查询地区
     *
     * @param regionIds 地区ID列表
     * @return List<RegionDTO>
     */
    List<RegionDTO> queryRegionByPrimaryKeys(Set<Long> regionIds);

    /**
     * 查询目标地区下一级地区列表
     *
     * @param tenantId    租户ID
     * @param countryId   国家ID
     * @param regionId    地区ID
     * @param enabledFlag 筛选条件
     * @return 地区列表List<RegionDTO>
     */
    List<RegionDTO> selectSubNodeRegion(Long tenantId, long countryId, Long regionId, Integer enabledFlag);

    /**
     * 分页打平查询地区列表信息
     *
     * @param tenantId    租户ID
     * @param countryId   国家ID
     * @param condition   查询条件
     * @param enabledFlag 筛选条件
     * @param pageRequest 分页条件
     * @return 地区列表
     */
    Page<RegionDTO> pageTreeRegion(Long tenantId, long countryId, String condition, Integer enabledFlag, PageRequest pageRequest);
}
