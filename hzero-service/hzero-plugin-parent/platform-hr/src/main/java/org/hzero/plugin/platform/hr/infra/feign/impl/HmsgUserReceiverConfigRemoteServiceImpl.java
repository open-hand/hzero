package org.hzero.plugin.platform.hr.infra.feign.impl;

import org.hzero.plugin.platform.hr.infra.feign.HmsgUserReceiverConfigRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author fanghan.liu 2020/04/14 14:16
 */
@Component
public class HmsgUserReceiverConfigRemoteServiceImpl implements HmsgUserReceiverConfigRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(HmsgUserReceiverConfigRemoteServiceImpl.class);

    @Override
    public ResponseEntity<Void> refreshTenant(Long tenantId) {
        logger.error("error refresh user message receive config");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
