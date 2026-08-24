package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DatasourceDriver;

import java.util.List;

/**
 * 数据源驱动配置资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-08-21 19:55:37
 */
public interface DatasourceDriverRepository extends BaseRepository<DatasourceDriver> {

    /**
     * 分页查询数据源驱动信息
     *
     * @param pageRequest pageRequest
     * @param datasourceDriver 查询条件
     * @return 分页查询结果
     */
    Page<DatasourceDriver> listDatasourceDrivers(PageRequest pageRequest, DatasourceDriver datasourceDriver, Boolean orgQueryFlag);

    /**
     * 通过数据库类型以及租户Id获取自定义驱动信息
     *
     * @param tenantId 租户Id
     * @param databaseType 数据库类型
     * @return DatasourceDriver
     */
    DatasourceDriver getDriverByDatabaseType(Long tenantId, String databaseType);

    /**
     * 校验唯一性
     *
     * @param datasourceDriver 需要校验的参数
     */
    void validateUnique(DatasourceDriver datasourceDriver);

    /**
     * 获取驱动明细
     *
     * @param driverId 驱动Id
     * @return 驱动明细信息
     */
    DatasourceDriver getDriverDetails(Long driverId);

    /**
     * 条件查询数据源驱动列表信息
     *
     * @param datasourceDriver 查询条件，为null时查询所有启用的驱动信息
     * @return 查询结果
     */
    List<DatasourceDriver> listDriverByCondition(DatasourceDriver datasourceDriver);
}
