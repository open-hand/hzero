package org.hzero.scheduler.api.controller.v1;

import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.app.service.ConcurrentService;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.Concurrent;
import org.hzero.scheduler.domain.repository.ConcurrentRepository;
import org.hzero.scheduler.infra.constant.HsdrConstant;
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

/**
 * 并发程序 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-11 10:20:47
 */
@Api(tags = SchedulerSwaggerApiConfig.CONCURRENT_SITE)
@RestController("concurrentSiteController.v1")
@RequestMapping("/v1/concurrents")
public class ConcurrentSiteController extends BaseController {

    private final ConcurrentService concurrentService;
    private final ConcurrentRepository concurrentRepository;

    @Autowired
    public ConcurrentSiteController(ConcurrentService concurrentService,
                                    ConcurrentRepository concurrentRepository) {
        this.concurrentService = concurrentService;
        this.concurrentRepository = concurrentRepository;
    }

    @ApiOperation(value = "并发程序列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query"),
            @ApiImplicitParam(name = "concCode", value = "程序代码", paramType = "query"),
            @ApiImplicitParam(name = "concName", value = "程序名称", paramType = "query"),
            @ApiImplicitParam(name = "concDescription", value = "程序描述", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "是否启用", paramType = "query")
    })
    public ResponseEntity<Page<Concurrent>> pageConcurrent(Long tenantId, String concCode, String concName, String concDescription, Integer enabledFlag,
                                                           @ApiIgnore @SortDefault(value = Concurrent.FIELD_CONCURRENT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(concurrentRepository.pageConcurrent(tenantId, concCode, concName, concDescription, enabledFlag, pageRequest));
    }

    @ApiOperation(value = "并发程序明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{concurrentId}")
    @ProcessLovValue(targetField = HsdrConstant.BODY_PARAM_LIST)
    public ResponseEntity<Concurrent> detailConcurrent(@PathVariable @ApiParam(value = "并发程序ID", required = true) @Encrypt Long concurrentId) {
        return Results.success(concurrentRepository.detailConcurrent(concurrentId, null));
    }

    @ApiOperation(value = "创建并发程序")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Concurrent> createConcurrent(@RequestBody @Encrypt Concurrent concurrent) {
        validObject(concurrent);
        return Results.success(concurrentService.createConcurrent(concurrent));
    }

    @ApiOperation(value = "修改并发程序")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Concurrent> updateConcurrent(@RequestBody @Encrypt Concurrent concurrent) {
        SecurityTokenHelper.validTokenIgnoreInsert(concurrent);
        validObject(concurrent, Concurrent.Validate.class);
        return Results.success(concurrentService.updateConcurrent(concurrent));
    }

    @ApiOperation(value = "用户可选并发程序列表")
    @Permission(permissionLogin = true)
    @GetMapping("/usable")
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "concCode", value = "并发程序代码", paramType = "query"),
            @ApiImplicitParam(name = "concName", value = "并发程序名称", paramType = "query")
    })
    public ResponseEntity<Page<Concurrent>> pageConcurrentByTenantId(@RequestParam Long tenantId, String concCode, String concName,
                                                                     @ApiIgnore @SortDefault(value = Concurrent.FIELD_CONCURRENT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<Concurrent> list = concurrentService.pageConcurrentByTenantId(tenantId, concCode, concName, pageRequest, true);
        return Results.success(list);
    }
}
