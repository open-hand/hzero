package org.hzero.admin.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.api.dto.ApiAccessStatisticsDTO;
import org.hzero.admin.api.dto.ServiceAccessStatisticsDTO;
import org.hzero.admin.app.service.AccessStatisticsService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

/**
 * 访问统计 管理API
 *
 * @author bergturing on 2020-5-7.
 */
@Api(tags = {SwaggerApiConfig.ACCESS_STATISTICS_SITE})
@RestController
@RequestMapping("/v1/statistics/access")
public class AccessStatisticsSiteController {
    /**
     * 访问统计服务对象
     */
    private final AccessStatisticsService accessStatisticsService;

    @Autowired
    public AccessStatisticsSiteController(AccessStatisticsService accessStatisticsService) {
        this.accessStatisticsService = accessStatisticsService;
    }

    @ApiOperation(value = "各在线服务API总数")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/api/count")
    public ResponseEntity<Map<String, Object>> queryInstanceApiCount() {
        // 查询数据，并返回结果
        return Results.success(this.accessStatisticsService.queryInstanceApiCount());
    }

    @ApiOperation(value = "单个API访问总次数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate", value = "开始日期(yyyy-MM-dd)", paramType = "query", dataType = "LocalDate"),
            @ApiImplicitParam(name = "endDate", value = "结束日期(yyyy-MM-dd)", paramType = "query", dataType = "LocalDate"),
            @ApiImplicitParam(name = "service", value = "服务名称", paramType = "query", dataType = "String")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/api-invoke/count")
    public ResponseEntity<ApiAccessStatisticsDTO> queryApiInvokeCount(@RequestParam("beginDate") LocalDate beginDate,
                                                                      @RequestParam("endDate") LocalDate endDate,
                                                                      @RequestParam("service") String service) {
        // 查询数据，并返回结果
        return Results.success(this.accessStatisticsService.queryApiInvokeCount(beginDate, endDate, service));
    }

    @ApiOperation(value = "各服务API访问总次数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate", value = "开始日期(yyyy-MM-dd)", paramType = "query", dataType = "LocalDate"),
            @ApiImplicitParam(name = "endDate", value = "结束日期(yyyy-MM-dd)", paramType = "query", dataType = "LocalDate")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/service-invoke/count")
    public ResponseEntity<ServiceAccessStatisticsDTO> queryServiceInvokeCount(@RequestParam("beginDate") LocalDate beginDate,
                                                                              @RequestParam("endDate") LocalDate endDate) {
        // 查询数据，并返回结果
        return Results.success(this.accessStatisticsService.queryServiceInvokeCount(beginDate, endDate));
    }
}
