package org.hzero.iam.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.DomainService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Domain;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 单点二级域名 管理 API
 *
 * @author minghui.qiu@hand-china.com 2019-06-27 20:50:16
 */
@Api(tags = SwaggerApiConfig.DOMAIN_SITE)
@RestController("domainSiteController.v1")
@RequestMapping("/v1/domains")
public class DomainSiteController extends BaseController {
    
    @Autowired
    private DomainService domainService;

    @ApiOperation(value = "单点二级域名列表")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    public ResponseEntity<Page<Domain>> list(Domain domain, @ApiIgnore @SortDefault(value = Domain.FIELD_DOMAIN_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<Domain> list = domainService.selectByOptions(pageRequest, domain);
        return Results.success(list);
    }

    @ApiOperation(value = "单点二级域名明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{domainId}")
    public ResponseEntity<Domain> detail(@Encrypt @PathVariable Long domainId) {
        Domain domain = domainService.selectByDomainId(domainId);
        return Results.success(domain);
    }

    @ApiOperation(value = "创建单点二级域名")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Domain> create(@RequestBody Domain domain) {
        domain.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        validObject(domain);
        domainService.insertDomain(domain);
        return Results.success(domain);
    }

    @ApiOperation(value = "修改单点二级域名")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<Domain> update(@RequestBody @Encrypt Domain domain) {
        SecurityTokenHelper.validToken(domain);
        domainService.updateDomain(domain);
        return Results.success(domain);
    }

    @ApiOperation(value = "删除单点二级域名")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody @Encrypt Domain domain) {
        SecurityTokenHelper.validToken(domain);
        domainService.deleteDomain(domain);
        return Results.success();
    }

}
