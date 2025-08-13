package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.WeChatEnterpriseService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.WeChatEnterprise;
import org.hzero.message.domain.repository.WeChatEnterpriseRepository;
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
 * 企业微信配置 管理 API
 *
 * @author fanghan.liu@hand-china.com 2019-10-15 14:31:46
 */
@Api(tags = MessageSwaggerApiConfig.WE_CHAT_ENTERPRISE)
@RestController("weChatEnterpriseController.v1")
@RequestMapping("/v1/{organizationId}/wechat-enterprises")
public class WeChatEnterpriseController extends BaseController {

    @Autowired
    private WeChatEnterpriseRepository wechatEnterpriseRepository;
    @Autowired
    private WeChatEnterpriseService wechatEnterpriseService;

    @ApiOperation(value = "企业微信配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverCode", value = "配置编码", paramType = "query"),
            @ApiImplicitParam(name = "serverName", value = "配置名称", paramType = "query"),
            @ApiImplicitParam(name = "authType", value = "授权类型", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<WeChatEnterprise>> list(@PathVariable Long organizationId, String serverCode, String serverName, String authType, Integer enabledFlag,
                                                       @ApiIgnore @SortDefault(value = WeChatEnterprise.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<WeChatEnterprise> list = wechatEnterpriseRepository.pageWeChatEnterprise(pageRequest, organizationId, serverCode, serverName, authType, enabledFlag, true);
        return Results.success(list);
    }

    @ApiOperation(value = "企业微信配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{serverId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<WeChatEnterprise> detail(@PathVariable Long organizationId, @Encrypt @PathVariable Long serverId) {
        WeChatEnterprise wechatEnterprise = wechatEnterpriseRepository.getWeChatEnterpriseById(organizationId, serverId);
        return Results.success(wechatEnterprise);
    }

    @ApiOperation(value = "创建企业微信配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<WeChatEnterprise> create(@PathVariable Long organizationId, @RequestBody WeChatEnterprise wechatEnterprise) {
        wechatEnterprise.setTenantId(organizationId);
        validObject(wechatEnterprise);
        return Results.success(wechatEnterpriseService.createWeChatEnterprise(wechatEnterprise));
    }

    @ApiOperation(value = "修改企业微信配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<WeChatEnterprise> update(@PathVariable Long organizationId, @Encrypt @RequestBody WeChatEnterprise wechatEnterprise) {
        wechatEnterprise.setTenantId(organizationId);
        validObject(wechatEnterprise);
        SecurityTokenHelper.validToken(wechatEnterprise);
        wechatEnterprise.validateTenant(wechatEnterpriseRepository);
        return Results.success(wechatEnterpriseService.updateWeChatEnterprise(wechatEnterprise));
    }

    @ApiOperation(value = "删除企业微信配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity deleteWeChatEnterprise(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody WeChatEnterprise weChatEnterprise) {
        weChatEnterprise.setTenantId(organizationId);
        SecurityTokenHelper.validToken(weChatEnterprise);
        wechatEnterpriseService.deleteWeChatEnterprise(weChatEnterprise);
        return Results.success();
    }
}
