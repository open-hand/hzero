package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.ReceiveConfigDTO;
import org.hzero.message.app.service.ReceiveConfigService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.ReceiveConfig;
import org.hzero.message.domain.repository.ReceiveConfigRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * 接收配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@Api(tags = MessageSwaggerApiConfig.RECEIVE_CONFIG)
@RestController("receiveConfigController.v1")
@RequestMapping("/v1/{organizationId}/receive-configs")
public class ReceiveConfigController extends BaseController {

    private final ReceiveConfigRepository receiveConfigRepository;
    private final ReceiveConfigService receiveConfigService;

    @Autowired
    public ReceiveConfigController(ReceiveConfigRepository receiveConfigRepository,
                                   ReceiveConfigService receiveConfigService) {
        this.receiveConfigRepository = receiveConfigRepository;
        this.receiveConfigService = receiveConfigService;
    }

    @ApiOperation(value = "接收配置列表")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<ReceiveConfigDTO>> listConfig(@PathVariable Long organizationId) {
        return Results.success(receiveConfigService.listConfig(organizationId));
    }

    @ApiOperation(value = "接收配置明细")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{receiveId}")
    public ResponseEntity<ReceiveConfig> detailConfig(@Encrypt @PathVariable Long organizationId, @PathVariable Long receiveId) {
        ReceiveConfig receiveConfig = receiveConfigRepository.selectOne(new ReceiveConfig()
                .setReceiveId(receiveId)
                .setTenantId(organizationId));
        return Results.success(receiveConfig);
    }

    @ApiOperation(value = "创建及修改接收配置")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<ReceiveConfig>> createConfig(@PathVariable Long organizationId, @Encrypt @RequestBody List<ReceiveConfig> receiveConfigList) {
        for (ReceiveConfig config : receiveConfigList) {
            if (config.getReceiveId() == null) {
                validObject(config);
            } else {
                validObject(config, ReceiveConfig.Validate.class);
                SecurityTokenHelper.validToken(config);
            }
        }
        return Results.success(receiveConfigService.createAndUpdate(receiveConfigList, organizationId));
    }

    @ApiOperation(value = "删除接收配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> removeConfig(@PathVariable Long organizationId, @Encrypt @RequestBody ReceiveConfig receiveConfig) {
        receiveConfig.setTenantId(organizationId);
        SecurityTokenHelper.validToken(receiveConfig);
        Assert.isTrue(receiveConfigRepository.selectCount(new ReceiveConfig().setParentReceiveCode(receiveConfig.getReceiveCode()).setTenantId(organizationId)) == 0,
                HmsgConstant.ErrorCode.DELETE_FAILED);
        receiveConfigService.deleteConfig(receiveConfig);
        return Results.success();
    }
}