package org.hzero.scheduler.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.scheduler.domain.entity.Executor;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-09 14:45:13
 */
public interface ExecutorMapper extends BaseMapper<Executor> {

    /**
     * 查询
     *
     * @param executorCode 执行器编码
     * @param executorName 执行器名称
     * @param executorType 执行器类型
     * @param status       状态
     * @param tenantId     租户Id
     * @return 列表
     */
    List<Executor> listExecutors(@Param("executorCode") String executorCode,
                                 @Param("executorName") String executorName,
                                 @Param("executorType") Integer executorType,
                                 @Param("status") String status,
                                 @Param("tenantId") Long tenantId);
}
