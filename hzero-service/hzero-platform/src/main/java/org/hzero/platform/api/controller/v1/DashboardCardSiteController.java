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
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
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
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_CARD_SITE)
@RestController("dashboardCardSiteController.v1")
@RequestMapping("/v1/dashboard/card")
public class DashboardCardSiteController extends BaseController {

    @Autowired
    private DashboardCardService dashboardCardService;
    @Autowired
    private DashboardCardRepository cardRepository;

    @ApiOperation("获取卡片列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<DashboardCard>> listDashboardCard(DashboardCard dashboardCard,
                                                                 @ApiIgnore @SortDefault(value = DashboardCard.FIELD_CODE) PageRequest pageRequest) {
        return Results.success(cardRepository.getDashboardCards(dashboardCard, pageRequest));
    }

    @ApiOperation("创建卡片")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<DashboardCard> createDashboardCard(@RequestBody DashboardCard dashboardCard) {
        dashboardCard.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        validObject(dashboardCard);
        return Results.success(dashboardCardService.createDashboardCard(dashboardCard));
    }

    @ApiOperation("修改卡片")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<DashboardCard> updateDashboardCard(@RequestBody @Encrypt DashboardCard dashboardCard) {
        SecurityTokenHelper.validToken(dashboardCard);
        dashboardCard.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        validObject(dashboardCard);
        return Results.success(dashboardCardService.updateDashboardCard(dashboardCard));
    }

    @ApiOperation("获取卡片详细信息")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/details/{dashboardCardId}")
    public ResponseEntity<DashboardCard> getDashboardCardDetails(@PathVariable("dashboardCardId") @Encrypt Long dashboardCardId) {
        return Results.success(cardRepository.getDashboardCardDetails(dashboardCardId));
    }

    @ApiOperation("获取可分配卡片列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/assign-list")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<DashboardCard>> listAssignableDashboardCard(@Encrypt DashboardCard dashboardCard,
                                                                           @ApiIgnore @SortDefault(value = DashboardCard.FIELD_CODE) PageRequest pageRequest) {
        dashboardCard.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        return Results.success(cardRepository.getAssignableDashboardCard(dashboardCard, pageRequest));
    }

}
