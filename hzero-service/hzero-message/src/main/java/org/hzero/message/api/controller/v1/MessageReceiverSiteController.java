package org.hzero.message.api.controller.v1;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * <p>
 * 获取消息接收者
 * </p>
 *
 * @author qingsheng.chen 2018/8/8 星期三 17:45
 */
@Api(tags = MessageSwaggerApiConfig.MESSAGE_RECEIVER_SITE)
@RestController("messageReceiverSiteController.v1")
@RequestMapping("v1/message/receiver/")
public class MessageReceiverSiteController {
    private MessageReceiverService messageReceiverService;

    @Autowired
    public MessageReceiverSiteController(MessageReceiverService messageReceiverService) {
        this.messageReceiverService = messageReceiverService;
    }

    @ApiOperation("获取消息接收人")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("type-code")
    public ResponseEntity<List<Receiver>> queryReceiver(@RequestParam @ApiParam(value = "租户ID", required = true) long tenantId,
                                                        @RequestParam @ApiParam(value = "接收人类型编码", required = true) String typeCode,
                                                        @RequestBody(required = false) @ApiParam(value = "自定义参数") Map<String, String> args) {
        return Results.success(messageReceiverService.queryReceiver(tenantId, typeCode, args));
    }
}
