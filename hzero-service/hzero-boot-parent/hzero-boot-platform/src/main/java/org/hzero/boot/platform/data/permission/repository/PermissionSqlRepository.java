package org.hzero.boot.platform.data.permission.repository;

import net.sf.jsqlparser.schema.Table;
import org.hzero.boot.platform.data.permission.vo.PermissionRangeVO;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 数据屏蔽资源层，用于获取将要被注入的sql
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 11:03
 */
public interface PermissionSqlRepository {

    /**
     * 根据条件获取已经维护好的数据屏蔽拦截sql
     *
     * @param serviceName 服务名
     * @param table       表名
     * @param sqlId       sqlId
     * @param tenantId    租户id
     * @return sqlList
     */
    PermissionRangeVO getPermissionRange(String serviceName, Table table, String sqlId, Long tenantId);

    /**
     * 根据表名和sqlId获取已经配置好的数据屏蔽拦截sql
     *
     * @param tableCollection 表集合
     * @param serviceName     服务名
     * @param sqlId           sqlId
     * @param tenantId        租户id
     * @return map
     */
    Map<Table, PermissionRangeVO> getPermissionRangeVOMap(Collection<Table> tableCollection, String serviceName, String sqlId, Long tenantId);

    default void resetCache() {

    }

}
