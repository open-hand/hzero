package org.hzero.platform.app.service.impl;

import org.hzero.platform.app.service.PermissionRangeService;
import org.hzero.platform.domain.entity.PermissionRange;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 屏蔽范围应用服务默认实现
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@Service
public class PermissionRangeServiceImpl implements PermissionRangeService {

    @Autowired
    private PermissionRangeRepository permissionRangeRepository;

    @Override
    public PermissionRange updatePermissionRange(PermissionRange permissionRange) {
        return permissionRangeRepository.updatePermissionRange(permissionRange);
    }
}
