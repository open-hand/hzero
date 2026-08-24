package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.ServerAssignService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.ServerAssign;
import org.hzero.platform.domain.repository.ServerAssignRepository;
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
 * 服务器的集群分配 管理 API
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Api(tags = PlatformSwaggerApiConfig.SERVER_ASSIGN)
@RestController("serverAssignController.v1")
@RequestMapping("/v1/{organizationId}/server-assigns")
public class ServerAssignController extends BaseController {

    @Autowired
    private ServerAssignRepository serverAssignRepository;
    @Autowired
    private ServerAssignService serverAssignService;

    @ApiOperation(value = "服务器的集群分配列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ServerAssign>> list(@PathVariable("organizationId") Long tenantId,
                                                   @Encrypt ServerAssign serverAssign,
                                                   @ApiIgnore @SortDefault(value = ServerAssign.FIELD_ASSIGN_ID,
                                                   direction = Sort.Direction.DESC) PageRequest pageRequest) {
        serverAssign.setTenantId(tenantId);
        Page<ServerAssign> list = serverAssignRepository.pageAndSort(pageRequest, serverAssign);
        return Results.success(list);
    }

    @ApiOperation(value = "创建服务器的集群分配")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<ServerAssign>> create(@PathVariable("organizationId") Long tenantId,@RequestBody @Encrypt List<ServerAssign> serverAssignList) {
        validList(serverAssignList);
        
        serverAssignService.batchInsertDTOFilterExist(tenantId,serverAssignList);
        return Results.success(serverAssignList);
    }

    @ApiOperation(value = "删除服务器的集群分配")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable("organizationId") Long tenantId,@RequestBody @Encrypt List<ServerAssign> serverAssignList) {
        serverAssignService.deleteAll(tenantId,serverAssignList);
        return Results.success();
    }

}
