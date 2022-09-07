package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 客户端API
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/02
 */
@Api(tags = SwaggerApiConfig.CLIENT_SITE)
@RestController
@RequestMapping(value = "/v1/clients")
public class ClientSiteController extends BaseController {

    private final ClientService clientService;

    public ClientSiteController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页模糊查询客户端")
    @GetMapping
    public ResponseEntity<Page<Client>> list(Long organizationId, String name, Integer enabledFlag, PageRequest pageRequest) {
        return Results.success(clientService.pageClient(organizationId, name, enabledFlag, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "创建客户端")
    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        this.validObject(client);
        return Results.success(clientService.create(client));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "修改客户端")
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody Client client) {
        this.validObject(client);
        clientService.update(client);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "通过id查询客户端")
    @GetMapping(value = "/{clientId}")
    public ResponseEntity<Client> query(@Encrypt @PathVariable Long clientId) {
        return Results.success(clientService.detailClient(null, clientId));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "客户端信息校验")
    @PostMapping(value = "/check")
    public ResponseEntity<Void> check(@RequestBody Client client) {
        clientService.createCheck(client);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "客户端可访问角色-查询")
    @GetMapping(value = "/access-roles/{clientId}")
    public ResponseEntity<Page<RoleVO>> listClientAccessRoles(@Encrypt @PathVariable(name = "clientId") Long clientId,
                                                              PageRequest pageRequest) {
        return Results.success(clientService.listClientAccessRoles(null, clientId, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "客户端可访问角色-新增")
    @PostMapping(value = "/access-roles/{clientId}")
    public ResponseEntity<List<MemberRole>> createClientAccessRoles(@Encrypt @PathVariable(name = "clientId") Long clientId,
                                                                    @RequestBody List<MemberRole> memberRoleList) {
        return Results.success(clientService.createAccessRoles(null, memberRoleList, clientId));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "客户端可访问角色-删除")
    @DeleteMapping(value = "/access-roles/{clientId}")
    public ResponseEntity deleteClientAccessRoles(@Encrypt @PathVariable(name = "clientId") Long clientId,
                                                  @Encrypt @RequestBody List<MemberRole> memberRoleList) {
        clientService.deleteAccessRoles(null, memberRoleList, clientId);
        return Results.success();
    }
}
