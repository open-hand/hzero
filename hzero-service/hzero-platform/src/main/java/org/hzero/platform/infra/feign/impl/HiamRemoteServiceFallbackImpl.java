package org.hzero.platform.infra.feign.impl;

import org.hzero.platform.api.dto.RoleDTO;
import org.hzero.platform.infra.feign.HiamRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <p>
 * HZERO-IAM 远程服务失败回调
 * </p>
 *
 * @author qingsheng.chen 2018/7/13 星期五 14:00
 */
@Component
public class HiamRemoteServiceFallbackImpl implements HiamRemoteService {
    private static final Logger logger = LoggerFactory.getLogger(HiamRemoteServiceFallbackImpl.class);


    @Override
    public RoleDTO getRole(Long tenantId, Long roleId) {
        return new RoleDTO();
    }
}
