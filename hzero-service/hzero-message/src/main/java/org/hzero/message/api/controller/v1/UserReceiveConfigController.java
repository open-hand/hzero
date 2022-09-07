package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.UserReceiveConfigDTO;
import org.hzero.message.app.service.UserReceiveConfigService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.UserReceiveConfig;
import org.hzero.message.domain.repository.UserReceiveConfigRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;

/**
 * 用户接收配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@Api(tags = MessageSwaggerApiConfig.USER_RECEIVE_CONFIG)
@RestController("userReceiveConfigController.v1")
@RequestMapping("/v1/user-receive-configs")
public class UserReceiveConfigController extends BaseController {

    private final UserReceiveConfigRepository userReceiveConfigRepository;
    private final UserReceiveConfigService userReceiveConfigService;

    @Autowired
    public UserReceiveConfigController(UserReceiveConfigRepository userReceiveConfigRepository,
                                       UserReceiveConfigService userReceiveConfigService) {
        this.userReceiveConfigRepository = userReceiveConfigRepository;
        this.userReceiveConfigService = userReceiveConfigService;
    }

    @ApiOperation(value = "用户接收配置列表")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<UserReceiveConfigDTO>> listUserConfig() {
        Assert.notNull(DetailsHelper.getUserDetails(), HmsgConstant.ErrorCode.USER_DETAIL_NOT_FOUND);
        Assert.notNull(DetailsHelper.getUserDetails().getUserId(), HmsgConstant.ErrorCode.USER_DETAIL_NOT_FOUND);
        return Results.success(userReceiveConfigService.listUserConfig(DetailsHelper.getUserDetails().getUserId(), DetailsHelper.getUserDetails().getTenantId()));
    }

    @ApiOperation(value = "用户接收配置明细")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{userReceiveId}")
    public ResponseEntity<UserReceiveConfig> detailUserConfig(@Encrypt @PathVariable Long userReceiveId) {
        UserReceiveConfig userReceiveConfig = userReceiveConfigRepository.selectByPrimaryKey(userReceiveId);
        return Results.success(userReceiveConfig);
    }

    @ApiOperation(value = "创建及修改用户接收配置")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<UserReceiveConfigDTO>> createUserConfig(@Encrypt @RequestBody List<UserReceiveConfig> userReceiveConfigList) {
        for (UserReceiveConfig config : userReceiveConfigList) {
            validObject(config);
        }
        return Results.success(userReceiveConfigService.createAndUpdate(DetailsHelper.getUserDetails().getTenantId(), userReceiveConfigList));
    }

    @ApiOperation(value = "刷新租户下用户接收配置")
    @Permission(permissionWithin = true)
    @GetMapping("/refresh/tenant")
    public ResponseEntity<Void> refreshTenantUserConfig(@RequestParam("tenantId") Long tenantId) {
        userReceiveConfigService.refreshTenantUserConfig(tenantId);
        return Results.success();
    }

    @ApiOperation(value = "刷新所有用户接收配置")
    @Permission(permissionWithin = true)
    @GetMapping("/refresh/all")
    public ResponseEntity<Void> refreshAllUserConfig() {
        userReceiveConfigService.refreshAllUserConfig();
        return Results.success();
    }
}
