package org.hzero.message.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.message.domain.entity.UserGroupAssign;
import org.hzero.message.infra.feign.fallback.IamRemoteServiceFallBackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * iam
 *
 * @author minghui.qiu@hand-china.com 2019/6/14 11:15
 */
@FeignClient(value = HZeroService.Iam.NAME, fallback = IamRemoteServiceFallBackImpl.class)
public interface IamRemoteService {


    /**
     * "获取用户组下的用户Id及用户所属租户Id-用于消息公告管理界面"
     *
     * @param userGroupAssigns 用户组列表
     * @return Set<Receiver>
     */
    @PostMapping("/v1/user-groups/user-ids")
    ResponseEntity<String> listUserGroupAssignUsers(@RequestBody List<UserGroupAssign> userGroupAssigns);

    /**
     * "获取用户组下的用户对应第三方平台用户ID"
     *
     * @param userGroupAssigns  用户组列表
     * @param thirdPlatformType 三方平台类型
     * @return Set<Receiver>
     */
    @PostMapping("/v1/user-groups/open/user-ids")
    ResponseEntity<String> listOpenUserGroupAssignUsers(@RequestBody List<UserGroupAssign> userGroupAssigns,
                                                        @RequestParam("thirdPlatformType") String thirdPlatformType);

    /**
     * 查询用户
     *
     * @param organizationId 租户ID
     * @param page           页
     * @param size           大小
     * @return 用户
     */
    @GetMapping("/hzero/v1/{organizationId}/users/paging")
    ResponseEntity<String> pageUser(@PathVariable("organizationId") Long organizationId,
                                    @RequestParam("page") Integer page,
                                    @RequestParam("size") Integer size);

    /**
     * 查询角色已分配的用户
     *
     * @param organizationId 租户ID
     * @param roleId         角色ID
     * @param page           页
     * @param size           大小
     * @return 用户
     */
    @GetMapping("/hzero/v1/{organizationId}/member-roles/role-users/{roleId}")
    ResponseEntity<String> listRoleMembers(@PathVariable(name = "organizationId") Long organizationId,
                                           @PathVariable(name = "roleId") Long roleId,
                                           @RequestParam("page") Integer page,
                                           @RequestParam("size") Integer size);

    /**
     * 查询用户
     *
     * @param organizationId 租户ID
     * @param userIds        用户ID
     * @return 用户
     */
    @GetMapping("/hzero/v1/{organizationId}/users/by-ids")
    ResponseEntity<String> listReceiverByUserIds(@PathVariable("organizationId") Long organizationId,
                                                 @RequestParam("userIds") List<Long> userIds);

    /**
     * 查询用户
     *
     * @param organizationId    租户ID
     * @param userIds           用户ID
     * @param thirdPlatformType 三方平台类型
     * @return 用户
     */
    @GetMapping("/hzero/v1/{organizationId}/open-users/by-ids")
    ResponseEntity<String> listOpenReceiverByUserIds(@PathVariable("organizationId") Long organizationId,
                                                     @RequestParam("userIds") List<Long> userIds,
                                                     @RequestParam("thirdPlatformType") String thirdPlatformType);
}
