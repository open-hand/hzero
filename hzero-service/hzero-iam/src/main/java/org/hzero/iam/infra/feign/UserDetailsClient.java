package org.hzero.iam.infra.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.hzero.common.HZeroService;
import org.hzero.iam.infra.feign.fallback.UserDetailsClientImpl;

/**
 * <p>
 * 用户缓存信息修改
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:09
 */
@FeignClient(value = HZeroService.Oauth.NAME, fallback = UserDetailsClientImpl.class, path = "/oauth")
public interface UserDetailsClient {

    /**
     * 更新用户的当前角色
     *
     * @param accessToken 当前用户的token
     * @param roleId      当前角色
     * @param assignLevel 分配层级
     * @param assignValue 分配层级值
     * @return 状态码
     */
    @PostMapping("/api/user/role-id")
    ResponseEntity storeUserRole(@RequestParam("access_token") String accessToken,
                                 @RequestParam("roleId") Long roleId,
                                 @RequestParam("assignLevel") String assignLevel,
                                 @RequestParam("assignValue") Long assignValue);

    /**
     * 更新用户的当前租户
     *
     * @param accessToken 当前用户的token
     * @param tenantId    当前租户
     * @return 状态码
     */
    @PostMapping("/api/user/tenant-id")
    ResponseEntity storeUserTenant(@RequestParam("access_token") String accessToken, @RequestParam("tenantId") Long tenantId);


    /**
     * 更新用户的当前语言
     *
     * @param accessToken 当前用户的token
     * @param language    当前语言
     * @return 状态码
     */
    @PostMapping("/api/user/language")
    ResponseEntity storeUserLanguage(@RequestParam("access_token") String accessToken, @RequestParam("language") String language);

    /**
     * 更新用户的当前时区
     *
     * @param accessToken 当前用户的token
     * @param timeZone    时区
     * @return 状态码
     */
    @PostMapping("/api/user/time-zone")
    ResponseEntity storeUserTimeZone(@RequestParam("access_token") String accessToken, @RequestParam("timeZone") String timeZone);

    /**
     * 刷新可访问租户列表和可选角色列表
     *
     * @param loginNameList 用户登录名列表
     */
    @PostMapping("/api/user/refresh")
    void refresh(@RequestParam("access_token") String accessToken, @RequestBody List<String> loginNameList);
}
