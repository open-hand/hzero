package org.hzero.iam.api.controller.v1;

import java.util.List;
import java.util.Set;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.LabelRelService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.LabelRel;
import org.hzero.iam.domain.repository.LabelRelRepository;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 标签关系表 管理 API
 *
 * @author bo.he02@hand-china.com 2020-04-27 10:23:19
 */
@Api(tags = SwaggerApiConfig.LABEL_RELATION_SITE)
@RestController("labelRelSiteController.v1")
@RequestMapping("/v1/label-rels")
public class LabelRelSiteController extends BaseController {

    @Autowired
    private LabelRelRepository labelRelRepository;

    @Autowired
    private LabelRelService labelRelService;

    @ApiOperation(value = "标签关系表列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<LabelRel>> list(LabelRel labelRel, @ApiIgnore @SortDefault(value = LabelRel.FIELD_LABEL_REL_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<LabelRel> list = labelRelRepository.pageAndSort(pageRequest, labelRel);
        return Results.success(list);
    }

    @ApiOperation(value = "标签关系表明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{labelRelId}")
    public ResponseEntity<LabelRel> detail(@Encrypt @PathVariable Long labelRelId) {
        LabelRel labelRel = labelRelRepository.selectByPrimaryKey(labelRelId);
        return Results.success(labelRel);
    }

    @ApiOperation(value = "根据数据类型和数据ID查询数据相关的标签关系详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "数据类型", required = true, paramType = "path"),
            @ApiImplicitParam(name = "dataId", value = "数据ID", required = true, paramType = "path")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{dataType}/{dataId}")
    public ResponseEntity<List<LabelRel>> listDataLabelRel(
            @PathVariable("dataType") String dataType,
            @Encrypt @PathVariable("dataId") Long dataId) {
        return Results.success(this.labelRelService.selectViewLabelsByDataTypeAndDataId(dataType, dataId));
    }

    @ApiOperation(value = "分配标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "数据类型", required = true, paramType = "path"),
            @ApiImplicitParam(name = "dataId", value = "数据ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "assignType", value = "分配类型：A-自动分配 M-手动分配，默认为手动分配", required = true, paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{dataType}/{dataId}")
    public ResponseEntity<?> assignLabels(
            @PathVariable("dataType") String dataType,
            @Encrypt @PathVariable("dataId") Long dataId,
            @RequestParam(name = "assignType", required = false) String assignType,
            @Encrypt @RequestBody Set<Long> labelIds) {
        this.labelRelService.addLabels(dataType, dataId, assignType, labelIds);
        return Results.success();
    }

    @ApiOperation(value = "取消分配标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataType", value = "数据类型", required = true, paramType = "path"),
            @ApiImplicitParam(name = "dataId", value = "数据ID", required = true, paramType = "path")
    })
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{dataType}/{dataId}")
    public ResponseEntity<?> removeLabels(
            @PathVariable("dataType") String dataType,
            @Encrypt @PathVariable("dataId") Long dataId,
            @Encrypt @RequestBody Set<Long> labelIds) {
        this.labelRelService.removeLabels(dataType, dataId, labelIds);
        return Results.success();
    }
}
