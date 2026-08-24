package org.hzero.scheduler.app.service.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.scheduler.app.service.ExecutorService;
import org.hzero.scheduler.domain.entity.Executable;
import org.hzero.scheduler.domain.entity.Executor;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.entity.JobLog;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
import org.hzero.scheduler.domain.repository.ExecutorRepository;
import org.hzero.scheduler.domain.repository.JobInfoRepository;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.domain.service.IAddressService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.scheduler.infra.util.AddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-09 14:45:13
 */
@Service
public class ExecutorServiceImpl implements ExecutorService {

    private final ExecutableRepository executableRepository;
    private final ExecutorRepository executorRepository;
    private final JobInfoRepository jobInfoRepository;
    private final JobLogRepository jobLogRepository;
    private final IAddressService addressService;

    @Autowired
    public ExecutorServiceImpl(ExecutableRepository executableRepository,
                               ExecutorRepository executorRepository,
                               JobInfoRepository jobInfoRepository,
                               JobLogRepository jobLogRepository,
                               IAddressService addressService) {
        this.executableRepository = executableRepository;
        this.executorRepository = executorRepository;
        this.jobInfoRepository = jobInfoRepository;
        this.jobLogRepository = jobLogRepository;
        this.addressService = addressService;
    }

    @Override
    public Page<Executor> pageExecutor(PageRequest pageRequest, String executorCode, String executorName, Integer executorType, String status, Long tenantId) {
        Page<Executor> page = executorRepository.pageExecutor(executorCode, executorName, executorType, status, tenantId, pageRequest);
        page.getContent().forEach(item -> {
            if (Objects.equals(item.getExecutorType(), BaseConstants.Flag.NO)) {
                String address = AddressUtils.getAddressString(addressService.getAddressList(item.getServerName()));
                // 执行器状态为下线，不可自动修改执行器状态
                boolean flag = !Objects.equals(item.getStatus(), HsdrConstant.ExecutorStatus.OFFLINE);
                if (flag) {
                    String itemStatus = StringUtils.isBlank(address) ? HsdrConstant.ExecutorStatus.A_OFFLINE : HsdrConstant.ExecutorStatus.ONLINE;
                    item.setStatus(itemStatus);
                }
                item.setAddressList(address);
            }
        });
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Executor createExecutor(Executor executor) {
        executor.setStatus(HsdrConstant.ExecutorStatus.ONLINE);
        if (Objects.equals(executor.getExecutorType(), BaseConstants.Flag.NO)) {
            executor.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            // 自动注册
            Assert.isTrue(StringUtils.isNotBlank(executor.getServerName()), BaseConstants.ErrorCode.DATA_INVALID);
        } else {
            executor.setTenantId(DetailsHelper.getUserDetails().getTenantId());
            // 手动注册
            Assert.isTrue(StringUtils.isNotBlank(executor.getAddressList()), BaseConstants.ErrorCode.DATA_INVALID);
        }
        executor.validateRepeat(executorRepository);
        executorRepository.insertSelective(executor);
        return executor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Executor updateExecutor(Executor executor) {
        // 清理执行器最大并发量控制缓存
        executorRepository.clearOffLineAddress(executor);
        if (Objects.equals(executor.getExecutorType(), BaseConstants.Flag.YES)) {
            // 手动录入
            executorRepository.updateOptional(executor,
                    Executor.FIELD_EXECUTOR_NAME,
                    Executor.FIELD_ADDRESS_LIST,
                    Executor.FIELD_STATUS,
                    Executor.FIELD_ORDER_SEQ);
        } else {
            executor.setAddressList(null);
            // 自动注册
            executorRepository.updateOptional(executor,
                    Executor.FIELD_EXECUTOR_NAME,
                    Executor.FIELD_ADDRESS_LIST,
                    Executor.FIELD_ORDER_SEQ,
                    Executor.FIELD_STATUS,
                    Executor.FIELD_SERVER_NAME);
        }
        return executor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExecutor(Long executorId) {
        // 判断记录是否被引用
        Assert.isTrue(executableRepository.selectCount(new Executable().setExecutorId(executorId)) == BaseConstants.Digital.ZERO, HsdrErrorCode.DELETE_ERROR);
        Assert.isTrue(jobInfoRepository.selectCount(new JobInfo().setExecutorId(executorId)) == BaseConstants.Digital.ZERO, HsdrErrorCode.DELETE_ERROR);
        Assert.isTrue(jobLogRepository.selectCount(new JobLog().setExecutorId(executorId)) == BaseConstants.Digital.ZERO, HsdrErrorCode.DELETE_ERROR);

        // 清理执行器最大并发量控制缓存
        executorRepository.clearOffLineAddress(executorId);

        executorRepository.deleteByPrimaryKey(executorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String refreshExecutor(String executorCode, String serverName) {
        Executor executor = executorRepository.selectOne(new Executor().setExecutorCode(executorCode));
        if (executor != null) {
            // 检查服务名
            if (StringUtils.isBlank(executor.getServerName())) {
                executor.setExecutorType(BaseConstants.Flag.NO)
                        .setStatus(HsdrConstant.ExecutorStatus.ONLINE)
                        .setServerName(serverName);
                executorRepository.updateByPrimaryKeySelective(executor);
                return BootSchedulerConstant.Response.SUCCESS;
            } else if (Objects.equals(serverName, executor.getServerName())) {
                return BootSchedulerConstant.Response.SUCCESS;
            } else {
                return BootSchedulerConstant.Response.FAILURE;
            }
        }
        // 新建执行器
        executorRepository.insertSelective(new Executor()
                .setExecutorCode(executorCode)
                .setExecutorName(executorCode)
                .setOrderSeq(BaseConstants.Digital.ZERO)
                .setExecutorType(BaseConstants.Flag.NO)
                .setStatus(HsdrConstant.ExecutorStatus.ONLINE)
                .setTenantId(BaseConstants.DEFAULT_TENANT_ID)
                .setServerName(serverName));
        return BootSchedulerConstant.Response.SUCCESS;
    }
}
