package org.hzero.platform.domain.service.datasource;

import org.hzero.platform.domain.entity.Datasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据源功能扩展通用接口实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2020/06/01 20:26
 */
@Component
public class DsExtendEntranceImpl implements DsExtendEntrance {

    @Autowired
    private Driver driver;

    @Override
    public void testConnectionByDatasource(Datasource datasource) {
        Connection connection = driver.getConnection(datasource.getDbType());
        connection.testConnection(datasource);
    }
}
