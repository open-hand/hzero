package org.hzero.plugin.platform.hr.infra.feign.impl;

import org.hzero.plugin.platform.hr.infra.feign.HiamUserRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author xiaoyu.zhao@hand-china.com 2020/03/25 9:26
 */
@Component
public class HiamUserRemoteServiceImpl implements FallbackFactory<HiamUserRemoteService> {

    private static final Logger logger = LoggerFactory.getLogger(HiamUserRemoteServiceImpl.class);

    @Override
    public HiamUserRemoteService create(Throwable throwable) {
        logger.error("locked user failed, feign exception is : {}", throwable.getMessage());
        return new HiamUserRemoteService() {
            @Override
            public ResponseEntity lockUser(Long organizationId, Long userId) {
                throw new CommonException("Remote call iam service failed to lock user, organizationId is :{"+organizationId+"}, userId is :{"+userId+"}");
            }
        };
    }
}
