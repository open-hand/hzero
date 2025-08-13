package org.hzero.scheduler.app.service;

import org.hzero.scheduler.domain.entity.Concurrent;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发程序Service
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/11 10:42
 */
public interface ConcurrentService {

    /**
     * 新建并发程序
     *
     * @param concurrent 并发对象
     * @return 新建的对象
     */
    Concurrent createConcurrent(Concurrent concurrent);

    /**
     * 更新
     *
     * @param concurrent 并发对象
     * @return 被更新的对象
     */
    Concurrent updateConcurrent(Concurrent concurrent);

    /**
     * 查询
     *
     * @param tenantId    租户Id
     * @param concCode    并发程序编码
     * @param concName    并发程序名称
     * @param pageRequest 分页
     * @param flag        是否为平台调用
     * @return 并发程序集合
     */
    Page<Concurrent> pageConcurrentByTenantId(Long tenantId, String concCode, String concName, PageRequest pageRequest, boolean flag);
}
