package org.hzero.oauth.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.oauth.security.custom.CustomRedisTokenStore;

/**
 *
 * @author bojiangzhou 2019/11/08 接口改成内部接口
 * @author XCXCXCXCX
 * @since 1.0
 */
@Api(tags = "Admin Manager")
@RestController("v1.adminController")
@RequestMapping("/admin")
public class AdminController {

    private CustomRedisTokenStore tokenStore;

    public AdminController(TokenStore tokenStore) {
        this.tokenStore = (CustomRedisTokenStore) tokenStore;
    }

    @ApiOperation("根据用户名失效Token")
    @Permission(permissionWithin = true)
    @DeleteMapping("/token/username")
    public ResponseEntity<?> invalidByUsername(@RequestParam("loginName") String loginName){
        tokenStore.removeAccessTokenByLoginName(loginName);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @ApiOperation("根据令牌失效Token")
    @Permission(permissionWithin = true)
    @DeleteMapping("/token/one")
    public ResponseEntity<?> invalidByToken(@RequestParam("token") String token){
        tokenStore.removeAccessToken(token);
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @ApiOperation("根据令牌批量失效Token")
    @Permission(permissionWithin = true)
    @DeleteMapping("/token/batch")
    public ResponseEntity<?> invalidByTokenBatch(@RequestParam("tokens") List<String> tokens){
        tokens.forEach(token -> tokenStore.removeAccessToken(token));
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

}
