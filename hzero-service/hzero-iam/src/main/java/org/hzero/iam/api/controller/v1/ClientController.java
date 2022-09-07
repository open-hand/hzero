package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.ClientService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.repository.ClientRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 客户端API
 *
 * @author bojiangzhou 2019/08/02
 */
@Api(tags = SwaggerApiConfig.CLIENT)
@RestController
@RequestMapping(value = "/v1")
public class ClientController extends BaseController {

    private final ClientRepository clientRepository;
    private final ClientService clientService;

    public ClientController(ClientRepository clientRepository, ClientService clientService) {
        this.clientRepository = clientRepository;
        this.clientService = clientService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页模糊查询客户端")
    @GetMapping("/{organizationId}/clients")
    public ResponseEntity<Page<Client>> list(@PathVariable Long organizationId, String name, Integer enabledFlag, PageRequest pageRequest) {
        return Results.success(clientService.pageClient(organizationId, name, enabledFlag, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建客户端")
    @PostMapping("/{organizationId}/clients")
    public ResponseEntity<Client> create(@PathVariable Long organizationId, @RequestBody Client client) {
        client.setOrganizationId(organizationId);
        this.validObject(client);
        return Results.success(clientService.create(client));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改客户端")
    @PutMapping("/{organizationId}/clients")
    public ResponseEntity<Void> update(@PathVariable("organizationId") Long organizationId, @RequestBody Client client) {
        client.setOrganizationId(organizationId);
        this.validObject(client);
        clientService.update(client);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "通过id查询客户端")
    @GetMapping(value = "/{organizationId}/clients/{clientId}")
    public ResponseEntity<Client> query(@PathVariable Long organizationId, @Encrypt @PathVariable Long clientId) {
        return Results.success(clientService.detailClient(organizationId, clientId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "客户端信息校验")
    @PostMapping(value = "/{organizationId}/clients/check")
    public ResponseEntity<Void> check(@PathVariable(name = "organizationId") Long organizationId, @RequestBody Client client) {
        client.setOrganizationId(organizationId);
        clientService.createCheck(client);
        return Results.success();
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "跨租户查询客户端简要信息")
    @GetMapping("/clients/paging/all")
    public ResponseEntity<Page<Client>> pageClient(@RequestParam @Encrypt Long roleId, String name, PageRequest pageRequest) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        return Results.success(clientRepository.pageClientSimple(tenantId, roleId, name, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "客户端可访问角色-查询")
    @GetMapping(value = "/{organizationId}/clients/access-roles/{clientId}")
    public ResponseEntity<Page<RoleVO>> listClientAccessRoles(@PathVariable(name = "organizationId") Long organizationId,
                                                              @Encrypt @PathVariable(name = "clientId") Long clientId,
                                                              PageRequest pageRequest) {
        return Results.success(clientService.listClientAccessRoles(organizationId, clientId, pageRequest));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "客户端可访问角色-新增")
    @PostMapping(value = "/{organizationId}/clients/access-roles/{clientId}")
    public ResponseEntity<List<MemberRole>> createClientAccessRoles(@PathVariable(name = "organizationId") Long organizationId,
                                                                    @Encrypt @PathVariable(name = "clientId") Long clientId,
                                                                    @RequestBody List<MemberRole> memberRoleList) {
        return Results.success(clientService.createAccessRoles(organizationId, memberRoleList, clientId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "客户端可访问角色-删除")
    @DeleteMapping(value = "/{organizationId}/clients/access-roles/{clientId}")
    public ResponseEntity<Void> deleteClientAccessRoles(@PathVariable(name = "organizationId") Long organizationId,
                                                                  @Encrypt @PathVariable(name = "clientId") Long clientId,
                                                                  @Encrypt @RequestBody List<MemberRole> memberRoleList) {
        clientService.deleteAccessRoles(organizationId, memberRoleList, clientId);
        return Results.success();
    }
}
