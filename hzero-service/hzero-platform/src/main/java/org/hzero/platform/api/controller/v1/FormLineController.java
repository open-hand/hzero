package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
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
@Api(tags = PlatformSwaggerApiConfig.FORM_LINE)
@RestController("formLineController.v1")
@RequestMapping("/v1/{organizationId}/form-lines")
public class FormLineController extends BaseController {

    private final FormLineRepository formLineRepository;
    private final FormLineService formLineService;

    @Autowired
    public FormLineController(FormLineRepository formLineRepository, FormLineService formLineService) {
        this.formLineRepository = formLineRepository;
        this.formLineService = formLineService;
    }

    @ApiOperation(value = "表单配置行列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "formHeaderId", value = "表单头Id", paramType = "path", required = true)
    })
    @GetMapping("/{formHeaderId}")
    public ResponseEntity<Page<FormLine>> pageFormLines(
                    @PathVariable("organizationId") Long tenantId,
                    @PathVariable("formHeaderId") @Encrypt Long formHeaderId,
                    @Encrypt FormLine formLine,
                    @ApiIgnore @SortDefault(value = FormLine.FIELD_ORDER_SEQ, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        formLine.setTenantId(tenantId);
        formLine.setFormHeaderId(formHeaderId);
        return Results.success(formLineRepository.pageFormLines(pageRequest, formLine));
    }

    @ApiOperation(value = "表单配置行明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "formLineId", value = "表单行Id", paramType = "path", required = true)
    })
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/detail/{formLineId}")
    public ResponseEntity<FormLine> detail(@PathVariable @Encrypt Long formLineId) {
        FormLine formLine = formLineRepository.selectByPrimaryKey(formLineId);
        return Results.success(formLine);
    }

    @ApiOperation(value = "创建表单配置行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<FormLine> create(@PathVariable("organizationId") Long tenantId, @RequestBody @Encrypt FormLine formLine) {
        formLine.setTenantId(tenantId);
        validObject(formLine);
        return Results.success(formLineService.createFormLine(formLine));
    }

    @ApiOperation(value = "修改表单配置行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<FormLine> update(@PathVariable("organizationId") Long tenantId, @RequestBody @Encrypt FormLine formLine) {
        formLine.setTenantId(tenantId);
        SecurityTokenHelper.validToken(formLine);
        validObject(formLine);
        return Results.success(formLineService.updateFormLine(formLine));
    }

    @ApiOperation(value = "删除表单配置行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody @Encrypt FormLine formLine) {
        SecurityTokenHelper.validToken(formLine);
        formLineService.deleteFormLine(formLine);
        return Results.success();
    }

    @ApiOperation(value = "根据表单头code查询启用状态的表单配置行列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParam(name = "formCode", value = "表单头编码", paramType = "query")
    @GetMapping("/header-code")
    public ResponseEntity<List<FormLine>> listFormLineByHeaderCode(@RequestParam String formCode, @PathVariable("organizationId") Long tenantId) {
        return Results.success(formLineService.listFormLineByHeaderCode(formCode, tenantId));
    }

    @ApiOperation(value = "根据表单头id查询启用状态的表单配置行列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParam(name = "formHeaderId", value = "表单头Id", paramType = "query")
    @GetMapping("/header-id")
    public ResponseEntity<List<FormLine>> listFormLineByHeaderId(@RequestParam @Encrypt Long formHeaderId, @PathVariable("organizationId") Long tenantId) {
        return Results.success(formLineService.listFormLineByHeaderId(formHeaderId, tenantId));
    }

    @ApiOperation(value = "翻译值集/值集视图类型的字段内容")
    @Permission(permissionLogin = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "valueSet", value = "值集/值集视图编码", paramType = "path", required = true),
            @ApiImplicitParam(name = "params", value = "查询参数", paramType = "query", required = true)
    })
    @GetMapping("/translate-lov/{formLineId}")
    public ResponseEntity<List<LovValueDTO>> translateValueSet(@PathVariable("organizationId") Long organizationId,
            @PathVariable("formLineId") @Encrypt Long formLineId, @RequestParam(required = false) List<String> params) {
        return Results.success(formLineService.translateValueSet(organizationId, formLineId, params));
    }

}
