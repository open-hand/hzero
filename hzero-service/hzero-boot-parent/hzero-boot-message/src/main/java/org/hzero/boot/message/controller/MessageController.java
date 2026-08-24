package org.hzero.boot.message.controller;

import org.hzero.boot.message.constant.WebSocketConstant;
import org.hzero.boot.message.entity.Msg;
import org.hzero.boot.message.handler.ISocketHandler;
import org.hzero.boot.message.registry.SocketHandlerRegistry;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/28 19:17
 */

@RestController
public class MessageController {

    @Permission(permissionWithin = true)
    @PostMapping(WebSocketConstant.MESSAGE_HANDLER_PATH)
    public ResponseEntity handlerMessage(@RequestBody Msg msg) {
        Object handler = SocketHandlerRegistry.getHandler(msg.getKey());
        if (handler instanceof ISocketHandler){
            ((ISocketHandler) handler).processMessage(msg);
        }
        return Results.success();
    }
}
