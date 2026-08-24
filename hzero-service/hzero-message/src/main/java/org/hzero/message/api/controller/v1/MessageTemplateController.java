package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.LanguageDTO;
import org.hzero.message.app.service.MessageTemplateService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.repository.MessageTemplateRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 消息模板 管理 API
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:11:11
 */
@Api(tags = MessageSwaggerApiConfig.MESSAGE_TEMPLATE)
@RestController("messageTemplateController.v1")
@RequestMapping("v1/{organizationId}/message/templates")
public class MessageTemplateController extends BaseController {
    private MessageTemplateService messageTemplateService;
    private MessageClientProperties messageClientProperties;
    private MessageTemplateRepository messageTemplateRepository;

    @Autowired
    public MessageTemplateController(MessageTemplateService messageTemplateService,
                                     MessageClientProperties messageClientProperties,
                                     MessageTemplateRepository messageTemplateRepository) {
        this.messageTemplateService = messageTemplateService;
        this.messageClientProperties = messageClientProperties;
        this.messageTemplateRepository = messageTemplateRepository;
    }

    @ApiOperation("查询消息模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "templateCode", value = "消息模板编码", paramType = "query"),
            @ApiImplicitParam(name = "templateName", value = "消息模板名称", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query"),
            @ApiImplicitParam(name = "lang", value = "语言", paramType = "query")
    })
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<MessageTemplate>> listMessageTemplate(@PathVariable("organizationId") Long organizationId,
                                                                     @RequestParam(required = false) String templateCode,
                                                                     @RequestParam(required = false) String templateName,
                                                                     @RequestParam(required = false) Integer enabledFlag,
                                                                     @RequestParam(required = false) String lang,
                                                                     @ApiIgnore @SortDefault(value = MessageTemplate.FIELD_TEMPLATE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(messageTemplateService.listMessageTemplate(organizationId, templateCode, templateName, enabledFlag, lang, true, pageRequest));
    }

    @ApiOperation("查询消息模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "消息模板ID", paramType = "path", required = true)
    })
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{templateId}")
    public ResponseEntity<MessageTemplate> getMessageTemplate(@PathVariable Long organizationId,
                                                              @Encrypt @PathVariable long templateId) {
        return Results.success(messageTemplateService.getMessageTemplate(organizationId, templateId));
    }

    @ApiOperation("消息模板复制")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "消息模板ID", paramType = "path", required = true)
    })
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{templateId}/copy")
    public ResponseEntity<MessageTemplate> copyMessageTemplate(@PathVariable Long organizationId,
                                                               @Encrypt @PathVariable long templateId) {
        return Results.success(messageTemplateService.copyMessageTemplate(organizationId, templateId));
    }

    @ApiOperation("根据编码查询消息模板明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/template-code")
    public ResponseEntity<MessageTemplate> getTemplateByCode(@PathVariable("organizationId") Long organizationId,
                                                             @RequestParam String templateCode,
                                                             @RequestParam(required = false) String lang) {
        return Results.success(messageTemplateService.getMessageTemplate(organizationId, templateCode, StringUtils.hasText(lang) ? lang : messageClientProperties.getDefaultLang()));
    }

    @ApiOperation("查询消息模板语言")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/template-lang")
    public ResponseEntity<List<LanguageDTO>> listMessageTemplateLang(@PathVariable("organizationId") Long organizationId,
                                                                     @RequestParam String messageCode) {
        return Results.success(messageTemplateService.listMessageTemplateLang(organizationId, messageCode));
    }

    @ApiOperation("查询消息模板参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/template-args")
    public ResponseEntity<Set<String>> listMessageTemplateArgs(@PathVariable("organizationId") Long organizationId,
                                                               @RequestParam String messageCode,
                                                               @RequestParam String lang) {
        return Results.success(messageTemplateService.listMessageTemplateArgs(organizationId, messageCode, lang));
    }

    @ApiOperation("创建消息模板")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<MessageTemplate> createMessageTemplate(@PathVariable("organizationId") Long organizationId, @RequestBody MessageTemplate messageTemplate) {
        messageTemplate.setTenantId(organizationId);
        validObject(messageTemplate);
        return Results.created(messageTemplateService.createMessageTemplate(messageTemplate));
    }

    @ApiOperation("修改消息模板")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<MessageTemplate> updateMessageTemplate(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody MessageTemplate messageTemplate) {
        messageTemplate.setTenantId(organizationId);
        SecurityTokenHelper.validToken(messageTemplate);
        validObject(messageTemplate);
        messageTemplate.validateTenant(messageTemplateRepository);
        return Results.success(messageTemplateService.updateMessageTemplate(messageTemplate));
    }

    @ApiOperation("删除消息模板")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity deleteMessageTemplate(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody MessageTemplate messageTemplate) {
        messageTemplate.setTenantId(organizationId);
        SecurityTokenHelper.validToken(messageTemplate);
        messageTemplate.validateCodeExistPredefined(messageTemplateRepository);
        return Results.success(messageTemplateRepository.deleteByPrimaryKey(messageTemplate.getTemplateId()));
    }
}
