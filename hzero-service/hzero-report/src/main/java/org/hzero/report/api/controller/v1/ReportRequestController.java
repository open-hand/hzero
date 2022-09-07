package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.ReportRequest;
import org.hzero.report.domain.repository.ReportRequestRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 报表请求 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2019-01-25 14:21:02
 */
@Api(tags = ReportSwaggerApiConfig.REPORT_REQUEST)
@RestController("reportRequestController.v1")
@RequestMapping("/v1/{organizationId}/report-requests")
public class ReportRequestController extends BaseController {

    private final ReportRequestRepository reportRequestRepository;

    @Autowired
    public ReportRequestController(ReportRequestRepository reportRequestRepository) {
        this.reportRequestRepository = reportRequestRepository;
    }

    @ApiOperation(value = "报表请求列表")
    @CustomPageRequest
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    public ResponseEntity<Page<ReportRequest>> list(@PathVariable Long organizationId, ReportRequest reportRequest,
                                                    @ApiIgnore @SortDefault(value = ReportRequest.FIELD_REQUEST_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        reportRequest.setTenantId(organizationId);
        return Results.success(reportRequestRepository.selectReportRequests(pageRequest, reportRequest));
    }

    @ApiOperation(value = "查看当前用户的报表请求")
    @CustomPageRequest
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping(value = {"/user"})
    public ResponseEntity<Page<ReportRequest>> reportRequest(@PathVariable Long organizationId, ReportRequest reportRequest,
                                                             @ApiIgnore @SortDefault(value = ReportRequest.FIELD_REQUEST_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        reportRequest.setTenantId(organizationId);
        reportRequest.setCreatedBy(userId);
        return Results.success(reportRequestRepository.selectReportRequests(pageRequest, reportRequest));
    }

    @ApiOperation(value = "报表请求明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{requestId}")
    public ResponseEntity<ReportRequest> detail(@Encrypt @PathVariable Long requestId) {
        return Results.success(reportRequestRepository.selectReportRequest(requestId));
    }

}
