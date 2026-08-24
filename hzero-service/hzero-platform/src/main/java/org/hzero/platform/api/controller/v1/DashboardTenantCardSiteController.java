package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DashboardTenantCardService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DashboardTenantCard;
import org.hzero.platform.domain.repository.DashboardTenantCardRepository;
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
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 租户卡片分配 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 09:30:19
 */
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_TENANT_CARD_SITE)
@RestController("dashboardTenantCardSiteController.v1")
@RequestMapping("/v1/dashboard-tenant-cards")
public class DashboardTenantCardSiteController extends BaseController {

    @Autowired
    private DashboardTenantCardRepository tenantCardRepository;
    @Autowired
    private DashboardTenantCardService tenantCardService;

    @ApiOperation(value = "租户卡片分配列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{cardId}")
    @CustomPageRequest
    public ResponseEntity<Page<DashboardTenantCard>> assignCardToTenantsList(@PathVariable("cardId") @Encrypt Long cardId,
                                                                             @Encrypt DashboardTenantCard dashboardTenantCard,
                                                                             @ApiIgnore @SortDefault(value = DashboardTenantCard.FIELD_CARD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dashboardTenantCard.setCardId(cardId);
        return Results.success(tenantCardRepository.getAssignCardToTenantsList(dashboardTenantCard, pageRequest));
    }

    @ApiOperation(value = "批量创建租户卡片分配")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<DashboardTenantCard>> createAssignCardToTenants(@RequestBody @Encrypt List<DashboardTenantCard> tenantCards) {
        this.validList(tenantCards);
        return Results.success(tenantCardService.createAssignCardToTenants(tenantCards));
    }

    @ApiOperation(value = "批量删除租户卡片分配")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity batchRemoveAssignTenantCards(@RequestBody @Encrypt List<DashboardTenantCard> tenantCards) {
        SecurityTokenHelper.validToken(tenantCards);
        tenantCardService.batchRemoveAssignTenantCards(tenantCards);
        return Results.success();
    }

}
