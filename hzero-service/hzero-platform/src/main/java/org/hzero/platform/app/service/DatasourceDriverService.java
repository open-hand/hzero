package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.DatasourceDriver;

/**
 * 数据源驱动配置应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-08-21 19:55:37
 */
public interface DatasourceDriverService {

    /**
     * 创建数据源驱动
     *
     * @param datasourceDriver 数据源驱动
     * @return 新增结果
     */
    DatasourceDriver createDatasourceDriver(DatasourceDriver datasourceDriver);

    /**
     * 更新数据源驱动
     *
     * @param datasourceDriver 数据源驱动
     * @return 更新结果
     */
    DatasourceDriver updateDatasourceDriver(DatasourceDriver datasourceDriver);

    /**
     * 删除数据源驱动（若该驱动被引用则不允许删除，给出提示信息）
     *
     * @param datasourceDriver 删除逇数据源驱动
     */
    void deleteDriver(DatasourceDriver datasourceDriver);
}
