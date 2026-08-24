package org.hzero.message.api.controller.v1;

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

import java.util.List;
import java.util.Map;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * <p>
 * 获取消息接收者
 * </p>
 *
 * @author qingsheng.chen 2018/8/8 星期三 17:45
 */
@Api(tags = MessageSwaggerApiConfig.MESSAGE_RECEIVER)
@RestController("messageReceiverController.v1")
@RequestMapping("v1/{organizationId}/message/receiver/")
public class MessageReceiverController {
    private MessageReceiverService messageReceiverService;

    @Autowired
    public MessageReceiverController(MessageReceiverService messageReceiverService) {
        this.messageReceiverService = messageReceiverService;
    }

    @ApiOperation("获取消息接收人")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("type-code")
    public ResponseEntity<List<Receiver>> queryReceiver(@PathVariable("organizationId") Long organizationId,
                                                        @RequestParam @ApiParam(value = "接收人类型编码", required = true) String typeCode,
                                                        @RequestBody(required = false) @ApiParam(value = "自定义参数") Map<String, String> args) {
        return Results.success(messageReceiverService.queryReceiver(organizationId, typeCode, args));
    }

    @ApiOperation("获取消息接收人-第三方平台")
    @Permission(permissionWithin = true)
    @PostMapping("type-code/open")
    ResponseEntity<List<Receiver>> queryOpenReceiver(@PathVariable("organizationId") long tenantId,
                                                     @RequestParam("thirdPlatformType") String thirdPlatformType,
                                                     @RequestParam("typeCode") String typeCode,
                                                     @RequestBody(required = false) Map<String, String> args){
        return Results.success(messageReceiverService.queryOpenReceiver(tenantId, typeCode, thirdPlatformType, args));
    }
}
