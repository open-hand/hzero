package org.hzero.boot.report.api.controller.v1;

import javax.servlet.http.HttpServletRequest;

import org.hzero.boot.report.app.ReportExecuteService;
import org.hzero.boot.report.configure.ReportClientApiConfig;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 异步报表请求
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 14:10
 */
@Api(tags = ReportClientApiConfig.REPORT_EXECUTE)
@RestController("reportExecuteController.v1")
@RequestMapping("/v1")
public class ReportExecuteController {

    private final ReportExecuteService reportExecuteService;

    @Autowired
    public ReportExecuteController(ReportExecuteService reportExecuteService) {
        this.reportExecuteService = reportExecuteService;
    }

    @ApiOperation(value = "报表执行")
    @Permission(permissionLogin = true)
    @GetMapping("/report/execute")
    public ResponseEntity<String> execute(HttpServletRequest request) {
        return Results.success(reportExecuteService.execute(request));
    }
}
