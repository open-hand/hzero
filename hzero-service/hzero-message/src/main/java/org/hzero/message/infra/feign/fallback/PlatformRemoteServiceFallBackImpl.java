package org.hzero.message.infra.feign.fallback;

import org.hzero.message.infra.feign.PlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author fanghan.liu 2020/05/14 17:28
 */
@Component
public class PlatformRemoteServiceFallBackImpl implements PlatformService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformRemoteServiceFallBackImpl.class);

    @Override
    public ResponseEntity<String> getOpenUserIdsByUserIds(Long organizationId, String ids, String thirdPlatformType) {
        logger.error("Error to list open userIds, params[ids = {}]", ids);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
