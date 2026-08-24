package org.hzero.admin.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.api.dto.TraceExport;
import org.hzero.admin.app.service.TraceAnalysisService;
import org.hzero.admin.app.service.TraceExportService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/23 11:18 上午
 */
@Api(tags = SwaggerApiConfig.TRACE)
@RestController("traceController.v1")
@RequestMapping("/v1/trace")
public class TraceController {

    @Autowired
    private TraceAnalysisService traceAnalysisService;

    @Autowired
    private TraceExportService traceExportService;

    @ApiOperation(value = "开启trace")
    @Permission(permissionLogin = true)
    @PostMapping("/start")
    public ResponseEntity<Long> start() {
        traceAnalysisService.start();
        return Results.success();
    }

    @ApiOperation(value = "结束trace并生成报告")
    @Permission(permissionLogin = true)
    @PostMapping("/end")
    public ResponseEntity<?> end() {
        return Results.success(traceAnalysisService.end());
    }

    @ApiOperation(value = "导出报告")
    @Permission(permissionLogin = true)
    @GetMapping("/export")
    @ExcelExport(TraceExport.class)
    public ResponseEntity<List<TraceExport>> export(@RequestParam("traceGroupId") String traceGroupId,
                                                          ExportParam exportParam,
                                                          HttpServletResponse response) {
        return Results.success(traceExportService.export(traceGroupId));
    }

    @ApiOperation(value = "查询是否正在trace")
    @Permission(permissionLogin = true)
    @GetMapping("/status")
    public ResponseEntity<Boolean> status() {
        return Results.success(traceAnalysisService.ifStarted());
    }
}
