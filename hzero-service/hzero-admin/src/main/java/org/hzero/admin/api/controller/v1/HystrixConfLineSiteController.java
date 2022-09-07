package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.HystrixConfLineService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.HystrixConfLine;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Hystrix保护设置行明细 管理 API
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@Api(tags = SwaggerApiConfig.HYSTRIX_LINE_SITE)
@RestController("hystrixConfLineSiteController.v1")
@RequestMapping("/v1/hystrix-conf-lines")
public class HystrixConfLineSiteController extends BaseController {

    @Autowired
    private HystrixConfLineService hystrixConfLineService;

    @ApiOperation(value = "分页查询Hystrix配置明细行(参数名模糊查询)")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<HystrixConfLine>> list(HystrixConfLine hystrixConfLine, @ApiIgnore @SortDefault(value = HystrixConfLine.FIELD_CONF_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(hystrixConfLineService.pageAndSort(hystrixConfLine, pageRequest));
    }

    @ApiOperation(value = "Hystrix保护设置行明细明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{confLineId}")
    public ResponseEntity<HystrixConfLine> detail(@PathVariable Long confLineId) {
        return Results.success(hystrixConfLineService.selectByPrimaryKey(confLineId));
    }

    @ApiOperation(value = "批量删除Hystrix保护设置行明细")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@RequestBody List<HystrixConfLine> hystrixConfLineList) {
        hystrixConfLineService.batchDelete(hystrixConfLineList);
        return Results.success();
    }
}
