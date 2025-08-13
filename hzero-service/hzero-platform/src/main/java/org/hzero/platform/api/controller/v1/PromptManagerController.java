package org.hzero.platform.api.controller.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.PromptDTO;
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

import io.choerodon.core.domain.Page;
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
 * <p>
 * 多语言标签租户级管理接口
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/27 10:28
 */
@Api(tags = PlatformSwaggerApiConfig.PROMPT_MANAGE)
@RestController("promptManagerController.v1")
@RequestMapping("/v1")
public class PromptManagerController extends BaseController {

    @Autowired
    private PromptRepository promptRepository;
    @Autowired
    private PromptService promptService;
    @Autowired
    private PromptDomainService promptDomainService;

    @ApiOperation(value = "租户级查询多语言描述列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/prompts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @CustomPageRequest
    public ResponseEntity list(@PathVariable Long organizationId, Prompt prompt,
                               @ApiIgnore @SortDefault(value = "promptId",
                                       direction = Sort.Direction.DESC) PageRequest pageRequest) {
        prompt.setTenantId(organizationId);
        // 租户级多语言标签查询
        return Results.success(promptRepository.selectPromptTenant(pageRequest, prompt));
    }

    @ApiOperation(value = "租户级新增或修改多语言描述")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/prompts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    public ResponseEntity insertOrUpdate(@PathVariable Long organizationId, @RequestBody @Encrypt List<Prompt> promptList) {
        validList(promptList);
        SecurityTokenHelper.validTokenIgnoreInsert(promptList);
        List<Prompt> updateList = promptList.stream().filter(prompt -> prompt.getPromptId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(updateList)) {
            updateList.forEach(updatePrompt -> updatePrompt.checkDataLegalization(promptRepository));
            return Results.success(promptRepository.insertOrUpdatePromptTenant(updateList, organizationId));
        } else {
            return Results.success(promptRepository.insertOrUpdatePromptTenant(promptList, organizationId));
        }
    }

    @ApiOperation(value = "删除多语言描述")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/prompts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    public ResponseEntity remove(@PathVariable Long organizationId, @Encrypt @RequestBody Prompt prompt) {
        prompt.setTenantId(organizationId);
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        promptRepository.deletePromptTenant(prompt, organizationId);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("多语言描述导入")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @PostMapping("/{organizationId}/prompts/prompt-import")
    public ResponseEntity promptImport(@PathVariable Long organizationId,
                                       @RequestParam(name = "promptFile") MultipartFile promptFile) {
        promptService.promptImport(organizationId, promptFile);
        return Results.success();
    }

    /**
     * 租户级查询多语言描述列表
     *
     * @param organizationId 租户id
     * @param prompt         多语言查询条件
     * @param pageRequest    分页
     * @return 统一返回结果
     */
    @ApiOperation(value = "租户级分页查询多语言描述信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/prompts/page-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @CustomPageRequest
    public ResponseEntity<Page<PromptDTO>> pagePromptList(@PathVariable Long organizationId, Prompt prompt,
            @ApiIgnore @SortDefault(value = "promptId",
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        prompt.setTenantId(organizationId);
        // 租户级多语言标签查询
        return Results.success(promptRepository.selectCurrentLangPrompts(pageRequest, prompt));
    }

    @ApiOperation(value = "租户级新增多语言描述信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/prompts/insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    public ResponseEntity insertPrompt(@PathVariable Long organizationId, @RequestBody Prompt prompt) {
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        return Results.success(promptService.insertPromptDescription(prompt, organizationId));
    }

    @ApiOperation(value = "租户级更新多语言描述信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/prompts/update")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    public ResponseEntity updatePrompt(@PathVariable Long organizationId, @RequestBody @Encrypt Prompt prompt) {
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        return Results.success(promptService.updatePromptDescription(prompt, organizationId));
    }

    @ApiOperation(value = "租户级多语言描述明细信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/prompts/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "promptKey", value = "多语言KEY", paramType = "query", required = true),
            @ApiImplicitParam(name = "promptCode", value = "多语言CODE", paramType = "query", required = true),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query", required = true),
            @ApiImplicitParam(name = "tenantId", value = "多语言租户Id", paramType = "query", required = true)
    })
    public ResponseEntity getPromptDetails(@PathVariable Long organizationId, @Encrypt Prompt prompt) {
        return Results.success(promptRepository.getPromptDetails(prompt));
    }

    @ApiOperation(value = "删除多语言描述信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/prompts/remove")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    public ResponseEntity removePrompt(@PathVariable Long organizationId, @RequestBody @Encrypt Prompt prompt) {
        prompt.setTenantId(organizationId);
        SecurityTokenHelper.validTokenIgnoreInsert(prompt);
        promptService.deletePromptByOptions(prompt);
        return Results.success();
    }

    @ApiOperation(value = "刷新多语言缓存")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/prompts/refresh/cache")
    public ResponseEntity refreshCache(@PathVariable Long organizationId) {
        promptDomainService.initCache();
        return Results.success();
    }
}
