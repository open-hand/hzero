package org.hzero.platform.domain.service.datasource;

import org.hzero.platform.domain.entity.Datasource;
import org.hzero.platform.domain.entity.DatasourceDriver;

/**
 * 数据源功能扩展通用接口
 *
 * @author xiaoyu.zhao@hand-china.com 2020/06/01 20:15
 */
public interface DsExtendEntrance {

    /**
     * 数据源创建事件发布类
     */
     default void createEventPublish(Datasource datasource, DatasourceDriver driver){}
    /**
     * 数据源更新事件发布类
     */
    default void updateEventPublish(Datasource beforeDatasource, Datasource afterDatasource, DatasourceDriver driver){}
    /**
     * 数据源删除事件发布类
     */
    default void removeEventPublish(Datasource datasource, DatasourceDriver driver){}

    /**
     * 测试连接
     */
    void testConnectionByDatasource(Datasource datasource);
}
