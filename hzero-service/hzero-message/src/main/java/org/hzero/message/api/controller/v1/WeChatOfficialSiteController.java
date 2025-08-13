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
@Api(tags = MessageSwaggerApiConfig.WE_CHAT_OFFICIAL_SITE)
@RestController("weChatOfficialSiteController.v1")
@RequestMapping("/v1/wechat-officials")
public class WeChatOfficialSiteController extends BaseController {

    @Autowired
    private WeChatOfficialRepository officialRepository;
    @Autowired
    private WeChatOfficialService officialService;

    @ApiOperation(value = "微信公众号配置列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "serverCode", value = "公众号编码", paramType = "query"),
            @ApiImplicitParam(name = "serverName", value = "公众号名称", paramType = "query"),
            @ApiImplicitParam(name = "authType", value = "授权类型", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query")
    })
    public ResponseEntity<Page<WechatOfficial>> pageAccount(Long tenantId, String serverCode, String serverName, String authType, Integer enabledFlag,
                                                            @ApiIgnore @SortDefault(value = WechatOfficial.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<WechatOfficial> list = officialRepository.pageWeChatOfficial(pageRequest, tenantId, serverCode, serverName, authType, enabledFlag, false);
        return Results.success(list);
    }

    @ApiOperation(value = "微信公众号配置明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{serverId}")
    public ResponseEntity<WechatOfficial> detailAccount(@Encrypt @PathVariable Long serverId) {
        return Results.success(officialRepository.getOfficialById(null, serverId));
    }

    @ApiOperation(value = "创建微信公众号配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<WechatOfficial> createAccount(@Encrypt @RequestBody WechatOfficial wechatOfficial) {
        validObject(wechatOfficial);
        return Results.success(officialService.addOfficial(wechatOfficial));
    }

    @ApiOperation(value = "修改微信公众号配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<WechatOfficial> updateAccount(@Encrypt @RequestBody WechatOfficial wechatOfficial) {
        validObject(wechatOfficial);
        SecurityTokenHelper.validToken(wechatOfficial);
        return Results.success(officialService.updateOfficial(wechatOfficial));
    }
}
