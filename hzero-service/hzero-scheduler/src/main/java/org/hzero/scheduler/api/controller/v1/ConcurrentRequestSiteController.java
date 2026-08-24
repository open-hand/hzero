package org.hzero.scheduler.api.controller.v1;

import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.scheduler.api.dto.RequestQueryDTO;
import org.hzero.scheduler.app.service.ConcurrentRequestService;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.ConcurrentRequest;
import org.hzero.scheduler.domain.repository.ConcurrentRequestRepository;
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
 * 并发请求 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-13 14:30:24
 */
@Api(tags = SchedulerSwaggerApiConfig.CONCURRENT_REQUEST_SITE)
@RestController("concurrentRequestSiteController.v1")
@RequestMapping("/v1/concurrent-requests")
public class ConcurrentRequestSiteController extends BaseController {

    private final ConcurrentRequestService requestService;
    private final ConcurrentRequestRepository requestRepository;

    @Autowired
    public ConcurrentRequestSiteController(ConcurrentRequestService requestService,
                                           ConcurrentRequestRepository requestRepository) {
        this.requestService = requestService;
        this.requestRepository = requestRepository;
    }

    @ApiOperation(value = "并发请求列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "jobId", value = "任务ID", paramType = "query"),
            @ApiImplicitParam(name = "concCode", value = "请求编码", paramType = "query"),
            @ApiImplicitParam(name = "concName", value = "请求名称", paramType = "query"),
            @ApiImplicitParam(name = "cycleFlag", value = "周期性", paramType = "query"),
            @ApiImplicitParam(name = "jobStatus", value = "任务状态", paramType = "query"),
            @ApiImplicitParam(name = "clientResult", value = "执行结果", paramType = "query")
    })
    public ResponseEntity<Page<ConcurrentRequest>> pageRequest(@Encrypt RequestQueryDTO requestQueryDTO,
                                                               @ApiIgnore @SortDefault(value = ConcurrentRequest.FIELD_REQUEST_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(requestService.pageRequest(requestQueryDTO, pageRequest));
    }

    @ApiOperation(value = "并发请求明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{requestId}")
    public ResponseEntity<ConcurrentRequest> detailRequest(@PathVariable @ApiParam(value = "请求Id") @Encrypt Long requestId) {
        return Results.success(requestRepository.selectById(null, requestId));
    }

    @ApiOperation(value = "创建并发请求")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<ConcurrentRequest> createRequest(@RequestBody @Encrypt ConcurrentRequest concurrentRequest) {
        validObject(concurrentRequest, ConcurrentRequest.Validate.class);
        concurrentRequest.validate();
        return Results.success(requestService.createRequest(concurrentRequest));
    }
}
