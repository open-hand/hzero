package org.hzero.iam.infra.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.hzero.common.HZeroService;
import org.hzero.iam.infra.feign.fallback.OauthAdminFeignFallback;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
@FeignClient(value = HZeroService.Oauth.NAME, fallback = OauthAdminFeignFallback.class)
public interface OauthAdminFeignClient {

    /**
     * 根据用户名失效Token
     * @param loginName 登录名
     */
    @DeleteMapping("/oauth/admin/token/username")
    ResponseEntity<?> invalidByUsername(@RequestParam("loginName") String loginName);

    /**
     * 根据令牌失效Token
     * @param token token
     */
    @DeleteMapping("/oauth/admin/token/one")
    ResponseEntity<?> invalidByToken(@RequestParam("token") String token);

    /**
     * 根据令牌批量失效Token
     * @param tokens tokens
     */
    @DeleteMapping("/oauth/admin/token/batch")
    ResponseEntity<?> invalidByTokenBatch(@RequestParam("tokens") List<String> tokens);

    /**
     * 获取密码加密的公钥
     */
    @GetMapping("/oauth/admin/pass/public-key")
    ResponseEntity<String> getPublicKey();

    /**
     * 获取加密后的密码
     */
    @PostMapping("/oauth/admin/pass/encrypt")
    ResponseEntity<String> getEncryptPass(@RequestParam("pass") String pass);

}
