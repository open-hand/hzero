package org.hzero.iam.api.controller.v1;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.service.user.ClientDetailsService;

/**
 * <p>
 * 当前客户端详细信息管理API
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 15:31
 */
@Api(tags = SwaggerApiConfig.USER_DETAILS_SITE)
@RestController("clientDetailsSiteController.v1")
@RequestMapping("/v1/clients")
public class ClientDetailsSiteController extends BaseController {
    private static final String TENANT_ID = "tenantId";

    private ClientDetailsService clientDetailsService;


    @Autowired
    public ClientDetailsSiteController(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @ApiOperation("查询当前客户端的当前租户")
    @Permission(permissionLogin = true)
    @GetMapping("/tenant-id")
    public ResponseEntity<Map<String, Long>> readClientTenant() {
        Map<String, Long> result = new HashMap<>(1);
        result.put(TENANT_ID, clientDetailsService.readClientTenant());
        return Results.success(result);
    }

    @ApiOperation("缓存当前客户端的租户")
    @Permission(permissionLogin = true)
    @PutMapping("/tenant-id")
    public ResponseEntity<Map<String, Long>> storeClientTenant(@RequestParam Long tenantId) {
        clientDetailsService.storeClientTenant(tenantId);
        return Results.success();
    }
}
