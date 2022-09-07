package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.OnLineUserDTO;
import org.hzero.platform.api.dto.OnlineUserCountDTO;
import org.hzero.platform.app.service.OnlineUserService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 在线用户
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/12 11:11
 */
@Api(tags = PlatformSwaggerApiConfig.ONLINE_USER)
@RestController("onlineUserController.v1")
@RequestMapping("/v1")
public class OnlineUserController {

    @Autowired
    private OnlineUserService onlineUserService;

    @ApiOperation(value = "分页查询在线用户列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/online-users")
    public ResponseEntity<Page<OnLineUserDTO>> pageOnlineUser(@PathVariable Long organizationId, @ApiIgnore PageRequest pageRequest) {
        return Results.success(onlineUserService.pageOnlineUser(pageRequest.getPage(), pageRequest.getSize(), organizationId));
    }

    @ApiOperation(value = "在线用户列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/online-users/list")
    public ResponseEntity<List<OnLineUserDTO>> listOnlineUser(@PathVariable Long organizationId) {
        return Results.success(onlineUserService.listOnlineUser(organizationId));
    }

    @ApiOperation(value = "在线的账户数量")
    @Permission(permissionWithin = true)
    @GetMapping("/online-users/count")
    public ResponseEntity<Integer> countOnline() {
        return Results.success(onlineUserService.countOnline());
    }

    @ApiOperation(value = "在线用户统计")
    @Permission(permissionLogin = true)
    @GetMapping("/online-users/statistics")
    public ResponseEntity<List<OnlineUserCountDTO>> countOnlineUser() {
        return Results.success(onlineUserService.countOnlineUser());
    }

    @ApiOperation(value = "指定时长的在线用户")
    @Permission(permissionLogin = true)
    @GetMapping("/online-users/hour")
    public ResponseEntity<Page<OnLineUserDTO>> pageWithHour(String hour, @ApiIgnore PageRequest pageRequest) {
        return Results.success(onlineUserService.pageWithHour(pageRequest, hour));
    }
}
