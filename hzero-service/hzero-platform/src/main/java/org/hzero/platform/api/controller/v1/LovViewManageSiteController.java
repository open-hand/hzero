package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.LovViewHeaderService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.entity.LovViewLine;
import org.hzero.platform.domain.repository.LovViewHeaderRepository;
import org.hzero.platform.domain.repository.LovViewLineRepository;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 值集视图管理API(平台级) v1
 *
 * @author gaokuo.dai@hand-china.com    2018年6月8日上午10:07:33
 */
@Api(tags = PlatformSwaggerApiConfig.LOV_VIEW_MANAGE_SITE)
@RestController("lovViewManageSiteController.v1")
@RequestMapping("/v1")
public class LovViewManageSiteController extends BaseController {

    @Autowired
    private LovViewHeaderService lovViewHeaderService;
    @Autowired
    private LovViewHeaderRepository lovViewHeaderRepository;
    @Autowired
    private LovViewLineRepository lovViewLineRepository;

    @ApiOperation("查询单个值集视图头")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/lov-view-headers/{viewHeaderId}")
    public ResponseEntity<LovViewHeader> queryLovViewHeader(
            @ApiParam(value = "视图头ID", required = true) @PathVariable @Encrypt Long viewHeaderId
    ) {
        return Results.success(this.lovViewHeaderRepository.selectLovViewHeaderByPrimaryKey(viewHeaderId, null));
    }

    @ApiOperation("根据值集视图头ID查询值集视图行")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/lov-view-headers/{viewHeaderId}/lines")
    public ResponseEntity<Page<LovViewLine>> pageAndSortLinesByViewHeaderId(
            @ApiParam(value = "视图头ID", required = true) @PathVariable @Encrypt Long viewHeaderId,
            @ApiIgnore @SortDefault(value = "viewHeaderId", direction = Sort.Direction.ASC) PageRequest pageRequest
    ) {
        return Results.success(this.lovViewLineRepository.pageAndSortLovViewLineByLovId(viewHeaderId, null, pageRequest));
    }

    @ApiOperation("条件查询值集视图头")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/lov-view-headers")
    @CustomPageRequest
    public ResponseEntity<Page<LovViewHeader>> pageLovViewHeaders(
            @Encrypt LovViewHeader lovViewHeader,
            @ApiIgnore @SortDefault(value = LovViewHeader.FIELD_VIEW_HEADER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest
    ) {
        return Results.success(this.lovViewHeaderService.pageLovViewHeaders(lovViewHeader, true, pageRequest));
    }

    @ApiOperation("插入值集视图头")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/lov-view-headers")
    public ResponseEntity<LovViewHeader> addLovViewHeaders(
            @RequestBody @Encrypt LovViewHeader lovViewHeader
    ) {
        this.validObject(lovViewHeader);
        if (lovViewHeader.getTenantId() == null) {
            lovViewHeader.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        return Results.created(this.lovViewHeaderService.insertSelective(lovViewHeader));
    }

    @ApiOperation("插入值集视图行")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/lov-view-lines")
    public ResponseEntity<LovViewLine> addLovViewLines(
            @RequestBody @Encrypt LovViewLine lovViewLine
    ) {
        this.validObject(lovViewLine);
        if (lovViewLine.getTenantId() == null) {
            lovViewLine.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        this.lovViewLineRepository.insertSelective(lovViewLine);
        return Results.created(lovViewLine);
    }

    @ApiOperation("更新值集视图头")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/lov-view-headers")
    public ResponseEntity<LovViewHeader> updateLovViewHeadersByPrimaryKey(
            @RequestBody @Encrypt LovViewHeader lovViewHeader
    ) {
        this.validObject(lovViewHeader);
        if (lovViewHeader.getTenantId() == null) {
            lovViewHeader.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        return Results.success(this.lovViewHeaderService.updateByPrimaryKey(lovViewHeader));
    }

    @ApiOperation("更新值集视图行")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/lov-view-lines")
    public ResponseEntity<LovViewLine> updateLovViewLinesByPrimaryKey(
            @RequestBody @Encrypt LovViewLine lovViewLine
    ) {
        this.validObject(lovViewLine);
        if (lovViewLine.getTenantId() == null) {
            lovViewLine.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        this.lovViewLineRepository.updateByPrimaryKey(lovViewLine);
        return Results.success(lovViewLine);
    }

    @ApiOperation("批量删除值集视图头")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/lov-view-headers")
    public ResponseEntity batchDeleteLovViewHeadersByPrimaryKey(
            @RequestBody @Encrypt List<LovViewHeader> lovViewHeaders
    ) {
        this.lovViewHeaderService.batchDeleteByPrimaryKey(lovViewHeaders);
        return Results.success();
    }

    @ApiOperation("删除值集视图头")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/lov-view-headers/delete")
    public ResponseEntity deleteLovViewHeader(
            @RequestBody @Encrypt LovViewHeader lovViewHeader
    ) {
        this.lovViewHeaderService.deleteLovViewHeader(lovViewHeader);
        return Results.success();
    }

    @ApiOperation("批量删除值集视图行")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/lov-view-lines")
    public ResponseEntity batchDeleteLovViewLinesByPrimaryKey(
            @RequestBody @Encrypt List<LovViewLine> lovViewLines
    ) {
        this.lovViewLineRepository.batchDeleteLovViewLinesByPrimaryKey(lovViewLines);
        return Results.success();
    }

    @ApiOperation("平台级-复制值集视图")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/lov-view/copy")
    public ResponseEntity copyLovView(
            @ApiParam(value = "复制值集视图时选择的租户Id", required = true) @RequestParam Long tenantId,
            @ApiParam(value = "lov视图编码", required = true) @RequestParam String viewCode,
            @ApiParam(value = "viewHeaderId", required = true) @RequestParam @Encrypt Long viewHeaderId
    ) {
        lovViewHeaderService.copyLovView(tenantId, viewCode, viewHeaderId, BaseConstants.Flag.YES);
        return Results.success();
    }
}
