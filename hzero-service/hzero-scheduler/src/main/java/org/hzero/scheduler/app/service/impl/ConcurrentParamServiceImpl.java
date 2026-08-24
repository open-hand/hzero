package org.hzero.scheduler.app.service.impl;

import org.hzero.scheduler.app.service.ConcurrentParamService;
import org.hzero.scheduler.domain.entity.ConcurrentParam;
import org.hzero.scheduler.domain.repository.ConcurrentParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 并发程序参数ServiceImpl
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/11 10:44
 */
@Service
public class ConcurrentParamServiceImpl implements ConcurrentParamService {

    @Autowired
    private ConcurrentParamRepository concurrentParamRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConcurrentParam createConcurrentParam(ConcurrentParam concurrentParam) {
        concurrentParam.validateParam();
        concurrentParamRepository.insertSelective(concurrentParam);
        return concurrentParam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConcurrentParam updateConcurrentParam(ConcurrentParam concurrentParam) {
        concurrentParam.validateParam();
        concurrentParamRepository.updateOptional(concurrentParam,
                ConcurrentParam.FIELD_ORDER_SEQ,
                ConcurrentParam.FIELD_PARAM_NAME,
                ConcurrentParam.FIELD_PARAM_FORMAT_CODE,
                ConcurrentParam.FIELD_PARAM_EDIT_TYPE_CODE,
                ConcurrentParam.FIELD_NOTNULL_FLAG,
                ConcurrentParam.FIELD_BUSINESS_MODEL,
                ConcurrentParam.FIELD_VALUE_FILED_FROM,
                ConcurrentParam.FIELD_VALUE_FILED_TO,
                ConcurrentParam.FIELD_SHOW_FLAG,
                ConcurrentParam.FIELD_ENABLED_FLAG,
                ConcurrentParam.FIELD_DEFAULT_VALUE);
        return concurrentParam;
    }
}
