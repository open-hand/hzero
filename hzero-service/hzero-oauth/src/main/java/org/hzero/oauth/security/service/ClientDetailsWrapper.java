package org.hzero.oauth.security.service;

import io.choerodon.core.oauth.CustomClientDetails;

/**
 * @author qingsheng.chen@hand-china.com
 */
public interface ClientDetailsWrapper {

    /**
     * 包裹额外信息
     * @param details 客户端西悉尼
     * @param clientId 客户端ID
     * @param tenantId 租户ID
     */
    void warp(CustomClientDetails details, Long clientId, Long tenantId);
}
