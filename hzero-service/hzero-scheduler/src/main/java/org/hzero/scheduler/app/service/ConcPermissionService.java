package org.hzero.scheduler.app.service;

import org.hzero.scheduler.domain.entity.ConcPermission;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求权限应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-31 09:47:02
 */
public interface ConcPermissionService {

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
     * 创建权限
     *
     * @param permission 权限
     * @return 新建的权限
     */
    ConcPermission createPermission(ConcPermission permission);

    /**
     * 权限更新
     *
     * @param permission 权限
     * @return 更新的权限
     */
    ConcPermission updatePermission(ConcPermission permission);
}
