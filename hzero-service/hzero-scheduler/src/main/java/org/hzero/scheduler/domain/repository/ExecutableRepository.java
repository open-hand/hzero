package org.hzero.scheduler.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.domain.entity.Executable;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/09 15:51
 */
public interface ExecutableRepository extends BaseRepository<Executable> {

    /**
     * 分页查询
     *
     * @param tenantId       租户Id
     * @param executorId     执行器Id
     * @param executableName 可执行名称
     * @param executableDesc 可执行描述
     * @param pageRequest    分页
     * @return 并发可执行集合
     */
    Page<Executable> pageExecutable(Long tenantId, Long executorId, String executableName, String executableDesc, PageRequest pageRequest);

    /**
     * 查询详情
     *
     * @param executableId 主键
     * @param tenantId     租户Id
     * @return 并发可执行
     */
    Executable selectExecutableById(Long executableId, Long tenantId);

}
