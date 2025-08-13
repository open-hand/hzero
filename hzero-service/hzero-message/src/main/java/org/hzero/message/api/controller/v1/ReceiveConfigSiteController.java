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
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;

/**
 * 接收配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@Api(tags = MessageSwaggerApiConfig.RECEIVE_CONFIG_SITE)
@RestController("receiveConfigSiteController.v1")
@RequestMapping("/v1/receive-configs")
public class ReceiveConfigSiteController extends BaseController {

    private final ReceiveConfigRepository receiveConfigRepository;
    private final ReceiveConfigService receiveConfigService;

    @Autowired
    public ReceiveConfigSiteController(ReceiveConfigRepository receiveConfigRepository,
                                       ReceiveConfigService receiveConfigService) {
        this.receiveConfigRepository = receiveConfigRepository;
        this.receiveConfigService = receiveConfigService;
    }

    @ApiOperation(value = "接收配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<List<ReceiveConfigDTO>> listConfig(@RequestParam(value = "tenantId", required = false, defaultValue = "0") Long tenantId) {
        return Results.success(receiveConfigService.listConfig(tenantId));
    }

    @ApiOperation(value = "接收配置明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{receiveId}")
    public ResponseEntity<ReceiveConfig> detailConfig(@Encrypt @PathVariable Long receiveId,
                                                      @RequestParam(value = "tenantId", required = false, defaultValue = "0") Long tenantId) {
        ReceiveConfig receiveConfig = receiveConfigRepository.selectOne(new ReceiveConfig().setTenantId(tenantId).setReceiveId(receiveId));
        return Results.success(receiveConfig);
    }

    @ApiOperation(value = "创建及修改接收配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<ReceiveConfig>> createConfig(@Encrypt @RequestBody List<ReceiveConfig> receiveConfigList,
                                                            @RequestParam(value = "tenantId", required = false, defaultValue = "0") Long tenantId) {
        for (ReceiveConfig config : receiveConfigList) {
            if (config.getReceiveId() == null) {
                validObject(config);
            } else {
                validObject(config, ReceiveConfig.Validate.class);
                SecurityTokenHelper.validToken(config);
            }
        }
        return Results.success(receiveConfigService.createAndUpdate(receiveConfigList, tenantId));
    }

    @ApiOperation(value = "删除接收配置")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> removeConfig(@Encrypt @RequestBody ReceiveConfig receiveConfig) {
        SecurityTokenHelper.validToken(receiveConfig);
        receiveConfig.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        Assert.isTrue(receiveConfigRepository.selectCount(new ReceiveConfig().setParentReceiveCode(receiveConfig.getReceiveCode())) == 0,
                HmsgConstant.ErrorCode.DELETE_FAILED);
        receiveConfigService.deleteConfig(receiveConfig);
        return Results.success();
    }
}