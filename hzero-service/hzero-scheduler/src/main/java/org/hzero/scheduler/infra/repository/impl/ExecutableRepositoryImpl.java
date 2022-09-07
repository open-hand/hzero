package org.hzero.scheduler.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.domain.entity.Executable;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
import org.hzero.scheduler.infra.mapper.ExecutableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/09 15:52
 */
@Component
public class ExecutableRepositoryImpl extends BaseRepositoryImpl<Executable> implements ExecutableRepository {

    @Autowired
    private ExecutableMapper executableMapper;

    @Override
    public Page<Executable> pageExecutable(Long tenantId, Long executorId, String executableName, String executableDesc, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> executableMapper.listExecutables(tenantId, executorId, executableName, executableDesc));
    }

    @Override
    public Executable selectExecutableById(Long executableId, Long tenantId) {
        return executableMapper.selectExecutableById(executableId, tenantId);
    }
}
