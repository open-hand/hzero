package org.hzero.boot.message.feign.fallback;

import org.hzero.boot.message.feign.PlatformRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/14 10:24
 */
@Component
public class PlatformRemoteImpl implements PlatformRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformRemoteImpl.class);

    @Override
    public ResponseEntity<String> listOnlineUser(Long organizationId) {
        logger.error("Error get Online users");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
