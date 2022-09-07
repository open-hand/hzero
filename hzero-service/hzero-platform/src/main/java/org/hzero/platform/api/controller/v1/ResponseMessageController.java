package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.ResponseMessageService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Message;
import org.hzero.platform.domain.repository.ResponseMessageRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 后端消息 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-08 15:04:28
 */
@Api(tags = {PlatformSwaggerApiConfig.RESPONSE_MESSAGE})
@RestController("responseMessageController.v1")
@RequestMapping("/v1/{organizationId}/response-messages")
public class ResponseMessageController extends BaseController {

    @Autowired
    private ResponseMessageRepository messageRepository;
    @Autowired
    private ResponseMessageService messageService;

    @ApiOperation(value = "后端消息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParam(name = "organizationId", value = "租户I的", paramType = "path", required = true)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Message>> messageList(Message message, PageRequest pageRequest) {
        return Results.success(messageRepository.selectMessageList(pageRequest, message));
    }

    @ApiOperation(value = "创建后端消息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParam(name = "organizationId", value = "租户I的", paramType = "path", required = true)
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        this.validObject(message);
        return Results.success(messageService.createMessage(message));
    }

    @ApiOperation(value = "修改后端消息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParam(name = "organizationId", value = "租户I的", paramType = "path", required = true)
    @PutMapping
    public ResponseEntity<Message> updateMessage(@RequestBody @Encrypt Message message) {
        SecurityTokenHelper.validToken(message);
        this.validObject(message);
        return Results.success(messageService.updateMessage(message));
    }

    @ApiOperation(value = "删除后端消息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParam(name = "organizationId", value = "租户I的", paramType = "path", required = true)
    @DeleteMapping
    public ResponseEntity<Message> removeMessage(@RequestBody @Encrypt Message message) {
        SecurityTokenHelper.validToken(message);
        messageService.deleteMessage(message);
        return Results.success();
    }

    @ApiOperation(value = "查询后端消息明细（编辑调用）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageId", value = "消息Id", paramType = "path"),
            @ApiImplicitParam(name = "organizationId", value = "租户I的", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/details/{messageId}")
    public ResponseEntity<Message> messageDetails(@PathVariable @Encrypt Long messageId) {
        return Results.success(messageRepository.selectMessageDetails(messageId));
    }

    @ApiOperation(value = "刷新返回消息缓存")
    @Permission(permissionPublic = true)
    @ApiImplicitParam(name = "organizationId", value = "租户I的", paramType = "path", required = true)
    @GetMapping("/refresh-cache")
    public ResponseEntity refreshMessageCache() {
        messageRepository.initAllReturnMessageToRedis();
        return Results.success();
    }
}
