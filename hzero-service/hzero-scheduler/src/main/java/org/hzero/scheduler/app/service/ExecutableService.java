package org.hzero.scheduler.app.service;

import org.hzero.scheduler.domain.entity.Executable;

/**
 * 并发可执行service
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/10 10:50
 */
public interface ExecutableService {


    /**
     * 新建
     *
     * @param executable 并发可执行对象
     * @return 并发可执行对象
     */
    Executable createExecutable(Executable executable);

    /**
     * 更新
     *
     * @param executable 并发可执行对象
     * @return 并发可执行对象
     */
    Executable updateExecutable(Executable executable);

    /**
     * 删除
     *
     * @param executableId 主键
     */
    void deleteExecutable(Long executableId);
}
