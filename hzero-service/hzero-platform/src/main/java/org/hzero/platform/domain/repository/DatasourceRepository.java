package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Datasource;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import java.util.List;

/**
 * 数据源配置资源库
 *
 * @author like.zhang@hand-china.com  2018-09-13 14:10:13
 */
public interface DatasourceRepository extends BaseRepository<Datasource> {

    /**
     * 分页查询数据源
     *
     * @param pageRequest   分页参数
     * @param datasource    数据源
     * @param orgQueryFlag 平台查询标识
     * @return 分页数据
     */
    Page<Datasource> pageDatasource(PageRequest pageRequest, Datasource datasource, Boolean orgQueryFlag);

    /**
     * 查询数据源
     *
     * @param datasourceId 数据源ID
     * @return 数据源
     */
    Datasource selectDatasource(Long datasourceId);

    /**
     * 查询数据源
     *
     * @param tenantId       租户ID
     * @param datasourceCode 数据源编码
     * @return 数据源
     */
    Datasource getByUnique(Long tenantId, String datasourceCode);

    /**
     * 条件查询所有匹配的数据源
     *
     * @param datasource 查询条件，若为null则查询所有
     * @return 查询结果
     */
    List<Datasource> listDatasourceByCondition(Datasource datasource);
}
