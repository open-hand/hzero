package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.DashboardLayoutService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DashboardLayout;
import org.hzero.platform.domain.repository.DashboardLayoutRepository;
import org.hzero.platform.domain.vo.DashboardLayoutVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 工作台配置 管理 API
 *
 * @author zhiying.dong@hand-china.com 2018-09-25 10:51:53
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_LAYOUT_CONFIG)
@RestController("dashboardLayoutController.v1")
@RequestMapping("/v1")
public class DashboardLayoutController extends BaseController {
    @Autowired
    private DashboardLayoutService dashboardLayoutService;
    @Autowired
    private DashboardLayoutRepository layoutRepository;

    @ApiOperation(value = "查询工作台界面卡片配置信息")
    @Permission(permissionLogin = true)
    @GetMapping("/dashboard/layout")
    public ResponseEntity<List<DashboardLayoutVO>> dashboardLayoutDetail() {
        List<DashboardLayoutVO> dashboardLayouts = layoutRepository.selectDashboardLayout();
        return Results.success(dashboardLayouts);
    }

    @ApiOperation(value = "创建工作台配置")
    @Permission(permissionLogin = true)
    @PostMapping("/dashboard/layout")
    public ResponseEntity<List<DashboardLayout>> createDashboardLayout(@RequestBody @Encrypt List<DashboardLayout> dashboardLayouts) {
        dashboardLayouts.forEach(layout -> {
            layout.setUserId(DetailsHelper.getUserDetails().getUserId());
            layout.setRoleId(DetailsHelper.getUserDetails().getRoleId());
            layout.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        });
        validList(dashboardLayouts);
        return Results.success(dashboardLayoutService.createDashboardLayout(dashboardLayouts));
    }

    @ApiOperation(value = "查询配置首页面板卡片信息")
    @Permission(permissionLogin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/dashboard/layout/role-cards")
    public ResponseEntity<List<DashboardLayoutVO>> getDashboardLayoutCards() {
        return Results.success(layoutRepository.selectDashboardLayoutCards());
    }
}
