package org.hzero.oauth.api.controller.v1;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.boot.platform.data.hierarchy.AdditionInfoValueHandler;
import org.hzero.core.util.Results;
import org.hzero.oauth.security.custom.CustomRedisTokenStore;
import org.hzero.oauth.security.service.UserDetailsWrapper;

/**
 * <p>
 * 更换用户当前信息
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:01
 */
@RestController("v1.userDetailsController")
@RequestMapping("api/user")
public class UserDetailsController {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private UserDetailsWrapper userDetailsWrapper;
    @Autowired
    private CustomRedisTokenStore customRedisTokenStore;
    @Autowired(required = false)
    private List<AdditionInfoValueHandler> additionInfoValueHandlerList;

    @ApiOperation("更换用户当前角色")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/role-id")
    public ResponseEntity<Void> storeUserRole(@RequestParam("access_token") String accessToken,
                                              @RequestParam long roleId) {
        return storeAccessToken(accessToken, customUserDetails -> userDetailsWrapper.warpRoleInfo(customUserDetails, roleId));
    }

    @ApiOperation("更换用户当前租户")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/tenant-id")
    public ResponseEntity<Void> storeUserTenant(@RequestParam("access_token") String accessToken,
                                                @RequestParam long tenantId) {
        return storeAccessToken(accessToken, customUserDetails -> {
            customUserDetails.setRoleId(null);
            customUserDetails.setTenantId(null);
            userDetailsWrapper.warp(customUserDetails, customUserDetails.getUserId(), tenantId, false);
        });
    }

    @ApiOperation("更换用户当前语言")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/language")
    public ResponseEntity<Void> storeLanguage(@RequestParam("access_token") String accessToken,
                                              @RequestParam String language) {
        return storeAccessToken(accessToken, customUserDetails -> customUserDetails.setLanguage(language));
    }

    @ApiOperation("更换用户当前时区")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/time-zone")
    public ResponseEntity<Void> storeTimeZone(@RequestParam("access_token") String accessToken,
                                              @RequestParam String timeZone) {
        return storeAccessToken(accessToken, customUserDetails -> customUserDetails.setTimeZone(timeZone));
    }

    @ApiOperation("刷新可访问租户列表和可选角色列表")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@RequestBody List<String> loginNameList) {
        if (!CollectionUtils.isEmpty(loginNameList)) {
            loginNameList.forEach(loginName -> {
                Collection<String> tokens = customRedisTokenStore.findTokenValuesByLoginName(loginName);
                if (!CollectionUtils.isEmpty(tokens)) {
                    tokens.forEach(accessToken ->
                            storeAccessToken(accessToken, customUserDetails ->
                                    userDetailsWrapper.warp(customUserDetails, customUserDetails.getUserId(), customUserDetails.getTenantId(), false)
                            )
                    );
                }
            });
        }
        return Results.success();
    }

    @ApiOperation("更新用户附加信息")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true, permissionWithin = true)
    @PostMapping("/addition-info")
    public ResponseEntity<Void> storeUserAdditionInfo(@RequestParam("access_token") String accessToken,
                                                      @RequestParam String dataHierarchyCode,
                                                      @RequestParam String dataHierarchyValue,
                                                      @RequestParam String dataHierarchyMeaning,
                                                      @RequestParam(required = false) List<String> childrenDataHierarchyCodes) {
        AtomicReference<String> dataHierarchyValueReference = new AtomicReference<>(dataHierarchyValue);
        AtomicReference<String> dataHierarchyMeaningReference = new AtomicReference<>(dataHierarchyMeaning);
        return storeAccessToken(accessToken, customUserDetails -> {
            if (!CollectionUtils.isEmpty(this.additionInfoValueHandlerList)) {
                for (AdditionInfoValueHandler additionInfoValueHandler : additionInfoValueHandlerList) {
                    String handleValue = additionInfoValueHandler.valueHandler(DetailsHelper.getUserDetails(), dataHierarchyCode, dataHierarchyValueReference.get(), dataHierarchyMeaningReference.get());
                    if (StringUtils.hasText(handleValue)) {
                        dataHierarchyValueReference.set(handleValue);
                    }
                    String handleMeaning = additionInfoValueHandler.meaningHandler(DetailsHelper.getUserDetails(), dataHierarchyCode, dataHierarchyValueReference.get(), dataHierarchyMeaningReference.get());
                    if (StringUtils.hasText(handleMeaning)) {
                        dataHierarchyMeaningReference.set(handleMeaning);
                    }
                }
            }
            customUserDetails.addAdditionInfo(dataHierarchyCode, dataHierarchyValueReference.get())
                    .addAdditionMeaning(dataHierarchyCode, dataHierarchyMeaningReference.get())
                    .removeAdditionInfos(childrenDataHierarchyCodes);
        });
    }

    @SuppressWarnings("Duplicates")
    private ResponseEntity<Void> storeAccessToken(String accessToken, ResetUserDetails resetUserDetails) {
        if (StringUtils.isEmpty(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
        if (oAuth2AccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        OAuth2Authentication authentication = tokenStore.readAuthentication(oAuth2AccessToken);
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            // 更换租户的时候更换角色
            CustomUserDetails customUserDetails = (CustomUserDetails) principal;
            resetUserDetails.resetUserDetails(customUserDetails);
            tokenStore.storeAccessToken(oAuth2AccessToken, authentication);
        }
        return Results.success();
    }

    @FunctionalInterface
    private interface ResetUserDetails {
        /**
         * 重新设置用户信息
         *
         * @param customUserDetails 用户信息
         */
        void resetUserDetails(CustomUserDetails customUserDetails);
    }
}
