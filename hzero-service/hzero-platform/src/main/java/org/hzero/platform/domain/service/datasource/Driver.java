package org.hzero.platform.domain.service.datasource;

/**
 * 驱动抽象
 *
 * @author xiaoyu.zhao@hand-china.com
 */
public interface Driver {

    /**
     * 注册数据源连接
     *
     * @param type       数据源类型
     * @param connection 数据源连接
     */
    void register(String type, Connection connection);

    /**
     * 获取连接
     *
     * @param type 数据源类型
     * @return 连接
     */
    Connection getConnection(String type);

}
