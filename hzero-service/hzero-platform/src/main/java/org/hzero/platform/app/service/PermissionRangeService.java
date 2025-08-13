package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.PermissionRange;

/**
 * 屏蔽范围应用服务
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
public interface PermissionRangeService {

    /**
     * 更新屏蔽范围
     *
     * @param permissionRange 屏蔽范围
     * @return 更新结果
     */
    PermissionRange updatePermissionRange(PermissionRange permissionRange);
}
