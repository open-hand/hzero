package org.hzero.scheduler.app.service.impl;

import java.util.Objects;

import org.hzero.core.base.BaseConstants;
import org.hzero.scheduler.app.service.ExecutableService;
import org.hzero.scheduler.app.service.JobInfoService;
import org.hzero.scheduler.domain.entity.Concurrent;
import org.hzero.scheduler.domain.entity.Executable;
import org.hzero.scheduler.domain.repository.ConcurrentRepository;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/10 10:52
 */
@Service
public class ExecutableServiceImpl implements ExecutableService {

    @Autowired
    private ExecutableRepository executableRepository;
    @Autowired
    private ConcurrentRepository concurrentRepository;
    @Autowired
    private JobInfoService jobInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Executable createExecutable(Executable executable) {
        executable.validate(executableRepository);
        executableRepository.insertSelective(executable);
        return executable;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Executable updateExecutable(Executable executable) {
        // 检查对象是否被关联,被关联的不可禁用
        if (!Objects.equals(executable.getEnabledFlag(), BaseConstants.Flag.YES)) {
            Assert.isTrue(concurrentRepository.selectCount(new Concurrent().setExecutableId(executable.getExecutableId())) == BaseConstants.Digital.ZERO, HsdrErrorCode.UPDATE_FLAG_ERROR);
        }
        // 移除任务-执行器策略的参数
        executable.setStrategyParam(jobInfoService.clearStrategyParam(executable.getExecutorStrategy(), executable.getStrategyParam()));
        executableRepository.updateOptional(executable,
                Executable.FIELD_EXECUTABLE_NAME,
                Executable.FIELD_EXECUTABLE_DESC,
                Executable.FIELD_EXECUTOR_ID,
                Executable.FIELD_EXECUTOR_STRATEGY,
                Executable.FIELD_FAIL_STRATEGY,
                Executable.FIELD_STRATEGY_PARAM,
                Executable.FIELD_EXE_TYPE_CODE,
                Executable.FIELD_JOB_HANDLER,
                Executable.FIELD_ENABLED_FLAG);
        return executable;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExecutable(Long executableId) {
        // 检查对象是否被关联
        Assert.isTrue(concurrentRepository.selectCount(new Concurrent().setExecutableId(executableId)) == BaseConstants.Digital.ZERO, HsdrErrorCode.DELETE_ERROR);
        executableRepository.deleteByPrimaryKey(executableId);
    }
}
