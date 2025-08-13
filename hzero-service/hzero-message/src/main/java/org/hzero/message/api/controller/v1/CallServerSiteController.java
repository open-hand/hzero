package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.CallServerService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.CallServer;
import org.hzero.message.domain.repository.CallServerRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 语音消息服务 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-26 15:59:05
 */
@Api(tags = MessageSwaggerApiConfig.CALL_SERVER_SITE)
@RestController("callServerSiteController.v1")
@RequestMapping("/v1/call-servers")
public class CallServerSiteController extends BaseController {

    @Autowired
    private CallServerRepository callServerRepository;
    @Autowired
    private CallServerService callServerService;

    @ApiOperation(value = "语音消息服务列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<CallServer>> page(Long tenantId, String serverCode, String serverName, String serverTypeCode, Integer enabledFlag,
                                                 @ApiIgnore @SortDefault(value = CallServer.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(callServerRepository.pageCallServer(pageRequest, tenantId, enabledFlag, serverTypeCode, serverCode, serverName, false));
    }

    @ApiOperation(value = "语音消息服务明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{serverId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<CallServer> detail(@Encrypt @PathVariable Long serverId) {
        CallServer callServer = callServerRepository.detailCallServer(null, serverId);
        return Results.success(callServer);
    }

    @ApiOperation(value = "创建语音消息服务")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<CallServer> create(@RequestBody CallServer callServer) {
        validObject(callServer);
        return Results.success(callServerService.createCallServer(callServer));
    }

    @ApiOperation(value = "修改语音消息服务")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<CallServer> update(@Encrypt @RequestBody CallServer callServer) {
        validObject(callServer);
        SecurityTokenHelper.validToken(callServer);
        return Results.success(callServerService.updateCallServer(callServer));
    }
}
