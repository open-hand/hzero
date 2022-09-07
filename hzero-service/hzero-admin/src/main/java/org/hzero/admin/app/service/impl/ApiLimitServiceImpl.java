package org.hzero.admin.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.api.dto.ApiLimitDTO;
import org.hzero.admin.app.service.ApiLimitService;
import org.hzero.admin.app.service.ApiMonitorRuleService;
import org.hzero.admin.domain.entity.ApiLimit;
import org.hzero.admin.domain.repository.ApiLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 4:48 下午
 */
@Service
public class ApiLimitServiceImpl implements ApiLimitService {

    @Lazy
    @Autowired
    private ApiMonitorRuleService apiMonitorRuleService;
    @Lazy
    @Autowired
    private ApiLimitRepository apiLimitRepository;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public ApiLimitDTO createOrUpdate(ApiLimitDTO apiLimitDTO) {
        ApiLimit apiLimit = new ApiLimit(apiLimitDTO);
        if (StringUtils.equals(ApiMonitorRuleService.BLACK_MODE, apiLimit.getListMode()) && apiLimit.getBlacklistThreshold() == null) {
            throw new CommonException("hadm.warn.apiLimit.thresholdNotBlankInBlackMode");
        }
        if (apiLimit.getApiLimitId() == null){
            apiLimitRepository.insertSelective(apiLimit);
        } else {
            ApiLimit old = apiLimitRepository.selectByPrimaryKey(apiLimit.getApiLimitId());
            apiLimit.setObjectVersionNumber(old.getObjectVersionNumber());
            apiLimitRepository.updateByPrimaryKey(apiLimit);
        }
        List<Long> ids = new ArrayList<>();
        ids.add(apiLimit.getMonitorRuleId());
        apiMonitorRuleService.apply(ids);
        return new ApiLimitDTO(apiLimit);
    }

    @Override
    public ApiLimitDTO detail(Long monitorRuleId) {
        ApiLimit apiLimit = apiLimitRepository.selectOne(new ApiLimit().setMonitorRuleId(monitorRuleId));
        return new ApiLimitDTO(apiLimit);
    }

}
