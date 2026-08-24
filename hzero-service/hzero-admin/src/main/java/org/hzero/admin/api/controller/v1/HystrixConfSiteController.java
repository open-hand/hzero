package org.hzero.admin.api.controller.v1;


import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.HystrixConfService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.HystrixConf;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Hystrix保护设置 管理 API
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@Api(tags = SwaggerApiConfig.HYSTRIX_SITE)
@RestController("hystrixConfSiteController.v1")
@RequestMapping("/v1/hystrix-confs")
public class HystrixConfSiteController extends BaseController {

    @Autowired
    private HystrixConfService hystrixConfService;

    @ApiOperation(value = "分页查询Hystrix配置")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<HystrixConf>> pageHystrixConf(HystrixConf hystrixConf,
                                                             @ApiIgnore @SortDefault(value = HystrixConf.FIELD_CONF_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(hystrixConfService.pageByCondition(pageRequest, hystrixConf));
    }

    @ApiOperation(value = "confId查询Hystrix配置")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{confId}")
    public ResponseEntity<HystrixConf> detail(@PathVariable Long confId) {
        return Results.success(hystrixConfService.selectByConfigId(confId));
    }

    @ApiOperation(value = "批量新增或更新Hystrix配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<HystrixConf>> insertOrUpdate(@RequestBody List<HystrixConf> hystrixConfs) {
        return Results.success(hystrixConfService.batchUpdateSelective(hystrixConfs));
    }

    @ApiOperation(value = "刷新Hystrix保护设置到配置中心")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/batch-refresh")
    public ResponseEntity<List<HystrixConf>> batchRefresh(@RequestBody List<HystrixConf> hystrixConfs) {
        return Results.success(hystrixConfService.refresh(hystrixConfs));
    }
}
