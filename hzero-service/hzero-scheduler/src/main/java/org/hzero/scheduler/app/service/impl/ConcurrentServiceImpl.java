package org.hzero.scheduler.app.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.scheduler.app.service.ConcurrentParamService;
import org.hzero.scheduler.app.service.ConcurrentService;
import org.hzero.scheduler.domain.entity.Concurrent;
import org.hzero.scheduler.domain.entity.ConcurrentParam;
import org.hzero.scheduler.domain.entity.Executable;
import org.hzero.scheduler.domain.repository.ConcurrentParamRepository;
import org.hzero.scheduler.domain.repository.ConcurrentRepository;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发程序ServiceImpl
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/11 10:43
 */
@Service
public class ConcurrentServiceImpl implements ConcurrentService {

    @Autowired
    private ExecutableRepository executableRepository;
    @Autowired
    private ConcurrentRepository concurrentRepository;
    @Autowired
    private ConcurrentParamRepository concurrentParamRepository;
    @Autowired
    private ConcurrentParamService concurrentParamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Concurrent createConcurrent(Concurrent concurrent) {
        // 校验executableId是否存在
        Assert.isTrue(executableRepository.selectCount(new Executable().setExecutableId(concurrent.getExecutableId())) > BaseConstants.Digital.ZERO, BaseConstants.ErrorCode.NOT_FOUND);
        // 校验头code是否重复
        concurrent.validateConcCodeRepeat(concurrentRepository);
        // 保存头
        concurrentRepository.insertSelective(concurrent);
        List<ConcurrentParam> paramList = concurrent.getParamList();
        if (CollectionUtils.isNotEmpty(paramList)) {
            paramList.forEach(param -> {
                // 回写concurrentId
                param.setConcurrentId(concurrent.getConcurrentId());
                param.setTenantId(concurrent.getTenantId());
                param.validateParam();
                param.validateParamCodeRepeat(concurrentParamRepository);
                concurrentParamRepository.insertSelective(param);
            });
        }
        return concurrent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Concurrent updateConcurrent(Concurrent concurrent) {
        concurrentRepository.updateOptional(concurrent,
                Concurrent.FIELD_CONC_NAME,
                Concurrent.FIELD_CONC_DESCRIPTION,
                Concurrent.FIELD_ALARM_EMAIL,
                Concurrent.FIELD_ENABLED_FLAG);
        // 更新行数据
        for (ConcurrentParam param : concurrent.getParamList()) {
            // 写入外键和租户并校验
            param.setConcurrentId(concurrent.getConcurrentId());
            param.setTenantId(concurrent.getTenantId());
            if (param.getConcParamId() != null) {
                // 更新
                concurrentParamService.updateConcurrentParam(param);
            } else {
                // 新建
                param.validateParamCodeRepeat(concurrentParamRepository);
                concurrentParamService.createConcurrentParam(param);
            }
        }
        return concurrent;
    }

    @Override
    public Page<Concurrent> pageConcurrentByTenantId(Long tenantId, String concCode, String concName, PageRequest pageRequest, boolean flag) {
        LocalDate date = LocalDateTime.now().toLocalDate();
        if (DetailsHelper.getUserDetails().getAdmin()) {
            return PageHelper.doPage(pageRequest, () -> concurrentRepository.adminListConcurrentByTenantId(tenantId, concCode, concName, date));
        } else {
            return PageHelper.doPageAndSort(pageRequest, () -> concurrentRepository.listConcurrentByTenantId(tenantId, DetailsHelper.getUserDetails().roleMergeIds(), concCode, concName, date, flag));

        }
    }
}