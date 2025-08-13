package org.hzero.iam.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.OpenAppService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.OpenApp;
import org.hzero.iam.domain.repository.OpenAppRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author jiaxu.cui@hand-china.com 2018/9/29 14:10
 */
@Api(tags = SwaggerApiConfig.OPEN_APP_SITE)
@RestController("hiamOpenAppSiteController.v1")
@RequestMapping("/hzero/v1/open-app")
public class OpenAppSiteController extends BaseController {

    @Autowired
    private OpenAppRepository openAppRepository;
    @Autowired
    private OpenAppService openAppService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("获取三方网站列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appCode", value = "应用编码", paramType = "query"),
            @ApiImplicitParam(name = "appName", value = "应用名称", paramType = "query"),
            @ApiImplicitParam(name = "channel", value = "设备类型，值集HIAM.CHANNEL", paramType = "query")
    })
    @GetMapping
    public ResponseEntity<Page<OpenApp>> pageOpenApp(OpenApp openApp, @ApiIgnore @SortDefault(value = OpenApp.FIELD_ORDER_SEQ,
            direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(openAppRepository.pageOpenApp(pageRequest, openApp));
    }

    @ApiOperation("根据三方网站Id查询三方网站")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{openAppId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<OpenApp> detail(@Encrypt@PathVariable Long openAppId) {
        return Results.success(openAppRepository.getOpenAppDetails(openAppId));
    }

    @ApiOperation("新增三方网站")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<OpenApp> createOpenApp(@RequestBody OpenApp openApp) {
        this.validObject(openApp);
        Assert.notNull(openApp.getAppKey(), BaseConstants.ErrorCode.NOT_NULL);
        openApp.setOrganizationId(BaseConstants.DEFAULT_TENANT_ID);
        return Results.success(openAppService.createOpenApp(openApp));
    }

    @ApiOperation("更新三方网站")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<OpenApp> updateOpenApp(@RequestBody OpenApp openApp) {
        this.validObject(openApp);
        SecurityTokenHelper.validToken(openApp);
        return Results.success(openAppService.updateOpenApp(openApp));
    }

    @ApiOperation("删除三方网站")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<OpenApp> deleteOpenApp(@RequestBody OpenApp openApp) {
        SecurityTokenHelper.validToken(openApp);
        openAppService.deleteOpenApp(openApp.getOpenAppId());
        return Results.success();
    }

    @ApiOperation("启用三方网站")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/enabled")
    public ResponseEntity<OpenApp> enableOpenApp(@RequestBody OpenApp openApp) {
        SecurityTokenHelper.validToken(openApp);
        return Results.success(openAppService.enableOpenApp(openApp.getOpenAppId()));
    }

    @ApiOperation("禁用三方网站")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/disabled")
    public ResponseEntity<OpenApp> disableOpenApp(@RequestBody OpenApp openApp) {
        SecurityTokenHelper.validToken(openApp);
        return Results.success(openAppService.disableOpenApp(openApp.getOpenAppId()));
    }
}
