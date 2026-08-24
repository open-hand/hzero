package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.MessageService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 消息信息 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Api(tags = MessageSwaggerApiConfig.MESSAGE_SITE)
@RestController("messageSiteController.v1")
@RequestMapping("v1/messages")
public class MessageSiteController extends BaseController {
    private MessageService messageService;

    @Autowired
    public MessageSiteController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ApiOperation("查询消息信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "serverCode", value = "消息模板编码", paramType = "query"),
            @ApiImplicitParam(name = "messageTypeCode", value = "消息类型", paramType = "query"),
            @ApiImplicitParam(name = "subject", value = "消息主题", paramType = "query"),
            @ApiImplicitParam(name = "trxStatusCode", value = "消息状态", paramType = "query"),
            @ApiImplicitParam(name = "startDate", value = "消息发送时间筛选，起始时间", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "消息发送时间筛选，结束时间", paramType = "query"),
            @ApiImplicitParam(name = "receiver", value = "接收人", paramType = "query")
    })
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<Message>> listMessage(@RequestParam(required = false) Long tenantId,
                                                     @RequestParam(required = false) String serverCode,
                                                     @RequestParam(required = false) String messageTypeCode,
                                                     @RequestParam(required = false) String subject,
                                                     @RequestParam(required = false) String trxStatusCode,
                                                     @RequestParam(required = false) Date startDate,
                                                     @RequestParam(required = false) Date endDate,
                                                     @RequestParam(required = false) String receiver,
                                                     @ApiIgnore @SortDefault(value = Message.FIELD_MESSAGE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(messageService.listMessage(tenantId, serverCode, messageTypeCode, subject, trxStatusCode, startDate, endDate, receiver, pageRequest));
    }

    @ApiOperation("查询消息信息内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageId", value = "消息ID", paramType = "path")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{messageId}/contents")
    public ResponseEntity<Message> getMessage(@Encrypt @PathVariable long messageId) {
        return Results.success(messageService.getMessage(null, messageId));
    }

    @ApiOperation("查询消息接收人列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageId", value = "消息ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query", required = true)
    })
    @CustomPageRequest
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{messageId}/receivers")
    public ResponseEntity<Page<MessageReceiver>> listMessageReceiver(@Encrypt @PathVariable long messageId,
                                                                     @ApiIgnore @SortDefault(value = MessageReceiver.FIELD_RECEIVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(messageService.listMessageReceiver(null, messageId, pageRequest));
    }

    @ApiOperation("查询消息错误信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transactionId", value = "事务ID", paramType = "query")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/transactions")
    public ResponseEntity<MessageTransaction> getMessageTransaction(@Encrypt @RequestParam long transactionId) {
        return Results.success(messageService.getMessageTransaction(null, transactionId));
    }

    @ApiOperation("重新发送消息")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/resend")
    public ResponseEntity<Message> resendMessage(@Encrypt @RequestParam long transactionId) {
        return Results.success(messageService.resendMessage(null, transactionId));
    }

    @ApiOperation("批量删除消息")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity deleteMessage(@Encrypt @RequestBody List<Message> messageList) {
        SecurityTokenHelper.validToken(messageList);
        messageService.batchDelete(messageList);
        return Results.success();
    }
}
