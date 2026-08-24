package org.hzero.platform.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.api.dto.RegionDTO;
import org.hzero.platform.domain.entity.Region;

import java.util.List;
import java.util.Set;


/**
 * <p>
 * 地区定义接口
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 9:17
 */
public interface RegionService {
    /**
     * 查询国家下地区定义，使用树状结构返回
     *
     * @param countryId   国家ID
     * @param enabledFlag 筛选条件
     * @return 树状结构地区定义
     */
    List<RegionDTO> treeRegion(long countryId, Integer enabledFlag);

    /**
     * 查询地区树，返回父级ID
     *
     * @param countryId   国家ID
     * @param condition   查询条件
     * @param enabledFlag 筛选条件
     * @return 当前节点以及父级ID
     */
    List<RegionDTO> treeRegionWithParent(long countryId, String condition, Integer enabledFlag);

    /**
     * 查询国家下地区定义
     *
     * @param countryId 国家ID
     * @return 地区定义
     */
    List<Region> listRegion(long countryId);

    /**
     * 查询国家下的地区定义以及可能的父亲
     *
     * @param countryId 国家ID
     * @param regionId  地区ID
     * @return 地区定义
     */
    List<Region> listRegionWithPossibleParent(long countryId, long regionId);

    /**
     * 查询地区
     *
     * @param countryId 国家ID
     * @param regionId  地区ID
     * @return 地区定义列表
     */
    List<Region> listRegion(Long countryId, Long regionId);

    /**
     * 查询指定的地区
     *
     * @param regionId 地区ID
     * @return 地区
     */
    Region queryRegion(long regionId);

    /**
     * 创建地区定义
     *
     * @param region 地区
     * @return 创建之后的地区定义
     */
    Region createRegion(Region region);

    /**
     * 更新地区定义
     *
     * @param region 地区
     * @return 更新之后的地区定义
     */
    Region updateRegion(Region region);

    /**
     * 禁用地区定义
     *
     * @param region 地区
     * @return 更新后的地区定义
     */
    List<Region> disableOrEnable(Region region);

    /**
     * 批量删除地区定义
     *
     * @param regionList 地区列表
     */
    void batchDeleteRegion(List<Region> regionList);

    /**
     * 批量查询地区
     *
     * @param regionIds 地区ID列表
     * @return List<RegionDTO>
     */
    List<RegionDTO> queryRegionByPrimaryKeys(Set<Long> regionIds);

    /**
     * 懒加载查询地区树
     *
     * @param tenantId    租户ID
     * @param countryId   国家ID
     * @param regionId    地区ID
     * @param enabledFlag 筛选条件
     * @return 子地区列表
     */
    List<RegionDTO> lazyTreeRegion(Long tenantId, long countryId, Long regionId, Integer enabledFlag);

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

    /**
     * 批量新建地区定义
     *
     * @param regions 待新建的地区
     * @return 新建后的地区
     */
    List<Region> batchCreateRegion(List<Region> regions);
}
