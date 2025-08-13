package org.hzero.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * <p>
 * 租户级租户API
 * </p>
 *
 * @author qingsheng.chen 2018/11/2 星期五 16:54
 */
@Api(tags = SwaggerApiConfig.IAM_TENANT_SITE)
@RestController("tenantSiteController.v1")
@RequestMapping("/v1/tenants")
public class TenantSiteController {

    @Autowired
    private TenantRepository tenantRepository;

    @ApiOperation(value = "查询有客制化菜单的租户")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/having-custom-menu")
    @CustomPageRequest
    public ResponseEntity<Page<Tenant>> pagingHavingCustomMenuTenants(@Encrypt Tenant tenant,
                                                                      @ApiIgnore @SortDefault(value = "tenantId", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(tenantRepository.pagingHavingCustomMenuTenants(tenant, pageRequest));
    }

    @ApiOperation(value = "分页查询租户")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<Tenant>> pagingTenants(@Encrypt Tenant tenant,
                                                      @ApiIgnore @SortDefault(value = "tenantId", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<Tenant> pages = tenantRepository.pagingTenantsList(tenant, pageRequest);
        return Results.success(pages);
    }

    @ApiOperation(value = "根据用户ID查询租户信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Tenant> selectTenantByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable @Encrypt Long userId) {
        return Results.success(this.tenantRepository.selectTenantByUserId(userId));
    }

    @ApiOperation(value = "查询当前用户所属租户")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/self")
    public ResponseEntity<Tenant> selectUsersTenant() {
        Tenant tenant;
        CustomUserDetails userDetail = DetailsHelper.getUserDetails();
        if (userDetail == null) {
            tenant = null;
        } else {
            tenant = this.tenantRepository.selectTenantByUserId(userDetail.getUserId());
        }
        return Results.success(tenant);
    }
}
