package org.hzero.admin.app.service;

import org.hzero.admin.api.dto.ApiLimitDTO;
import org.hzero.admin.domain.entity.ApiLimit;
import org.hzero.admin.domain.entity.ApiMonitor;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 4:47 下午
 */
public interface ApiLimitService {

    /**
     * 创建或更新
     * @param apiLimitDTO
     * @return
     */
    ApiLimitDTO createOrUpdate(ApiLimitDTO apiLimitDTO);

    /**
     * 根据monitorRuleId查询
     * @param monitorRuleId
     * @return
     */
    ApiLimitDTO detail(Long monitorRuleId);

}
