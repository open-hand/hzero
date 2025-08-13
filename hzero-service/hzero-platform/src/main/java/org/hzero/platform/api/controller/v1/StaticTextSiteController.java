package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.StaticTextService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.StaticText;
import org.hzero.platform.domain.entity.StaticTextValue;
import org.hzero.platform.domain.repository.StaticTextRepository;
import org.hzero.platform.domain.repository.StaticTextValueRepository;
import org.hzero.platform.domain.vo.StaticTextVO;
import org.hzero.platform.domain.vo.StaticTextValueVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 平台静态信息 管理 API
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:19:42
 */
@Api(tags = {PlatformSwaggerApiConfig.STATIC_TEXT_SITE})
@RestController("staticTextSiteController.v1")
@RequestMapping("/v1/static-texts")
public class StaticTextSiteController extends BaseController {

    @Autowired
    private StaticTextService staticTextService;
    @Autowired
    private StaticTextRepository staticTextRepository;
    @Autowired
    private StaticTextValueRepository staticTextValueRepository;

    @ApiOperation(value = "分页查询静态文本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textCode", value = "文本编码", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page> paging(StaticTextVO searchDto,
                                       @ApiIgnore @SortDefault(value = StaticText.FIELD_TEXT_CODE) PageRequest pageRequest) {
        return Results.success(staticTextRepository.paging(searchDto, pageRequest));
    }

    @ApiOperation(value = "查询静态文本详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textId", value = "文本ID", paramType = "query"),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/details/{textId}")
    public ResponseEntity<StaticTextVO> detail(@PathVariable @Encrypt Long textId, String lang) {
        return Results.success(staticTextRepository.getTextDetails(textId, lang));
    }

    @ApiOperation(value = "根据编码查询静态文本基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textCode", value = "文本编码", paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @GetMapping("/by-code")
    public ResponseEntity<StaticTextVO> getTextBaseInfo(String textCode) {
        return Results.success(staticTextRepository.getTextHead(BaseConstants.DEFAULT_TENANT_ID, BaseConstants.DEFAULT_TENANT_ID, textCode));
    }

    @ApiOperation(value = "根据编码查询静态文本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textCode", value = "文本编码", paramType = "query"),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @GetMapping("/text/by-code")
    public ResponseEntity<StaticTextValueVO> getText(String textCode, String lang) {
        return Results.success(staticTextValueRepository.getTextValue(BaseConstants.DEFAULT_TENANT_ID,
                BaseConstants.DEFAULT_TENANT_ID, textCode, lang));
    }

    @ApiOperation(value = "根据编码查询静态文本信息，非public接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path"),
            @ApiImplicitParam(name = "companyId", value = "公司ID", paramType = "query"),
            @ApiImplicitParam(name = "textCode", value = "文本编码", paramType = "query"),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query")})
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/text-code")
    public ResponseEntity<StaticTextValueVO> getTextMsg(String textCode, String lang) {
        return Results.success(staticTextValueRepository.getTextValue(BaseConstants.DEFAULT_TENANT_ID,
                BaseConstants.DEFAULT_TENANT_ID, textCode, lang));
    }

    @ApiOperation(value = "根据编码查询静态文本信息(无,返回204)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textCode", value = "文本编码", paramType = "query"),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @GetMapping("/text/by-code/nullable")
    public ResponseEntity<StaticTextValueVO> getTextNullAble(String textCode, String lang) {
        StaticTextValueVO staticTextValueVO = staticTextValueRepository.getTextNullAble(BaseConstants.DEFAULT_TENANT_ID,
                BaseConstants.DEFAULT_TENANT_ID, textCode, lang);
        if (staticTextValueVO == null) {
            return Results.success();
        }
        return Results.success(staticTextValueVO);
    }

    @ApiOperation(value = "新增文本")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<StaticText> create(@RequestBody @Encrypt StaticText staticText) {
        staticText.setCompanyId(staticText.getCompanyId() == null ? BaseConstants.DEFAULT_TENANT_ID : staticText.getCompanyId());
        validObject(staticText);
        return Results.success(staticTextService.createText(staticText));
    }

    @ApiOperation(value = "修改文本")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<StaticText> update(@RequestBody @Encrypt StaticText staticText) {
        staticText.setCompanyId(staticText.getCompanyId() == null ? BaseConstants.DEFAULT_TENANT_ID : staticText.getCompanyId());
        SecurityTokenHelper.validToken(staticText);
        validObject(staticText);
        return Results.success(staticTextService.updateText(staticText));
    }

    @ApiOperation(value = "批量删除文本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textIds", value = "待删除的文本ID", paramType = "body")
    })
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@RequestBody @Encrypt List<StaticText> texts) {
        SecurityTokenHelper.validToken(texts);
        staticTextService.batchDelete(texts);
        return Results.success();
    }

    @ApiOperation("根据ID查询文本内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "textId", value = "文本ID", paramType = "path")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{textId}/text-value")
    public ResponseEntity<StaticTextValue> getTextValueById(@PathVariable @Encrypt Long textId) {
        return Results.success(staticTextService.getTextValueById(textId));
    }
}
