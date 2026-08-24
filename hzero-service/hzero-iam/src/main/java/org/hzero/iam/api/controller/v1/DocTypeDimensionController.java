package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.DocTypeDimensionService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.repository.DocTypeDimensionRepository;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
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
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 单据类型维度 管理 API
 *
 * @author zifeng.ding@hand-china.com 2019-09-18 11:48:01
 */
@Api(tags = SwaggerApiConfig.DOC_TYPE_DIMENSION)
@RestController("docTypeDimensionController.v1")
@RequestMapping("/v1/{organizationId}/doc-type/dimensions")
public class DocTypeDimensionController extends BaseController {

    private final DocTypeDimensionRepository docTypeDimensionRepository;

    @Autowired
    private DocTypeDimensionService dimensionService;

    public DocTypeDimensionController(DocTypeDimensionRepository docTypeDimensionRepository) {
        this.docTypeDimensionRepository = docTypeDimensionRepository;
    }

    @ApiOperation(value = "单据类型维度列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = {"body"})
    public ResponseEntity<Page<DocTypeDimension>> list(@ApiParam(value = "租户id", required = true) @PathVariable Long organizationId,
                                                       @ApiParam("维度编码") @RequestParam(required = false) String dimensionCode,
                                                       @ApiParam("维度名称") @RequestParam(required = false) String dimensionName,
                                                       @ApiParam("维度类型") @RequestParam(required = false) String dimensionType,
                                                       @ApiParam("值来源类型") @RequestParam(required = false) String valueSourceType,
                                                       @ApiParam("是否启用") @RequestParam(required = false) Integer enabledFlag,
                                                       @ApiIgnore @SortDefault(
                                                               value = {DocTypeDimension.FIELD_ORDER_SEQ, DocTypeDimension.FIELD_DIMENSION_ID},
                                                               direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<DocTypeDimension> list = docTypeDimensionRepository.pageAndSortDocTypeDimension(organizationId,
                dimensionCode, dimensionName, pageRequest, dimensionType, valueSourceType, enabledFlag);
        return Results.success(list);
    }

    @ApiOperation(value = "单据维度业务范围列表")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/biz")
    public ResponseEntity<List<DocTypeDimension>> listBiz(@ApiParam(value = "租户id", required = true) @PathVariable Long organizationId) {
        return Results.success(docTypeDimensionRepository.listBizDocTypeDimension(organizationId));
    }


    @ApiOperation(value = "单据类型维度明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dimensionId}")
    @ProcessLovValue(targetField = {"body"})
    public ResponseEntity<DocTypeDimension> detail(@Encrypt @ApiParam(value = "单据类型维度id", required = true) @PathVariable Long dimensionId) {
        DocTypeDimension docTypeDimension = docTypeDimensionRepository.selectDocTypeDimensionById(dimensionId);
        return Results.success(docTypeDimension);
    }

    @ApiOperation(value = "创建单据类型维度")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DocTypeDimension> create(@ApiParam(value = "租户id", required = true) @PathVariable Long organizationId,
                                                   @ApiParam(value = "单据类型维度数据", required = true) @RequestBody DocTypeDimension docTypeDimension) {
        docTypeDimension.setTenantId(organizationId);
        validObject(docTypeDimension);
        if (Constants.AUTHORITY_SCOPE_CODE.BIZ.equals(docTypeDimension.getDimensionType())) {
            validObject(docTypeDimension, DocTypeDimension.Biz.class);
        }
        return Results.success(dimensionService.createDocTypeDimension(docTypeDimension));
    }

    @ApiOperation(value = "修改单据类型维度")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DocTypeDimension> update(@ApiParam(value = "租户id", required = true) @PathVariable long organizationId,
                                                   @ApiParam(value = "单据类型维度数据", required = true) @RequestBody DocTypeDimension docTypeDimension) {
        validObject(docTypeDimension);
        if (Constants.AUTHORITY_SCOPE_CODE.BIZ.equals(docTypeDimension.getDimensionType())) {
            validObject(docTypeDimension, DocTypeDimension.Biz.class);
        }
        SecurityTokenHelper.validToken(docTypeDimension);
        return Results.success(dimensionService.updateDocTypeDimension(docTypeDimension));
    }


}
