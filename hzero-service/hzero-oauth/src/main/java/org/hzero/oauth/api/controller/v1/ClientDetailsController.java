package org.hzero.oauth.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomClientDetails;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.oauth.security.service.ClientDetailsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 更换用户当前信息
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:01
 */
@RestController("v1.clientDetailsController")
@RequestMapping("api/client")
public class ClientDetailsController {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientDetailsWrapper clientDetailsWrapper;

    @ApiOperation("更换用户当前角色")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/role-id")
    public ResponseEntity<Void> storeClientRole(@RequestParam("access_token") String accessToken,
                                                @RequestParam long roleId) {
        return storeAccessToken(accessToken, customClientDetails -> customClientDetails
                .setCurrentRoleId(roleId));
    }

    @ApiOperation("更换用户当前租户")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/tenant-id")
    public ResponseEntity<Void> storeClientTenant(@RequestParam("access_token") String accessToken,
                                                  @RequestParam long tenantId) {
        return storeAccessToken(accessToken, customClientDetails -> {
            clientDetailsWrapper.warp(customClientDetails, customClientDetails.getId(), tenantId);
        });
    }

    @SuppressWarnings("Duplicates")
    private ResponseEntity<Void> storeAccessToken(String accessToken, ResetClientDetails resetClientDetails) {
        if (StringUtils.isEmpty(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
        if (oAuth2AccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OAuth2Authentication authentication = tokenStore.readAuthentication(oAuth2AccessToken);
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomClientDetails) {
            // 更换租户的时候更换角色
            CustomClientDetails customClientDetails = (CustomClientDetails) principal;
            resetClientDetails.resetClientDetails(customClientDetails);
            tokenStore.storeAccessToken(oAuth2AccessToken, authentication);
        }
        return Results.success();
    }

    @FunctionalInterface
    private interface ResetClientDetails {
        /**
         * 重新设置用户信息
         *
         * @param customClientDetails 用户信息
         */
        void resetClientDetails(CustomClientDetails customClientDetails);
    }
}
