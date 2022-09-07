package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.DomainAssignService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.DomainAssign;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 单点二级域名分配 管理 API
 *
 * @author xiaoyu.zhao@hand-china.com 2020-09-02 15:34:46
 */
@Api(tags = SwaggerApiConfig.DOMAIN_ASSIGN_SITE)
@RestController("domainAssignSiteController.v1")
@RequestMapping("/v1/domain-assigns")
public class DomainAssignSiteController extends BaseController {

    @Autowired
    private DomainAssignService domainAssignService;

    @ApiOperation(value = "单点二级域名分配列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{domainId}")
    public ResponseEntity<Page<DomainAssign>> pageDomainAssign(@PathVariable("domainId") @Encrypt Long domainId,
                    @Encrypt DomainAssign domainAssign,
                    @ApiIgnore @SortDefault(value = DomainAssign.FIELD_DOMAIN_ASSIGN_ID,
                                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(domainAssignService.pageDomainAssign(domainId, domainAssign, pageRequest));
    }

    @ApiOperation(value = "单点二级域名分配明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{domainId}/{domainAssignId}")
    public ResponseEntity<DomainAssign> getDomainAssignDetail(@PathVariable("domainId") @Encrypt Long domainId,
                    @PathVariable("domainAssignId") @Encrypt Long domainAssignId) {
        return Results.success(domainAssignService.getDomainAssignDetail(domainId, domainAssignId));
    }

    @ApiOperation(value = "添加单点二级域名分配")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{domainId}")
    public ResponseEntity<DomainAssign> createDomainAssign(@PathVariable("domainId") @Encrypt Long domainId,
                    @RequestBody DomainAssign domainAssign) {
        return Results.success(domainAssignService.createDomainAssign(domainId, domainAssign));
    }

    @ApiOperation(value = "修改单点二级域名分配")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<DomainAssign> updateDomainAssign(@RequestBody @Encrypt DomainAssign domainAssign) {
        return Results.success(domainAssignService.updateDomainAssign(domainAssign));
    }

    @ApiOperation(value = "批量删除单点二级域名分配")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{domainId}")
    public ResponseEntity batchRemoveDomainAssigns(@PathVariable("domainId") @Encrypt Long domainId,
                    @RequestBody @Encrypt List<DomainAssign> domainAssigns) {
        domainAssignService.batchRemoveDomainAssigns(domainId, domainAssigns);
        return Results.success();
    }

}
