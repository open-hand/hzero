package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.ServerClusterService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.ServerCluster;
import org.hzero.platform.domain.repository.ServerClusterRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 服务器集群设置表 管理 API
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Api(tags = PlatformSwaggerApiConfig.SERVER_CLUSTER)
@RestController("serverClusterController.v1")
@RequestMapping("/v1/{organizationId}/server-clusters")
public class ServerClusterController extends BaseController {

    @Autowired
    private ServerClusterRepository serverClusterRepository;
    
    @Autowired
    private ServerClusterService serverClusterService;

    @ApiOperation(value = "服务器集群设置表列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ServerCluster>> list(@PathVariable("organizationId") Long tenantId,
                                                    ServerCluster serverCluster, 
                                                    @ApiIgnore @SortDefault(value = ServerCluster.FIELD_CLUSTER_ID,
                                                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        serverCluster.setTenantId(tenantId);
        Page<ServerCluster> list = serverClusterService.serverClusterList(pageRequest, serverCluster);
        return Results.success(list);
    }

    @ApiOperation(value = "服务器集群设置表明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{clusterId}")
    public ResponseEntity<ServerCluster> detail(@PathVariable("organizationId") Long tenantId,@PathVariable("clusterId") @Encrypt Long clusterId) {
        ServerCluster serverCluster = serverClusterService.serverClusterById(tenantId,clusterId);
        return Results.success(serverCluster);
    }

    @ApiOperation(value = "创建服务器集群设置表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<ServerCluster> create(@PathVariable("organizationId") Long tenantId,@RequestBody ServerCluster serverCluster) {
        validObject(serverCluster);
        serverCluster.setTenantId(tenantId);
        serverClusterRepository.insertSelective(serverCluster);
        return Results.success(serverCluster);
    }

    @ApiOperation(value = "修改服务器集群设置表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<ServerCluster> update(@PathVariable("organizationId") Long tenantId,@RequestBody @Encrypt ServerCluster serverCluster) {
        serverCluster.setTenantId(tenantId);
        SecurityTokenHelper.validToken(serverCluster);
        serverClusterService.updateServerCluster(serverCluster);
        return Results.success(serverCluster);
    }

    @ApiOperation(value = "删除服务器集群设置表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable("organizationId") Long tenantId,@RequestBody @Encrypt ServerCluster serverCluster) {
        SecurityTokenHelper.validToken(serverCluster);
        serverClusterRepository.deleteByPrimaryKey(serverCluster);
        return Results.success();
    }

}
