package org.hzero.platform.api.controller.v1;

import java.util.List;

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
 * 服务器定义
 *
 * @author minghui.qiu@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.SERVER_SITE)
@RestController("serverSiteController.v1")
@RequestMapping("/v1/servers")
public class ServerSiteController extends BaseController {

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private ServerService serverService;

    @ApiOperation(value = "文件服务器列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Server>> list(@Encrypt Server server,@ApiIgnore @SortDefault(value = Server.FIELD_SERVER_ID,
                                             direction = Sort.Direction.DESC) PageRequest pageRequest) {

        Page<Server> list = serverService.findServers(server, pageRequest);
        // serverRepository.pageAndSort(pageRequest, server);
        return Results.success(list);
    }

    @ApiOperation(value = "文件服务器明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{serverId}")
    public ResponseEntity<Server> detail(@PathVariable("serverId") @Encrypt Long serverId) {
        Server server = serverService.findServerByServerId(null, serverId);
        return Results.success(server);
    }

    @ApiOperation(value = "创建文件服务器")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Server> create(@RequestBody @Encrypt Server server) {
        validObject(server);
        return Results.success(serverService.insertServer(server));
    }

    @ApiOperation(value = "修改文件服务器")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Server> update(@RequestBody @Encrypt Server server) {
        SecurityTokenHelper.validToken(server);
        return Results.success(serverService.updateServer(server));
    }

    @ApiOperation(value = "删除文件服务器")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody @Encrypt Server server) {
        SecurityTokenHelper.validToken(server);
        serverService.deleteServer(server);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "查询文件可以分配的服务器", notes = "分页查询")
    @GetMapping("/can-assign/{clusterId}")
    public ResponseEntity<?> fetchCanAssignFileServers(@PathVariable @Encrypt Long clusterId, @Encrypt Server server,
                                    @ApiIgnore @SortDefault(value = Server.FIELD_SERVER_CODE, 
                                    direction = Sort.Direction.DESC) PageRequest pageRequest)throws CommonException {
        return Results.success(serverService.fetchCanAssignFileServers(null,clusterId,server, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据serverId查询带有cluster信息服务器信息", notes = "分页查询")
    @GetMapping("/clusters/server-id/{serverId}")
    public ResponseEntity<?> findFileServerClustersByServerId(@PathVariable("serverId") @Encrypt Long serverId,
                                @ApiIgnore @SortDefault(value = Server.FIELD_SERVER_CODE, 
                                direction = Sort.Direction.DESC) PageRequest pageRequest)throws CommonException {
        Server server = new Server();
        server.setServerId(serverId);
        List<Server> result = serverService.selectAllWithClusterInfo(server, pageRequest);
        return Results.success(result);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据clusterId查询带有cluster信息服务器信息", notes = "分页查询")
    @GetMapping("/clusters/cluster-id/{clusterId}")
    public ResponseEntity<?> findFileServerClustersByClusterId(@PathVariable("clusterId") @Encrypt Long clusterId,
                                        @ApiIgnore @SortDefault(value = Server.FIELD_SERVER_CODE, 
                                        direction = Sort.Direction.DESC) PageRequest pageRequest)throws CommonException {
        Server server = new Server();
        server.setClusterId(clusterId);
        List<Server> result = serverService.selectAllWithClusterInfo(server, pageRequest);
        return Results.success(result);
    }

}
