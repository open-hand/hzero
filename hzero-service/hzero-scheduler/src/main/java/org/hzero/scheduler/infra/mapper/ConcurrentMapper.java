package org.hzero.scheduler.infra.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.scheduler.domain.entity.Concurrent;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 并发程序Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-11 10:20:47
 */
public interface ConcurrentMapper extends BaseMapper<Concurrent> {

    /**
     * 获取并发程序列表
     *
     * @param tenantId        租户Id
     * @param concCode        并发程序代码
     * @param concName        并发程序名称
     * @param concDescription 并发程序描述
     * @param enabledFlag     启用标识
     * @return concurrent集合
     */
    List<Concurrent> listConcurrent(@Param("tenantId") Long tenantId,
                                    @Param("concCode") String concCode,
                                    @Param("concName") String concName,
                                    @Param("concDescription") String concDescription,
                                    @Param("enabledFlag") Integer enabledFlag);

    /**
     * 详情
     *
     * @param concurrentId 主键
     * @param tenantId     租户Id
     * @return 并发对象
     */
    Concurrent queryConcurrentById(@Param("concurrentId") Long concurrentId,
                                   @Param("tenantId") Long tenantId);


    /**
     * 可选并发程序集合
     *
     * @param tenantId 租户Id
     * @param roleIds  角色Id
     * @param concCode 并发程序编码
     * @param concName 并发程序名称
     * @param nowDate  当前日期
     * @param flag     是否平台调用
     * @return 可选并发程序集合
     */
    List<Concurrent> listConcurrentByTenantId(@Param("tenantId") Long tenantId,
                                              @Param("roleIds") List<Long> roleIds,
                                              @Param("concCode") String concCode,
                                              @Param("concName") String concName,
                                              @Param("nowDate") LocalDate nowDate,
                                              @Param("flag") boolean flag);

    /**
     * 可选并发程序集合
     *
     * @param tenantId 租户Id
     * @param concCode 并发程序编码
     * @param concName 并发程序名称
     * @param nowDate  当前日期
     * @return 可选并发程序集合
     */
    List<Concurrent> adminListConcurrentByTenantId(@Param("tenantId") Long tenantId,
                                                   @Param("concCode") String concCode,
                                                   @Param("concName") String concName,
                                                   @Param("nowDate") LocalDate nowDate);
}
