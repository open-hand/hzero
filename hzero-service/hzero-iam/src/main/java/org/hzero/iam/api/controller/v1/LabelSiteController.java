package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.LabelService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Label;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2020-02-25 14:22:16
 */
@Api(tags = SwaggerApiConfig.LABEL_SITE)
@RestController("labelSiteController.v1")
@RequestMapping("/v1/labels")
public class LabelSiteController extends BaseController {

    private final LabelService labelService;

    @Autowired
    public LabelSiteController(LabelService labelService) {
        this.labelService = labelService;
    }

    @ApiOperation(value = "分页获取标签列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<Label>> pageLabelList(Label label, @ApiIgnore @SortDefault(value = Label.FIELD_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(labelService.pageLabelList(pageRequest, label));
    }

    @ApiOperation(value = "获取标签明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{id}")
    public ResponseEntity<Label> getLabelDetail(@Encrypt@PathVariable Long id) {
        return Results.success(labelService.getLabelDetail(id));
    }

    @ApiOperation(value = "创建标签")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Label> createLabel(@RequestBody Label label) {
        return Results.success(labelService.createLabel(label));
    }

    @ApiOperation(value = "修改标签")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Label> updateLabel(@RequestBody Label label) {
        return Results.success(labelService.updateLabel(label));
    }

    @ApiOperation(value = "根据标签类型获取标签列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/by-type")
    public ResponseEntity<List<Label>> getLabelListByType(@RequestParam("type") String type) {
        return Results.success(labelService.getLabelListByType(type));
    }
}
