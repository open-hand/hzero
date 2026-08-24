package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.OnLineUserDTO;
import org.hzero.platform.app.service.OnlineUserService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(tags = PlatformSwaggerApiConfig.ONLINE_USER_SITE)
@RestController("onlineUserSiteController.v1")
@RequestMapping("/v1/online-users")
public class OnlineUserSiteController {

    @Autowired
    private OnlineUserService onlineUserService;

    @ApiOperation(value = "查询在线用户列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<OnLineUserDTO>> pageOnlineUser(Long tenantId, @ApiIgnore PageRequest pageRequest) {
        return Results.success(onlineUserService.pageOnlineUser(pageRequest.getPage(), pageRequest.getSize(), tenantId));
    }

    @ApiOperation(value = "在线用户列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/list")
    public ResponseEntity<List<OnLineUserDTO>> listOnlineUser(Long tenantId) {
        return Results.success(onlineUserService.listOnlineUser(tenantId));
    }

}