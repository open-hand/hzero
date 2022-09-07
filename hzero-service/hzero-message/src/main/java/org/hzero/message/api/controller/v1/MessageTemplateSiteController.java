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
@Api(tags = MessageSwaggerApiConfig.MESSAGE_TEMPLATE_SITE)
@RestController("messageTemplateSiteController.v1")
@RequestMapping("v1/message/templates")
public class MessageTemplateSiteController extends BaseController {
    private MessageTemplateService messageTemplateService;
    private MessageClientProperties messageClientProperties;

    @Autowired
    public MessageTemplateSiteController(MessageTemplateService messageTemplateService,
                                         MessageClientProperties messageClientProperties) {
        this.messageTemplateService = messageTemplateService;
        this.messageClientProperties = messageClientProperties;
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
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<MessageTemplate>> listMessageTemplate(@RequestParam(required = false) Long tenantId,
                                                                     @RequestParam(required = false) String templateCode,
                                                                     @RequestParam(required = false) String templateName,
                                                                     @RequestParam(required = false) Integer enabledFlag,
                                                                     @RequestParam(required = false) String lang,
                                                                     @ApiIgnore @SortDefault(value = MessageTemplate.FIELD_TEMPLATE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(messageTemplateService.listMessageTemplate(tenantId, templateCode, templateName, enabledFlag, lang, false, pageRequest));
    }

    @ApiOperation("查询消息模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "消息模板ID", paramType = "path", required = true)
    })
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{templateId}")
    public ResponseEntity<MessageTemplate> getMessageTemplate(@Encrypt @PathVariable long templateId) {
        return Results.success(messageTemplateService.getMessageTemplate(null, templateId));
    }

    @ApiOperation("根据编码查询消息模板明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/template-code")
    public ResponseEntity<MessageTemplate> getTemplateByCode(@RequestParam long tenantId,
                                                             @RequestParam String templateCode,
                                                             @RequestParam(required = false) String lang) {
        return Results.success(messageTemplateService.getMessageTemplate(tenantId, templateCode, StringUtils.hasText(lang) ? lang : messageClientProperties.getDefaultLang()));
    }

    @ApiOperation("查询消息模板语言")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/template-lang")
    public ResponseEntity<List<LanguageDTO>> listMessageTemplateLang(@RequestParam long tenantId,
                                                                     @RequestParam String messageCode) {
        return Results.success(messageTemplateService.listMessageTemplateLang(tenantId, messageCode));
    }

    @ApiOperation("查询消息模板参数")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/template-args")
    public ResponseEntity<Set<String>> listMessageTemplateArgs(@RequestParam long tenantId,
                                                               @RequestParam String messageCode,
                                                               @RequestParam String lang) {
        return Results.success(messageTemplateService.listMessageTemplateArgs(tenantId, messageCode, lang));
    }

    @ApiOperation("创建消息模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<MessageTemplate> createMessageTemplate(@RequestBody MessageTemplate messageTemplate) {
        validObject(messageTemplate);
        return Results.created(messageTemplateService.createMessageTemplate(messageTemplate));
    }

    @ApiOperation("修改消息模板")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<MessageTemplate> updateMessageTemplate(@Encrypt @RequestBody MessageTemplate messageTemplate) {
        SecurityTokenHelper.validToken(messageTemplate);
        validObject(messageTemplate);
        return Results.success(messageTemplateService.updateMessageTemplate(messageTemplate));
    }

}
