package org.hzero.iam.api.controller.v1;

import java.util.HashMap;
import java.util.Map;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.service.user.ClientDetailsService;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 当前客户端登录详细信息管理
 *
 * @author qingsheng.chen@hand-china.com
 */
@Api(tags = SwaggerApiConfig.CLIENT_DETAILS)
@RestController("clientDetailsController.v1")
@RequestMapping("/v1")
public class ClientDetailsController extends BaseController {
    private static final String ROLE_ID = "roleId";
    private ClientDetailsService clientDetailsService;


    @Autowired
    public ClientDetailsController(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @ApiOperation("查询当前客户端的当前角色")
    @Permission(permissionLogin = true)
    @GetMapping("/clients/roles")
    public ResponseEntity<Map<String, Long>> readClientRole() {
        Map<String, Long> result = new HashMap<>(1);
        result.put(ROLE_ID, clientDetailsService.readClientRole());
        return Results.success(result);
    }

    @ApiOperation("缓存当前客户端的角色")
    @Permission(permissionLogin = true)
    @PutMapping("/clients/roles")
    public ResponseEntity<Void> storeClientRole(@RequestParam @Encrypt Long roleId) {
        clientDetailsService.storeClientRole(roleId);
        return Results.success();
    }
}
