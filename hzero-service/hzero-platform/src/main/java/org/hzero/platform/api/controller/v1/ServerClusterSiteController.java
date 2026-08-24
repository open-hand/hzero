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
 * 服务器集群定义
 *
 * @author minghui.qiu@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.SERVER_CLUSTER_SITE)
@RestController("serverClusterSiteController.v1")
@RequestMapping("/v1/server-clusters")
public class ServerClusterSiteController extends BaseController {

    @Autowired
    private ServerClusterRepository serverClusterRepository;
    
    @Autowired
    private ServerClusterService serverClusterService;

    @ApiOperation(value = "服务器集群设置表列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ServerCluster>> list(ServerCluster serverCluster, 
                                                    @ApiIgnore @SortDefault(value = ServerCluster.FIELD_CLUSTER_ID,
                                                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<ServerCluster> list = serverClusterService.serverClusterList(pageRequest, serverCluster);
        //serverClusterRepository.pageAndSort(pageRequest, serverCluster);
        return Results.success(list);
    }

    @ApiOperation(value = "服务器集群设置表明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{clusterId}")
    public ResponseEntity<ServerCluster> detail(@PathVariable("clusterId") @Encrypt Long clusterId) {
        ServerCluster serverCluster = serverClusterService.serverClusterById(null,clusterId);
        return Results.success(serverCluster);
    }

    @ApiOperation(value = "创建服务器集群设置表")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<ServerCluster> create(@RequestBody ServerCluster serverCluster) {
        validObject(serverCluster);
        serverClusterRepository.insertSelective(serverCluster);
        return Results.success(serverCluster);
    }

    @ApiOperation(value = "修改服务器集群设置表")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<ServerCluster> update(@RequestBody @Encrypt ServerCluster serverCluster) {
        SecurityTokenHelper.validToken(serverCluster);
        serverClusterRepository.updateByPrimaryKeySelective(serverCluster);
        return Results.success(serverCluster);
    }

    @ApiOperation(value = "删除服务器集群设置表")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody @Encrypt ServerCluster serverCluster) {
        SecurityTokenHelper.validToken(serverCluster);
        serverClusterService.deleteServerCluster(serverCluster);
        return Results.success();
    }

}
