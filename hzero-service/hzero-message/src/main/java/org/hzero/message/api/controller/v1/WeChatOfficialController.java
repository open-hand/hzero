package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.WeChatOfficialService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.WechatOfficial;
import org.hzero.message.domain.repository.WeChatOfficialRepository;
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
 * 微信公众号配置 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-10-15 14:33:21
 */
@Api(tags = MessageSwaggerApiConfig.WE_CHAT_OFFICIAL)
@RestController("weChatOfficialController.v1")
@RequestMapping("/v1/{organizationId}/wechat-officials")
public class WeChatOfficialController extends BaseController {

    @Autowired
    private WeChatOfficialRepository officialRepository;
    @Autowired
    private WeChatOfficialService officialService;

    @ApiOperation(value = "微信公众号配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverCode", value = "公众号编码", paramType = "query"),
            @ApiImplicitParam(name = "serverName", value = "公众号名称", paramType = "query"),
            @ApiImplicitParam(name = "authType", value = "授权类型", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query")
    })
    public ResponseEntity<Page<WechatOfficial>> pageAccount(@PathVariable Long organizationId, String serverCode, String serverName, String authType, Integer enabledFlag,
                                                            @ApiIgnore @SortDefault(value = WechatOfficial.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<WechatOfficial> list = officialRepository.pageWeChatOfficial(pageRequest, organizationId, serverCode, serverName, authType, enabledFlag, true);
        return Results.success(list);
    }

    @ApiOperation(value = "微信公众号配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{serverId}")
    public ResponseEntity<WechatOfficial> detailAccount(@PathVariable Long organizationId, @Encrypt @PathVariable Long serverId) {
        return Results.success(officialRepository.getOfficialById(organizationId, serverId));
    }

    @ApiOperation(value = "创建微信公众号配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<WechatOfficial> createAccount(@PathVariable Long organizationId, @RequestBody WechatOfficial wechatOfficial) {
        wechatOfficial.setTenantId(organizationId);
        validObject(wechatOfficial);
        return Results.success(officialService.addOfficial(wechatOfficial));
    }

    @ApiOperation(value = "修改微信公众号配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<WechatOfficial> updateAccount(@PathVariable Long organizationId, @Encrypt @RequestBody WechatOfficial wechatOfficial) {
        wechatOfficial.setTenantId(organizationId);
        validObject(wechatOfficial);
        SecurityTokenHelper.validToken(wechatOfficial);
        wechatOfficial.validateTenant(officialRepository);
        return Results.success(officialService.updateOfficial(wechatOfficial));
    }

    @ApiOperation(value = "删除微信公众号配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity deleteWeChatEnterprise(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody WechatOfficial wechatOfficial) {
        wechatOfficial.setTenantId(organizationId);
        SecurityTokenHelper.validToken(wechatOfficial);
        officialService.deleteOfficial(wechatOfficial);
        return Results.success();
    }
}
