package org.hzero.scheduler.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.domain.entity.Concurrent;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/09 16:24
 */
public interface ConcurrentRepository extends BaseRepository<Concurrent> {

    /**
     * 并发程序集合
     *
     * @param tenantId        租户ID
     * @param concCode        并发程序代码
     * @param concName        并发程序名称
     * @param concDescription 并发程序描述
     * @param enabledFlag     启用标识
     * @param pageRequest     分页
     * @return 并发程序集合
     */
    Page<Concurrent> pageConcurrent(Long tenantId, String concCode, String concName, String concDescription, Integer enabledFlag, PageRequest pageRequest);

    /**
     * 并发程序明细
     *
     * @param concurrentId 主键
     * @param tenantId     租户Id
     * @return 并发程序对象
     */
    Concurrent detailConcurrent(Long concurrentId, Long tenantId);

    /**
     * 可选并发程序集合(已分配的)
     *
     * @param tenantId 租户Id
     * @param roleIds  角色Id
     * @param concCode 并发程序编码
     * @param concName 并发程序名称
     * @param nowDate  当前日期
     * @param flag     是否平台调用
     * @return 可选并发程序集合
     */
    List<Concurrent> listConcurrentByTenantId(Long tenantId, List<Long> roleIds, String concCode, String concName, LocalDate nowDate, boolean flag);

    /**
     * 可选并发程序集合(所有租户自己的和平台分配给租户的)
     *
     * @param tenantId 租户Id
     * @param concCode 并发程序编码
     * @param concName 并发程序名称
     * @param nowDate  当前日期
     * @return 可选并发程序集合
     */
    List<Concurrent> adminListConcurrentByTenantId(Long tenantId, String concCode, String concName, LocalDate nowDate);
}
