package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.ServerService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Server;
import org.hzero.platform.domain.repository.ServerRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 文件服务器 管理 API
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Api(tags = PlatformSwaggerApiConfig.SERVER)
@RestController("serverController.v1")
@RequestMapping("/v1/{organizationId}/servers")
public class ServerController extends BaseController {

    @Autowired
    private ServerRepository serverRepository;
    
    @Autowired
    private ServerService serverService;

    @ApiOperation(value = "文件服务器列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Server>> list(@PathVariable("organizationId") Long tenantId,
                                             @Encrypt Server server,
                                             @ApiIgnore @SortDefault(value = Server.FIELD_SERVER_ID,
                                             direction = Sort.Direction.DESC) PageRequest pageRequest) {
        server.setTenantId(tenantId);
        Page<Server> list = serverService.findServers(server, pageRequest);
        return Results.success(list);
    }

    @ApiOperation(value = "文件服务器明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{serverId}")
    public ResponseEntity<Server> detail(@PathVariable("organizationId") Long tenantId,
                                         @PathVariable("serverId") @Encrypt Long serverId) {
        Server server = serverService.findServerByServerId(tenantId, serverId);
        return Results.success(server);
    }

    @ApiOperation(value = "创建文件服务器")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Server> create(@PathVariable("organizationId") Long tenantId,@RequestBody @Encrypt Server server) {
        validObject(server);
        server.setTenantId(tenantId);
        return Results.success(serverService.insertServer(server));
    }

    @ApiOperation(value = "修改文件服务器")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Server> update(@PathVariable("organizationId") Long tenantId,@RequestBody @Encrypt Server server) {
        SecurityTokenHelper.validToken(server);
        return Results.success(serverService.updateServer(server));
    }

    @ApiOperation(value = "删除文件服务器")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable("organizationId") Long tenantId,@RequestBody @Encrypt Server server) {
        SecurityTokenHelper.validToken(server);
        serverRepository.deleteByPrimaryKey(server);
        return Results.success();
    }
    
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询文件可以分配的服务器", notes = "分页查询")
    @GetMapping("/can-assign/{clusterId}")
    public ResponseEntity<?> fetchCanAssignFileServers(@PathVariable("organizationId") Long tenantId,
                                 @PathVariable @Encrypt Long clusterId,
                                 @Encrypt Server server,
                                 @ApiIgnore
                                 @SortDefault(value = Server.FIELD_SERVER_CODE, direction = Sort.Direction.DESC)
                                 PageRequest pageRequest) throws CommonException {
        return Results.success(serverService.fetchCanAssignFileServers(tenantId,clusterId,server, pageRequest));
    }
    
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据serverId查询带有cluster信息服务器信息", notes = "分页查询")
    @GetMapping("/clusters/server-id/{serverId}")
    public ResponseEntity<?> findFileServerClustersByServerId(@PathVariable("organizationId") Long tenantId,
                              @PathVariable("serverId") @Encrypt Long serverId,
                              @ApiIgnore @SortDefault(value = Server.FIELD_SERVER_CODE, 
                              direction = Sort.Direction.DESC) PageRequest pageRequest) throws CommonException {
        Server server = new Server();
        server.setTenantId(tenantId);
        server.setServerId(serverId);
        List<Server> result = serverService.selectAllWithClusterInfo(server, pageRequest);
        return Results.success(result);
    }
    

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据clusterId查询带有cluster信息服务器信息", notes = "分页查询")
    @GetMapping("/clusters/cluster-id/{clusterId}")
    public ResponseEntity<?> findFileServerClustersByClusterId(@PathVariable("organizationId") Long tenantId,
                                @PathVariable("clusterId") @Encrypt Long clusterId,
                                @ApiIgnore @SortDefault(value = Server.FIELD_SERVER_CODE,
                                direction = Sort.Direction.DESC) PageRequest pageRequest) throws CommonException {
        Server server = new Server();
        server.setTenantId(tenantId);
        server.setClusterId(clusterId);
        List<Server> result = serverService.selectAllWithClusterInfo(server, pageRequest);
        return Results.success(result);
    }
    
    @ApiOperation(value = "根据serverid批量查询")
    @Permission(level = ResourceLevel.ORGANIZATION,permissionWithin = true)
    @GetMapping("/server-ids")
    public ResponseEntity<List<Server>> listByServerIds(@PathVariable("organizationId") Long tenantId,
                                                        @RequestParam("serverIdList") List<Long> serverIdList) {
        return Results.success(serverRepository.selectByIds(StringUtils.join(serverIdList, ",")));
    }
    
    @ApiOperation(value = "根据clusterid批量查询")
    @Permission(level = ResourceLevel.ORGANIZATION,permissionWithin = true)
    @GetMapping("/cluster-ids")
    public ResponseEntity<List<Server>> listByClusterIds(@PathVariable("organizationId") Long tenantId,
                                                        @RequestParam("clustersIdList") List<Long> clustersIdList) {
        return Results.success(serverService.selectByClusterIdList(clustersIdList));
    }

}
