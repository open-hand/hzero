package org.hzero.iam.infra.feign.fallback;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.hzero.iam.infra.feign.OauthAdminFeignClient;

/**
 * description
 *
 * @author xiaoyu.zhao@hand-china.com 2019/11/08 15:28
 */
@Component
public class OauthAdminFeignFallback implements OauthAdminFeignClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthAdminFeignFallback.class);

    @Override
    public ResponseEntity<?> invalidByUsername(String loginName) {
        LOGGER.error("invalid token by user loginName failed, loginName is :{}", loginName);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<?> invalidByToken(String token) {
        LOGGER.error("invalid token by token failed, token is :{}", token);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<?> invalidByTokenBatch(List<String> tokens) {
        LOGGER.error("batch invalid token by tokens failed, tokens is :{}", tokens);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> getPublicKey() {
        LOGGER.error("get public key from oauth error.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> getEncryptPass(String pass) {
        LOGGER.error("get encrypted password from oauth error.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
