package org.hzero.scheduler.infra.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.scheduler.domain.entity.ConcPermission;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 并发请求权限Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-31 09:47:02
 */
public interface ConcPermissionMapper extends BaseMapper<ConcPermission> {

    /**
     * 根据并发程序id查询
     *
     * @param concurrentId 并发程序Id
     * @param tenantId     租户Id
     * @param ignore       是否忽略roleId为-1的记录
     * @return 权限
     */
    List<ConcPermission> selectByConcurrentId(@Param("concurrentId") Long concurrentId,
                                              @Param("tenantId") Long tenantId,
                                              @Param("ignore") boolean ignore);

    /**
     * 获取限制次数
     *
     * @param roleId       角色Id
     * @param tenantId     租户Id
     * @param concurrentId 并发程序Id
     * @param nowDate      日期
     * @return 列表
     */
    List<ConcPermission> selectQuantity(@Param("roleId") Long roleId,
                                        @Param("tenantId") Long tenantId,
                                        @Param("concurrentId") Long concurrentId,
                                        @Param("nowDate") LocalDate nowDate);
}
