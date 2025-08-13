package org.hzero.boot.platform.profile.feign.impl;

import org.hzero.boot.platform.profile.constant.ProfileConstants;
import org.hzero.boot.platform.profile.feign.ProfileRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * feign调用失败回调类
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/09 12:31
 */
@Component
public class ProfileRemoteServiceImpl implements ProfileRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileRemoteServiceImpl.class);

    @Override
    public ResponseEntity<String> getProfileValueByLevel(Long tenantId, String profileName, Long userId, Long roleId) {
        logger.error("Get profile value fail where tenantId={} and profileName={} and userId={} and roleId={}",
                        tenantId, profileName, userId, roleId);
        throw new CommonException(ProfileConstants.ErrorCode.PROFILE_FEIGN_FAIL);
    }
}
