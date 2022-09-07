package org.hzero.platform.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DashboardCardService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DashboardCard;
import org.hzero.platform.domain.repository.DashboardCardRepository;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 平台卡片管理 API
 * 
 * @author xiaoyu.zhao@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_CARD)
@RestController("dashboardCardController.v1")
@RequestMapping("/v1/{organizationId}/dashboard/card")
public class DashboardCardController extends BaseController {

    @Autowired
    private DashboardCardService cardService;
    @Autowired
    private DashboardCardRepository cardRepository;

    @ApiOperation("获取卡片列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<DashboardCard>> listDashboardCard(@PathVariable("organizationId") Long tenantId,
                    DashboardCard dashboardCard,
                    @ApiIgnore @SortDefault(value = DashboardCard.FIELD_CODE) PageRequest pageRequest) {
        // op仅查询当前租户下的卡片信息
        dashboardCard.setTenantId(tenantId);
        dashboardCard.setLevel(Constants.SITE_LEVEL_UPPER_CASE);
        return Results.success(cardRepository.getDashboardCards(dashboardCard, pageRequest));
    }

    @ApiOperation("创建卡片")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DashboardCard> createDashboardCard(@PathVariable("organizationId") Long tenantId,
                    @RequestBody DashboardCard dashboardCard) {
        dashboardCard.setTenantId(tenantId);
        // 设置site层级，可直接在角色中分配卡片
        dashboardCard.setLevel(Constants.SITE_LEVEL_UPPER_CASE);
        validObject(dashboardCard);
        return Results.success(cardService.createDashboardCard(dashboardCard));
    }

    @ApiOperation("修改卡片")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DashboardCard> updateDashboardCard(@PathVariable("organizationId") Long tenantId,
                    @RequestBody @Encrypt DashboardCard dashboardCard) {
        SecurityTokenHelper.validToken(dashboardCard);
        dashboardCard.setTenantId(tenantId);
        validObject(dashboardCard);
        return Results.success(cardService.updateDashboardCard(dashboardCard));
    }

    @ApiOperation("获取卡片详细信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/details/{dashboardCardId}")
    public ResponseEntity<DashboardCard> getDashboardCardDetails(@PathVariable("dashboardCardId") @Encrypt Long dashboardCardId) {
        return Results.success(cardRepository.getDashboardCardDetails(dashboardCardId));
    }

    @ApiOperation("获取可分配卡片列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/assign-list")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<DashboardCard>> listAssignableDashboardCard(
                    @PathVariable("organizationId") Long tenantId, DashboardCard dashboardCard,
                    @ApiIgnore @SortDefault(value = DashboardCard.FIELD_CODE) PageRequest pageRequest) {
        dashboardCard.setTenantId(tenantId);
        return Results.success(cardRepository.getAssignableDashboardCard(dashboardCard, pageRequest));
    }

}
