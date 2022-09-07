package org.hzero.iam.api.controller.v1;

import java.util.Collections;
import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.DocTypeAssignService;
import org.hzero.iam.app.service.DocTypeAuthDimService;
import org.hzero.iam.app.service.DocTypeService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.DocType;
import org.hzero.iam.domain.entity.DocTypeAssign;
import org.hzero.iam.domain.entity.DocTypeAuthDim;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 租户级单据权限类型
 *
 * @author min.wang01@hand-china.com 2018/08/27 14:22
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = SwaggerApiConfig.DOC_TYPE)
@RestController("docTypeController.v1")
@RequestMapping("/v1/{organizationId}/doc-types")
public class DocTypeController extends BaseController {
    private DocTypeService docTypeService;
    private DocTypeAssignService docTypeAssignService;
    private DocTypeAuthDimService docTypeAuthDimService;

    @Autowired
    public DocTypeController(DocTypeService docTypeService,
                             DocTypeAssignService docTypeAssignService,
                             DocTypeAuthDimService docTypeAuthDimService) {
        this.docTypeService = docTypeService;
        this.docTypeAssignService = docTypeAssignService;
        this.docTypeAuthDimService = docTypeAuthDimService;
    }

    @ApiOperation("查询单据类型定义列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    public ResponseEntity<Page<DocType>> queryDocTypeByTenant(@ApiParam(value = "租户id", required = true) @PathVariable long organizationId,
                                                              @ApiParam("单据类型编码") @RequestParam(required = false) String docTypeCode,
                                                              @ApiParam("单据类型名称") @RequestParam(required = false) String docTypeName,
                                                              @ApiIgnore @SortDefault({DocType.FIELD_ORDER_SEQ, DocType.FIELD_DOC_TYPE_ID}) PageRequest pageRequest) {
        return Results.success(docTypeService.pageDocType(organizationId, docTypeCode, docTypeName, pageRequest));
    }

    @ApiOperation("单据类型定义明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{docTypeId}")
    public ResponseEntity<DocType> detailDocType(@ApiParam(value = "租户id", required = true) @PathVariable long organizationId,
                                                 @Encrypt @ApiParam("单据权限id") @PathVariable long docTypeId,
                                                 @ApiParam("包含单据权限分配") @RequestParam(defaultValue = "true") boolean includeAssign) {
        return Results.success(docTypeService.queryDocType(organizationId, docTypeId, includeAssign));
    }

    @ApiOperation("分页查询当前单据类型分配")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{docTypeId}/assigns")
    @CustomPageRequest
    public ResponseEntity<Page<DocTypeAssign>> pageAssign(@ApiParam(value = "租户id", required = true) @PathVariable long organizationId,
                                                          @Encrypt @ApiParam("单据权限id") @PathVariable long docTypeId,
                                                          @ApiIgnore @SortDefault(DocType.FIELD_ORDER_SEQ) PageRequest pageRequest) {
        return Results.success(docTypeAssignService.pageAssign(organizationId, docTypeId, pageRequest));
    }

    @ApiOperation(value = "查询单据类型下的权限维度")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{docTypeId}/auth-dim")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<DocTypeAuthDim>> listAuthDim(@ApiParam(value = "租户id", required = true) @PathVariable long organizationId,
                                                            @Encrypt @ApiParam(value = "单据权限id", required = true) @PathVariable long docTypeId) {
        return Results.success(docTypeAuthDimService.listAuthDim(organizationId, docTypeId));
    }

    @ApiOperation(value = "创建单据类型定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @PostMapping
    public ResponseEntity<DocType> createDocType(@ApiParam(value = "租户id", required = true) @PathVariable Long organizationId,
                                                 @ApiParam("单据类型数据") @RequestBody DocType docType) {
        validObject(docType.setTenantId(organizationId)
                .setLevelCode(Constants.DocType.DOC_TENANT_LEVEL)
                .setDocTypeAssigns(Collections.singletonList(new DocTypeAssign().setAssignValueId(organizationId))));
        Assert.isTrue(UniqueHelper.valid(docType), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        return Results.success(docTypeService.createDocType(docType));
    }

    @ApiOperation(value = "保存单据类型下的权限维度")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{docTypeId}/auth-dim/save")
    public ResponseEntity<List<DocTypeAuthDim>> saveAuthDim(@ApiParam(value = "租户id", required = true) @PathVariable long organizationId,
                                                            @Encrypt @ApiParam(value = "单据权限id", required = true) @PathVariable long docTypeId,
                                                            @RequestBody List<DocTypeAuthDim> docTypeAuthDims) {
        SecurityTokenHelper.validTokenIgnoreInsert(docTypeAuthDims);
        validList(docTypeAuthDims);
        docTypeAuthDims = docTypeAuthDimService.saveAuthDim(organizationId, docTypeId, docTypeAuthDims);
        docTypeService.generateShieldRule(organizationId, Collections.singletonList(docTypeId));
        return Results.success(docTypeAuthDims);
    }

    @ApiOperation(value = "修改单据类型定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @PutMapping
    public ResponseEntity<DocType> updateDocType(@ApiParam(value = "租户id", required = true) @PathVariable Long organizationId,
                                                 @ApiParam("单据类型数据") @Encrypt @RequestBody DocType docType) {
        SecurityTokenHelper.validTokenIgnoreInsert(docType);
        validObject(docType.setTenantId(organizationId).setLevelCode(Constants.DocType.DOC_TENANT_LEVEL));
        return Results.success(docTypeService.updateDocType(docType));
    }

    @ApiOperation(value = "生成数据屏蔽规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/generate-shield-rule")
    public ResponseEntity generateShieldRule(@ApiParam(value = "租户id", required = true) @PathVariable Long organizationId,
                                             @Encrypt @RequestBody List<Long> docTypeIds) {
        docTypeService.generateShieldRule(organizationId, docTypeIds);
        return Results.success();
    }
}
