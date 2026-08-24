package org.hzero.message.api.controller.v1;

import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.DingTalkServerService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.DingTalkServer;
import org.hzero.message.domain.repository.DingTalkServerRepository;
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
import io.choerodon.swagger.annotation.Permission;

/**
 * 钉钉配置 管理 API
 *
 * @author zifeng.ding@hand-china.com 2019-11-13 14:36:25
 */
@Api(tags = MessageSwaggerApiConfig.DING_TALK_SERVER_SITE)
@RestController("dingTalkServerSiteController.v1")
@RequestMapping("/v1/dingtalk-servers")
public class DingTalkServerSiteController extends BaseController {

    @Autowired
    private DingTalkServerRepository dingTalkServerRepository;
    @Autowired
    private DingTalkServerService dingTalkServerService;

    @ApiOperation(value = "钉钉配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverCode", value = "钉钉编码", paramType = "query"),
            @ApiImplicitParam(name = "serverName", value = "钉钉名称", paramType = "query"),
            @ApiImplicitParam(name = "authType", value = "授权类型", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query")
    })
    public ResponseEntity<Page<DingTalkServer>> list(Long organizationId, String serverCode, String serverName, String authType, Integer enabledFlag,
                                                     @ApiIgnore @SortDefault(value = DingTalkServer.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<DingTalkServer> list = dingTalkServerRepository.pageDingTalkServer(pageRequest, organizationId, serverCode, serverName, authType, enabledFlag, false);
        return Results.success(list);
    }

    @ApiOperation(value = "钉钉配置明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{serverId}")
    public ResponseEntity<DingTalkServer> detail(@ApiParam("配置ID") @Encrypt @PathVariable Long serverId) {
        DingTalkServer dingTalkServer = dingTalkServerRepository.getDingTalkServerById(null, serverId);
        return Results.success(dingTalkServer);
    }

    @ApiOperation(value = "创建钉钉配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<DingTalkServer> create(@RequestBody DingTalkServer dingTalkServer) {
        validObject(dingTalkServer);
        return Results.success(dingTalkServerService.addDingTalkServer(dingTalkServer));
    }

    @ApiOperation(value = "修改钉钉配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<DingTalkServer> update(@Encrypt @RequestBody DingTalkServer dingTalkServer) {
        SecurityTokenHelper.validToken(dingTalkServer);
        validObject(dingTalkServer);
        return Results.success(dingTalkServerService.updateDingTalkServer(dingTalkServer));
    }
}
