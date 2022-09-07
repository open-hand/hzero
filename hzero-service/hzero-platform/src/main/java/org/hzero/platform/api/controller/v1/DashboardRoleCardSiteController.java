package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.DashboardRoleCardService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DashboardRoleCard;
import org.hzero.platform.domain.repository.DashboardRoleCardRepository;
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
 * 角色卡片表 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 19:58:17
 */
@Api(tags = PlatformSwaggerApiConfig.DASHBOARD_ROLE_CARD_SITE)
@RestController("dashboardRoleCardSiteController.v1")
@RequestMapping("/v1/dashboard-role-cards")
public class DashboardRoleCardSiteController extends BaseController {

    @Autowired
    private DashboardRoleCardRepository roleCardRepository;
    @Autowired
    private DashboardRoleCardService roleCardService;

    @ApiOperation(value = "角色卡片表列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{roleId}")
    public ResponseEntity<Page<DashboardRoleCard>> getRoleCardList(@PathVariable("roleId") @Encrypt Long roleId,
                                                                   @Encrypt DashboardRoleCard dashboardRoleCard,
                                                                   @ApiIgnore @SortDefault(value = DashboardRoleCard.FIELD_CARD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dashboardRoleCard.setRoleId(roleId);
        return Results.success(roleCardRepository.getRoleCardList(dashboardRoleCard, pageRequest));
    }

    @ApiOperation(value = "批量创建角色卡片表")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<DashboardRoleCard>> batchCreateOrUpdateRoleCard(@RequestBody @Encrypt List<DashboardRoleCard> dashboardRoleCards) {
        validList(dashboardRoleCards);
        return Results.success(roleCardService.batchCreateOrUpdateRoleCard(dashboardRoleCards));
    }

    @ApiOperation(value = "批量删除角色卡片表")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<List<DashboardRoleCard>> batchRemoveRoleCard(@RequestBody @Encrypt List<DashboardRoleCard> dashboardRoleCards) {
        SecurityTokenHelper.validToken(dashboardRoleCards);
        roleCardService.batchRemoveRoleCard(dashboardRoleCards);
        return Results.success();
    }

    @ApiOperation(value = "角色卡片分配列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    @GetMapping("/role-assign-card")
    public ResponseEntity<Page<DashboardRoleCard>> getRoleAssignCard(@Encrypt DashboardRoleCard dashboardRoleCard,
                                                                     @ApiIgnore PageRequest pageRequest) {
        dashboardRoleCard.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        return Results.success(roleCardRepository.selectRoleAssignCard(dashboardRoleCard, pageRequest));
    }

}
