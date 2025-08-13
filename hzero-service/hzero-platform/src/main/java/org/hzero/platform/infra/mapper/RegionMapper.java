package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.RegionDTO;
import org.hzero.platform.domain.entity.Region;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 地区定义数据库操作映射
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 10:30
 */
public interface RegionMapper extends BaseMapper<Region> {

    /**
     * 查询地区
     *
     * @param countryId   国家ID
     * @param enabledFlag 筛选条件
     * @return 地区列表
     */
    List<RegionDTO> listRegion(@Param("countryId") long countryId, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询国家下的地区定义以及可能的父亲
     *
     * @param countryId 国家ID
     * @param regionId  地区ID
     * @return 地区定义
     */
    List<Region> listRegionWithPossibleParent(@Param("countryId") long countryId,
                                              @Param("regionId") long regionId);

    /**
     * 查询地区树，返回父级ID
     *
     * @param countryId   国家ID
     * @param condition   查询条件
     * @param enabledFlag 筛选条件
     * @return 当前节点以及父级
     */
    List<RegionDTO> listRegionWithParent(@Param("countryId") long countryId, @Param("condition") String condition, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询地区树，返回父级ID
     *
     * @param countryId   国家ID
     * @param condition   查询条件
     * @param enabledFlag 筛选条件
     * @return 当前节点以及父级ID
     */
    Set<RegionDTO> selectRegion(@Param("countryId") long countryId, @Param("condition") String condition, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 批量查询地区
     *
     * @param parentRegionIdList 父级level_path
     * @param enabledFlag        筛选条件
     * @return List<RegionDTO>
     */
    Set<RegionDTO> selectRegionParent(@Param("parentRegionIdList") Set<Long> parentRegionIdList, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 根据 levelPath 查询地区列表
     *
     * @param levelPathList 等级路径列表
     * @return 地区列表
     */
    List<Region> listRegionByLevelPath(@Param("levelPathList") List<String> levelPathList);

    /**
     * 查询地区以及子地区
     *
     * @param levelPath 等级路径
     * @return 地区以及子地区
     */
    List<Region> listRegionChildrenByLevelPath(@Param("levelPath") String levelPath);

    /**
     * 批量查询地区
     *
     * @param regionIds 地区ID
     * @return List<RegionDTO>
     */
    List<RegionDTO> queryRegionByPrimaryKeys(@Param("regionIds") Set<Long> regionIds);

    /**
     * 查询目标地区下一级地区列表  不传地区ID查询最上级地区列表
     *
     * @param tenantId    租户ID
     * @param countryId   国家ID
     * @param regionId    地区ID
     * @param enabledFlag 筛选条件
     * @return 地区列表List<RegionDTO>
     */
    List<RegionDTO> selectSubNodeRegion(@Param("tenantId") long tenantId, @Param("countryId") long countryId, @Param("regionId") Long regionId, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 分页查询地区列表信息
     *
     * @param tenantId    租户ID
     * @param countryId   国家ID
     * @param condition   查询条件
     * @param enabledFlag 筛选条件
     * @return 地区列表
     */
    List<RegionDTO> selectPageRegion(@Param("tenantId") long tenantId, @Param("countryId") long countryId, @Param("condition") String condition, @Param("enabledFlag") Integer enabledFlag);

    /**
     * 查询父级地区名称
     *
     * @param parentIds 父级ID
     * @param tenantId  租户ID
     * @return
     */
    List<RegionDTO> selectNameByParentIds(@Param("parentIds") Set<Long> parentIds, @Param("tenantId") Long tenantId);
}
