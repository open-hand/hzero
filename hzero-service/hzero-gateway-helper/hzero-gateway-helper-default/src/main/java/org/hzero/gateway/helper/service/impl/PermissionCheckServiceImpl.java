package org.hzero.gateway.helper.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.hzero.gateway.helper.domain.entity.PermissionCheck;
import org.hzero.gateway.helper.domain.repository.PermissionCheckRepository;
import org.hzero.gateway.helper.entity.CheckResponse;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.service.PermissionCheckService;


/**
 * 缺失权限应用服务实现
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 */
@Service
public class PermissionCheckServiceImpl implements PermissionCheckService {

    @Autowired
    private PermissionCheckRepository permissionCheckRepository;

    @Override
    @Async("permissionCheckSaveExecutor")
    public void savePermissionCheck(RequestContext requestContext, CheckResponse checkResponse) {
        permissionCheckRepository.insertSelective(PermissionCheck.build(requestContext, checkResponse));
    }
}
