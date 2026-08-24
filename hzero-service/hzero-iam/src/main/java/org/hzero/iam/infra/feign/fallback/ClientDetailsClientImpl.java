package org.hzero.iam.infra.feign.fallback;

import org.hzero.iam.infra.feign.ClientDetailsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 客户端操作失败回调
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:10
 */
@Component
public class ClientDetailsClientImpl implements ClientDetailsClient {
    private static final Logger logger = LoggerFactory.getLogger(ClientDetailsClientImpl.class);

    @Override
    public ResponseEntity storeClientRole(String accessToken, Long roleId, String assignLevel, Long assignValue) {
        logger.error("Error store client accessToken = {} and roleId {}", accessToken, roleId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity storeClientTenant(String accessToken, Long tenantId) {
        logger.error("Error store client accessToken = {} and tenantId {}", accessToken, tenantId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
