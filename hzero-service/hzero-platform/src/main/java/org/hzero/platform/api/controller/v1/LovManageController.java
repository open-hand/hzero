package org.hzero.platform.api.controller.v1;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.app.service.LovValueService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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
import io.swagger.annotations.ApiParam;

/**
 * 值集管理API(租户级) v1
 *
 * @author gaokuo.dai@hand-china.com    2018年6月5日下午7:40:37
 */
@Api(tags = PlatformSwaggerApiConfig.LOV_MANAGE)
@RestController("lovManageController.v1")
@RequestMapping("/v1/{organizationId}")
public class LovManageController extends BaseController {

    @Autowired
    private LovService lovService;
    @Autowired
    private LovRepository lovRepository;
    @Autowired
    private LovValueRepository lovValueRepository;
    @Autowired
    private LovValueService lovValueService;

    @ApiOperation("查询单个值集头")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/lov-headers/{lovId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Lov> queryHeader(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @ApiParam(value = "值集ID", required = true) @PathVariable @Encrypt Long lovId) {
        return Results.success(this.lovRepository.selectLovHeaderByLovId(lovId, tenantId, false));
    }

    @ApiOperation("根据值集头ID分页查询值集值")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    @GetMapping("/lov-headers/{lovId}/values")
    public ResponseEntity<Page<LovValue>> pageAndSortValuesByLovId(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @ApiParam(value = "值集ID", required = true) @PathVariable @Encrypt Long lovId,
            @ApiParam(value = "值") @RequestParam(value = "value", required = false) String value,
            @ApiParam(value = "含义") @RequestParam(value = "meaning", required = false) String meaning,
            @ApiIgnore @SortDefault(value = LovValue.FIELD_ORDER_SEQ, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<LovValue> result = this.lovValueRepository.pageAndSortByLovId(pageRequest, lovId, tenantId, value, meaning);
        return Results.success(result);
    }

    @ApiOperation("条件查询值集头")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/lov-headers")
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<Lov>> listLovHeaders(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            Lov lovHeader,
            @ApiIgnore @SortDefault(value = Lov.FIELD_LOV_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        lovHeader = ObjectUtils.defaultIfNull(lovHeader, new Lov());
        lovHeader.setTenantId(tenantId);
        lovHeader.setSqlTypeControl(true);
        return Results.success(this.lovRepository.pageAndSort(lovHeader, pageRequest));
    }

    @ApiOperation("插入值集头")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/lov-headers")
    public ResponseEntity<Lov> addLovHeader(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @RequestBody Lov lovHeader) {
        this.validObject(lovHeader);
        lovHeader.setTenantId(tenantId);
        Assert.isTrue(BaseConstants.DEFAULT_TENANT_ID.equals(tenantId) || !FndConstants.LovTypeCode.SQL.equals(lovHeader.getLovTypeCode()),
                BaseConstants.ErrorCode.DATA_INVALID);
        return Results.created(this.lovService.addLov(lovHeader));
    }

    @ApiOperation("插入值集值")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/lov-values")
    public ResponseEntity<LovValue> addLovValues(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @RequestBody @Encrypt LovValue lovValue) {
        this.validObject(lovValue);
        lovValue.setTenantId(tenantId);
        this.lovValueService.addLovValue(lovValue);
        return Results.created(lovValue);
    }

    @ApiOperation("更新值集头")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/lov-headers")
    public ResponseEntity<Lov> updateLovHeadersByPrimaryKey(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @RequestBody @Encrypt Lov lovHeader) {
        this.validObject(lovHeader);
        SecurityTokenHelper.validToken(lovHeader);
        boolean tenantAccessFlag = Objects.equals(tenantId, lovHeader.getTenantId()) || Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID);
        Assert.isTrue(tenantAccessFlag, BaseConstants.ErrorCode.DATA_INVALID);
        // 将当前租户Id设置进去，用于校验数据的合法性
        lovHeader.setTenantId(tenantId);
        lovHeader.checkOrgLovTenant(lovRepository);
        return Results.success(this.lovService.updateLov(lovHeader));
    }

    @ApiOperation("更新值集值")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/lov-values")
    public ResponseEntity<LovValue> updateLovValuesByPrimaryKey(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @RequestBody @Encrypt LovValue lovValue) {
        this.validObject(lovValue);
        SecurityTokenHelper.validToken(lovValue);
        boolean tenantAccessFlag = Objects.equals(tenantId, lovValue.getTenantId()) || Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID);
        Assert.isTrue(tenantAccessFlag, BaseConstants.ErrorCode.DATA_INVALID);
        lovValue.setTenantId(tenantId);
        lovValue.checkOrgLovValueTenant(lovValueRepository);
        this.lovValueService.updateLovValue(lovValue);
        return Results.success(lovValue);
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation("批量删除值集头")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/lov-headers")
    public ResponseEntity batchDeleteLovHeadersByPrimaryKey(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @RequestBody @Encrypt List<Lov> lovHeaders) {
        SecurityTokenHelper.validToken(lovHeaders);
        this.lovService.batchDeleteLovHeadersByPrimaryKey(lovHeaders);
        return Results.success();
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation("批量删除值集值")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/lov-values")
    public ResponseEntity batchDeleteLovValuesByPrimaryKey(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @RequestBody @Encrypt List<LovValue> lovValues) {
        SecurityTokenHelper.validToken(lovValues);
        this.lovValueService.batchDeleteLovValuesByPrimaryKey(lovValues);
        return Results.success();
    }

    @ApiOperation("租户级-复制值集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/lov/copy")
    public ResponseEntity<Void> copyLov(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
            @ApiParam(value = "lov编码", required = true) @RequestParam String lovCode,
            @ApiParam(value = "lovId", required = true) @RequestParam @Encrypt Long lovId) {
        lovService.copyLov(tenantId, lovCode, lovId, BaseConstants.Flag.NO);
        return Results.success();
    }

    @ApiOperation("删除值集头")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/lov-headers/delete")
    public ResponseEntity deleteLovHeaderByPrimaryKey(@RequestBody @Encrypt Lov lovHeader) {
        SecurityTokenHelper.validToken(lovHeader);
        lovService.deleteLovHeaderByPrimaryKey(lovHeader);
        return Results.success();
    }

}
