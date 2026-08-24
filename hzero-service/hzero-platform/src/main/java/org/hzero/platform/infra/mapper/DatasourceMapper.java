package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.Datasource;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 数据源配置Mapper
 *
 * @author like.zhang@hand-china.com  2018-09-13 14:10:13
 */
public interface DatasourceMapper extends BaseMapper<Datasource> {

    /**
     * 查询数据源
     *
     * @param datasource    数据源实体
     * @param orgQueryFlag 平台查询标识
     * @return List<Datasource>
     */
    List<Datasource> selectDatasources(@Param("datasource") Datasource datasource, @Param("orgQueryFlag") Boolean orgQueryFlag);

    /**
     * 查询数据源
     *
     * @param datasourceId 数据源ID
     * @return Datasource
     */
    Datasource selectDatasource(@Param("datasourceId") Long datasourceId);

    /**
     * 查询数据源
     *
     * @param tenantId       租户ID
     * @param datasourceCode 数据源编码
     * @return Datasource
     */
    Datasource getByUnique(@Param("tenantId") Long tenantId, @Param("datasourceCode") String datasourceCode);
}
