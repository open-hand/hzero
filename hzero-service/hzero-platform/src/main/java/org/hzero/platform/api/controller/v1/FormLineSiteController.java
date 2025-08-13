package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.FormLineService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.FormLine;
import org.hzero.platform.domain.repository.FormLineRepository;
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
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 表单配置行 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
@Api(tags = PlatformSwaggerApiConfig.FORM_LINE_SITE)
@RestController("formLineSiteController.v1")
@RequestMapping("/v1/form-lines")
public class FormLineSiteController extends BaseController {

    private final FormLineRepository formLineRepository;
    private final FormLineService formLineService;

    @Autowired
    public FormLineSiteController(FormLineRepository formLineRepository, FormLineService formLineService) {
        this.formLineRepository = formLineRepository;
        this.formLineService = formLineService;
    }

    @ApiOperation(value = "表单配置行列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({@ApiImplicitParam(name = "formHeaderId", value = "表单头Id", paramType = "path", required = true)})
    @GetMapping("/{formHeaderId}")
    public ResponseEntity<Page<FormLine>> pageFormLines(@PathVariable("formHeaderId") @Encrypt Long formHeaderId,
                    @Encrypt FormLine formLine, @ApiIgnore @SortDefault(value = FormLine.FIELD_ORDER_SEQ,
                                    direction = Sort.Direction.ASC) PageRequest pageRequest) {
        formLine.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        formLine.setFormHeaderId(formHeaderId);
        return Results.success(formLineRepository.pageFormLines(pageRequest, formLine));
    }

    @ApiOperation(value = "表单配置行明细")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParams({@ApiImplicitParam(name = "formLineId", value = "表单行Id", paramType = "path", required = true)})
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/detail/{formLineId}")
    public ResponseEntity<FormLine> detail(@PathVariable @Encrypt Long formLineId) {
        FormLine formLine = formLineRepository.selectByPrimaryKey(formLineId);
        return Results.success(formLine);
    }

    @ApiOperation(value = "创建表单配置行")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<FormLine> create(@RequestBody @Encrypt FormLine formLine) {
        formLine.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        validObject(formLine);
        return Results.success(formLineService.createFormLine(formLine));
    }

    @ApiOperation(value = "修改表单配置行")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<FormLine> update(@RequestBody @Encrypt FormLine formLine) {
        formLine.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        SecurityTokenHelper.validToken(formLine);
        validObject(formLine);
        return Results.success(formLineService.updateFormLine(formLine));
    }

    @ApiOperation(value = "删除表单配置行")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody @Encrypt FormLine formLine) {
        SecurityTokenHelper.validToken(formLine);
        formLineService.deleteFormLine(formLine);
        return Results.success();
    }

    @ApiOperation(value = "根据表单头code查询启用状态的表单配置行列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParam(name = "formCode", value = "表单头编码", paramType = "query", required = true)
    @GetMapping("/header-code")
    public ResponseEntity<List<FormLine>> listFormLineByHeaderCode(@RequestParam String formCode) {
        return Results.success(formLineService.listFormLineByHeaderCode(formCode, BaseConstants.DEFAULT_TENANT_ID));
    }

    @ApiOperation(value = "根据表单头id查询启用状态的表单配置行列表")
    @Permission(level = ResourceLevel.SITE)
    @ApiImplicitParam(name = "formHeaderId", value = "表单头Id", paramType = "query", required = true)
    @GetMapping("/header-id")
    public ResponseEntity<List<FormLine>> listFormLineByHeaderId(@RequestParam @Encrypt Long formHeaderId) {
        return Results.success(formLineService.listFormLineByHeaderId(formHeaderId, BaseConstants.DEFAULT_TENANT_ID));
    }

}
