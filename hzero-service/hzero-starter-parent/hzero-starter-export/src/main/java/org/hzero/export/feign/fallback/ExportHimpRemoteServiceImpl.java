package org.hzero.export.feign.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import org.hzero.export.feign.ExportHimpRemoteService;

/**
 * Content feign fallback
 *
 * @author : shuangfei.zhu@hand-china.com 2020/10/26 20:00
 */
public class ExportHimpRemoteServiceImpl implements ExportHimpRemoteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportHimpRemoteServiceImpl.class);

    @Override
    public ResponseEntity<String> getTemplate(Long organizationId, String templateCode, String lang) {
        LOGGER.error("Feign call to get template failed!!!");
        return null;
    }
}