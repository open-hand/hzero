package org.hzero.iam.api.controller.v1;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * <p>
 * 租户级租户API
 * </p>
 *
 * @author qingsheng.chen 2018/11/2 星期五 16:54
 */
@Api(tags = SwaggerApiConfig.IAM_TENANT)
@RestController("tenantController.v1")
@RequestMapping("/v1")
public class TenantController {

    @Autowired
    private TenantRepository tenantRepository;

    @ApiOperation(value = "租户级分页查询租户")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/tenants/paging-tenant")
    @CustomPageRequest
    public ResponseEntity<Page<Tenant>> pagingTenants(
            @PathVariable Long organizationId,
            @Encrypt Tenant tenant,
            @ApiIgnore @SortDefault(value = "tenantId", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(tenantRepository.pagingTenantsList(tenant, pageRequest));
    }

    @ApiOperation(value = "根据租户ID查询租户信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/tenants")
    public ResponseEntity<Tenant> selectTenant(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId) {
        return Results.success(tenantRepository.selectTenantDetails(organizationId));
    }

    @ApiOperation(value = "拉取最近更新过的记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/tenants/recent")
    public ResponseEntity<List<Tenant>> listRecentTenant(
            @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
            @ApiParam("过去多久内(单位：ms，默认5min)") @RequestParam(required = false, defaultValue = "300000") Long before) {
        return Results.success(tenantRepository.selectByCondition(Condition.builder(Tenant.class)
                .andWhere(Sqls.custom()
                        .andGreaterThan(Tenant.FIELD_LAST_UPDATE_DATE, new Date(System.currentTimeMillis() - before))
                        .andEqualTo(Tenant.TENANT_ID, tenantId, true))
                .build()));
    }
}
