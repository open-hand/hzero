package org.hzero.message.api.controller.v1;

import io.swagger.annotations.*;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * <p>
 * 消息标题内容获取API
 * </p>
 *
 * @author qingsheng.chen 2018/7/31 星期二 10:08
 */
@Api(tags = MessageSwaggerApiConfig.MESSAGE_GENERATOR)
@RestController("messageGeneratorController.v1")
@RequestMapping("v1/{organizationId}")
public class MessageGeneratorController {

    private final MessageGeneratorService messageGeneratorService;
    private final MessageClientProperties messageClientProperties;

    @Autowired
    public MessageGeneratorController(MessageGeneratorService messageGeneratorService, MessageClientProperties messageClientProperties) {
        this.messageGeneratorService = messageGeneratorService;
        this.messageClientProperties = messageClientProperties;
    }

    @ApiOperation("生成消息内容和标题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "templateCode", value = "消息模板编码", paramType = "path", required = true),
            @ApiImplicitParam(name = "args", value = "消息替换参数", paramType = "body")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/messages/contents")
    public ResponseEntity<Message> generateContent(@PathVariable("organizationId") Long organizationId,
                                                   @RequestBody @ApiParam(value = "templateCode(必须),args(可空),lang(可空，取默认语言)") UserMessageDTO userMessage) {
        return Results.success(messageGeneratorService.generateMessage(organizationId, userMessage.getTemplateCode(),
                StringUtils.hasText(userMessage.getLang()) ? userMessage.getLang() : messageClientProperties.getDefaultLang(), userMessage.getArgs()));
    }
}
