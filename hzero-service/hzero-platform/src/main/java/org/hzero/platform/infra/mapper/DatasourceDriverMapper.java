package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.DatasourceDriver;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 数据源驱动配置Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-08-21 19:55:37
 */
public interface DatasourceDriverMapper extends BaseMapper<DatasourceDriver> {

    /**
     * 分页查询数据源驱动信息
     *
     * @param datasourceDriver 查询条件
     * @return 分页查询结果
     */
    List<DatasourceDriver> selectDatasourceDrivers(DatasourceDriver datasourceDriver);

    /**
     * 通过数据库类型以及租户Id获取自定义驱动信息
     *
     * @param tenantId 租户Id
     * @param databaseType 数据库类型
     * @return DatasourceDriver
     */
    DatasourceDriver selectDriverByDatabaseType(@Param("tenantId") Long tenantId,
            @Param("databaseType") String databaseType);

    /**
     * 获取数据源驱动明细
     *
     * @param driverId 驱动Id
     * @return 明细信息
     */
    DatasourceDriver selectDriverDetails(@Param("driverId") Long driverId);
}
