package org.hzero.iam.infra.feign.fallback;

import org.hzero.iam.infra.feign.UserDetailsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 用户操作失败回调
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:10
 */
@Component
public class UserDetailsClientImpl implements UserDetailsClient {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsClientImpl.class);

    @Override
    public ResponseEntity storeUserRole(String accessToken, Long roleId, String assignLevel, Long assignValue) {
        logger.error("Error store user accessToken = {} and roleId {}", accessToken, roleId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity storeUserTenant(String accessToken, Long tenantId) {
        logger.error("Error store user accessToken = {} and tenantId {}", accessToken, tenantId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity storeUserLanguage(String accessToken, String language) {
        logger.error("Error store user accessToken = {} and language {}", accessToken, language);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity storeUserTimeZone(String accessToken, String timeZone) {
        logger.error("Error store user accessToken = {} and timeZone {}", accessToken, timeZone);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public void refresh(String accessToken, List<String> loginNameList) {
        logger.error("Error refresh user {}, accessToken {}", loginNameList, accessToken);
    }
}
