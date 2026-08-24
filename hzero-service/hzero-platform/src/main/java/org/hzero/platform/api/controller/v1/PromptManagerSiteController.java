package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.PromptService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Prompt;
import org.hzero.platform.domain.repository.PromptRepository;
import org.hzero.platform.domain.service.PromptDomainService;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 多语言描述 管理 API
 *
 * @author yunxiang.zhou01@hand-china.com 2018-06-21 16:02:18
 */
@Api(tags = PlatformSwaggerApiConfig.PROMPT_MANAGE_SITE)
@RestController("promptManagerSiteController.v1")
@RequestMapping("/v1/prompts")
public class PromptManagerSiteController extends BaseController {

    @Autowired
    private PromptRepository promptRepository;
    @Autowired
    private PromptService promptService;
    @Autowired
    private PromptDomainService promptDomainService;

    @ApiOperation(value = "多语言描述列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity list(Prompt prompt, @ApiIgnore @SortDefault(value = "promptId",
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(promptRepository.selectPrompt(pageRequest, prompt));
    }

    @ApiOperation(value = "新增或修改多语言描述")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity insertOrUpdate(@RequestBody @Encrypt List<Prompt> promptList) {
        validList(promptList);
        SecurityTokenHelper.validTokenIgnoreInsert(promptList);
        return Results.success(promptRepository.insertOrUpdate(promptList));
    }

    @ApiOperation(value = "删除多语言描述")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity remove(@RequestBody @Encrypt Prompt prompt) {
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        promptRepository.deletePrompt(prompt);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("多语言描述导入")
    @PostMapping("/prompt-import")
    public ResponseEntity promptImport(@RequestParam(name = "promptFile") MultipartFile promptFile) {
        promptService.promptImport(BaseConstants.DEFAULT_TENANT_ID, promptFile);
        return Results.success();
    }

    /**
     * 租户级查询多语言描述列表
     *
     * @param prompt         多语言查询条件
     * @param pageRequest    分页
     * @return 统一返回结果
     */
    @ApiOperation(value = "分页查询多语言描述信息列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/page-list")
    @CustomPageRequest
    public ResponseEntity pagePromptList(Prompt prompt, @ApiIgnore @SortDefault(value = "promptId",
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(promptRepository.selectPrompt(pageRequest, prompt));
    }

    @ApiOperation(value = "新增多语言描述信息")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/insert")
    public ResponseEntity insertPrompt(@RequestBody @Encrypt Prompt prompt) {
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        return Results.success(promptService.insertPromptDescription(prompt, prompt.getTenantId()));
    }

    @ApiOperation(value = "更新多语言描述信息")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("/update")
    public ResponseEntity updatePrompt(@RequestBody @Encrypt Prompt prompt) {
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        return Results.success(promptService.updatePromptDescription(prompt, prompt.getTenantId()));
    }

    @ApiOperation(value = "多语言描述明细信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "promptKey", value = "多语言KEY", paramType = "query", required = true),
            @ApiImplicitParam(name = "promptCode", value = "多语言CODE", paramType = "query", required = true),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query", required = true),
            @ApiImplicitParam(name = "tenantId", value = "多语言租户Id", paramType = "query", required = true)
    })
    public ResponseEntity getPromptDetails(@Encrypt Prompt prompt) {
        return Results.success(promptRepository.getPromptDetails(prompt));
    }

    @ApiOperation(value = "删除多语言描述信息")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/remove")
    public ResponseEntity removePrompt(@RequestBody @Encrypt Prompt prompt) {
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        promptService.deletePromptByOptions(prompt);
        return Results.success();
    }

    @ApiOperation(value = "刷新多语言缓存")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/refresh/cache")
    public ResponseEntity refreshCache() {
        promptDomainService.initCache();
        return Results.success();
    }
}
