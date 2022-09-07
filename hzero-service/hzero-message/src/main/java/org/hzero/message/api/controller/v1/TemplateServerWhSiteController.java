package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.TemplateServerWhService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.TemplateServerWh;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

/**
 * 消息发送配置webhook 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2020-05-15 10:29:57
 */
@Api(tags = MessageSwaggerApiConfig.TEMPLATE_SERVER_WH_SITE)
@RestController("templateServerWhSiteController.v1")
@RequestMapping("/v1/template-server-whs/{tempServerLineId}")
public class TemplateServerWhSiteController extends BaseController {

    @Autowired
    private TemplateServerWhService templateServerWhService;

    @ApiOperation(value = "消息发送配置webhook列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<TemplateServerWh>> pageTemplateServerWh(
            @Encrypt @PathVariable("tempServerLineId") Long tempServerLineId,
            @ApiIgnore @SortDefault(value = TemplateServerWh.FIELD_TEMP_SERVER_WH_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(templateServerWhService.pageTemplateServerWh(tempServerLineId, pageRequest));
    }

    @ApiOperation(value = "批量新增消息发送配置webhook")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<TemplateServerWh>> create(@Encrypt @PathVariable("tempServerLineId") Long tempServerLineId,
                                                         @RequestBody List<TemplateServerWh> templateServerWhs) {
        return Results.success(
                templateServerWhService.batchCreateTemplateServerWh(tempServerLineId, templateServerWhs));
    }

    @ApiOperation(value = "批量删除消息发送配置webhook")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@Encrypt @RequestBody List<TemplateServerWh> templateServerWhs) {
        templateServerWhService.batchDeleteTemplateServerWh(templateServerWhs);
        return Results.success();
    }

}
