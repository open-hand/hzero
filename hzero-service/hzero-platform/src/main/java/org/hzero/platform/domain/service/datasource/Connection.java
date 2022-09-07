package org.hzero.platform.domain.service.datasource;

import org.hzero.platform.domain.entity.Datasource;

/**
 * 数据库连接抽象
 *
 * @author xiaoyu.zhao@hand-china.com
 */
public interface Connection {

    /**
     * 通用测试连接，可实现该接口对测试连接功能做定制化处理
     *
     * @param datasource 数据源参数
     */
    void testConnection(Datasource datasource);

}
