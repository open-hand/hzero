package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
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

@Api(tags = PlatformSwaggerApiConfig.SERVER_ASSIGN_SITE)
@RestController("serverAssignSiteController.v1")
@RequestMapping("/v1/server-assigns")
public class ServerAssignSiteController extends BaseController {

    @Autowired
    private ServerAssignRepository serverAssignRepository;
    @Autowired
    private ServerAssignService serverAssignService;

    @ApiOperation(value = "服务器的集群分配列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ServerAssign>> list(@Encrypt ServerAssign serverAssign,
                                                   @ApiIgnore @SortDefault(value = ServerAssign.FIELD_ASSIGN_ID,
                                                   direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<ServerAssign> list = serverAssignRepository.pageAndSort(pageRequest, serverAssign);
        return Results.success(list);
    }

//    @ApiOperation(value = "服务器的集群分配明细")
//    @Permission(level = ResourceLevel.SITE)
//    @GetMapping("/{assignId}")
//    public ResponseEntity<ServerAssign> detail(@PathVariable Long assignId) {
//        ServerAssign serverAssign = serverAssignRepository.selectByPrimaryKey(assignId);
//        return Results.success(serverAssign);
//    }

    @ApiOperation(value = "创建服务器的集群分配")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<ServerAssign>> create(@RequestBody @Encrypt List<ServerAssign> serverAssignList) {
        validList(serverAssignList);
        serverAssignService.batchInsertDTOFilterExist(null,serverAssignList);
        //serverAssignRepository.insertSelective(serverAssign);
        return Results.success(serverAssignList);
    }

    @ApiOperation(value = "修改服务器的集群分配")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<ServerAssign> update(@RequestBody @Encrypt ServerAssign serverAssign) {
        SecurityTokenHelper.validToken(serverAssign);
        serverAssignRepository.updateByPrimaryKeySelective(serverAssign);
        return Results.success(serverAssign);
    }

    @ApiOperation(value = "删除服务器的集群分配")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody @Encrypt List<ServerAssign> serverAssignList) {
        //SecurityTokenHelper.validToken(serverAssignList);
        //serverAssignRepository.deleteByPrimaryKey(serverAssign);
        serverAssignService.deleteAll(null,serverAssignList);
        return Results.success();
    }

}
