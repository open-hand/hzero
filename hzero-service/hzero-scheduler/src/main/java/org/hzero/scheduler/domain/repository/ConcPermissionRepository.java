package org.hzero.scheduler.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.domain.entity.ConcPermission;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求权限资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-31 09:47:02
 */
public interface ConcPermissionRepository extends BaseRepository<ConcPermission> {

    /**
     * 根据并发程序id查询
     *
     * @param concurrentId 并发程序Id
     * @param tenantId     租户Id
     * @param ignore       是否忽略未指定角色的记录
     * @param pageRequest  分页
     * @return 权限
     */
    Page<ConcPermission> selectByConcurrentId(Long concurrentId, Long tenantId, boolean ignore, PageRequest pageRequest);

    /**
     * 获取限制次数
     *
     * @param roleId       角色Id
     * @param tenantId     租户Id
     * @param concurrentId 并发程序Id
     * @return 列表
     */
    List<ConcPermission> selectQuantity(Long roleId, Long tenantId, Long concurrentId);
}
