package org.hzero.scheduler.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.scheduler.domain.entity.Executable;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 并发可执行Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-10 10:30:58
 */
public interface ExecutableMapper extends BaseMapper<Executable> {

    /**
     * 查询
     *
     * @param tenantId       租户Id
     * @param executorId     执行器Id
     * @param executableName 可执行名称
     * @param executableDesc 可执行描述o
     * @return 并发可执行集合
     */
    List<Executable> listExecutables(@Param("tenantId") Long tenantId,
                                     @Param("executorId") Long executorId,
                                     @Param("executableName") String executableName,
                                     @Param("executableDesc") String executableDesc);

    /**
     * 查询详情
     *
     * @param executableId 主键
     * @param tenantId     租户Id
     * @return 并发可执行
     */
    Executable selectExecutableById(@Param("executableId") Long executableId,
                                    @Param("tenantId") Long tenantId);
}
