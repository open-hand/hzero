package org.hzero.plugin.platform.hr.api.controller;

import java.util.Date;

import javax.validation.Valid;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.plugin.platform.hr.app.service.HrSyncLogService;
import org.hzero.plugin.platform.hr.app.service.HrSyncService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.plugin.platform.hr.domain.entity.HrSync;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncLog;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
import springfox.documentation.annotations.ApiIgnore;

/**
 * hr基础数据同步外部系统 管理 API
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
@Api(tags = {EnablePlatformHrPlugin.HR_SYNC})
@RestController("hrSyncController.v1")
@RequestMapping("/v1/{organizationId}/hr-syncs")
public class HrSyncController extends BaseController {

    @Autowired
    private HrSyncService hrSyncService;
    @Autowired
    private HrSyncLogService hrSyncLogService;

    @ApiOperation(value = "hr基础数据同步外部系统列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<HrSync>> list(@PathVariable Long organizationId, HrSync hrSync,
                                             @ApiIgnore @SortDefault(value = HrSync.FIELD_SYNC_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(hrSyncService.pageAndSort(pageRequest, organizationId, hrSync));
    }

    @ApiOperation(value = "hr基础数据同步外部系统明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("detail/{syncId}")
    public ResponseEntity<HrSync> detail(@PathVariable Long organizationId, @PathVariable @Encrypt Long syncId) {
        return Results.success(hrSyncService.selectHrSync(syncId));
    }

    @ApiOperation(value = "创建hr基础数据同步外部系统")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<HrSync> create(@PathVariable Long organizationId, @RequestBody HrSync hrSync) {
        validObject(hrSync);
        return Results.success(hrSyncService.insertHrSync(hrSync));
    }

    @ApiOperation(value = "修改hr基础数据同步外部系统")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<HrSync> update(@PathVariable Long organizationId, @RequestBody @Encrypt HrSync hrSync) {
        SecurityTokenHelper.validToken(hrSync);
        return Results.success(hrSyncService.updateHrSync(hrSync));
    }

    @ApiOperation(value = "删除hr基础数据同步外部系统")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable Long organizationId, @RequestBody @Encrypt HrSync hrSync) {
        SecurityTokenHelper.validToken(hrSync);
        hrSyncService.deleteHrSync(hrSync);
        return Results.success();
    }

    @ApiOperation(value = "第三方组织信息同步到外部")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping({"/do"})
    public ResponseEntity<Void> syncNow(@PathVariable Long organizationId, @RequestBody @Valid @Encrypt HrSync hrSyncDto,
                                        @ApiParam(name = "是否使用第三方生成的部门id") @RequestParam(required = false, defaultValue = "true") Boolean useGeneratedUnitId) {
        hrSyncDto.setTenantId(organizationId);
        hrSyncService.syncNow(hrSyncDto, useGeneratedUnitId);
        return Results.success();
    }

    @ApiOperation(value = "hr基础数据同步外部日志列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{syncId}")
    @CustomPageRequest
    public ResponseEntity<Page<HrSyncLog>> logList(@PathVariable Long organizationId,
                                                   @PathVariable @Encrypt Long syncId,
                                                   @RequestParam(required = false) Date startDate,
                                                   @RequestParam(required = false) Date endDate,
                                                   @ApiIgnore @SortDefault(value = HrSyncLog.FIELD_SYNC_LOG_ID,
                                                           direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(hrSyncLogService.listHrSyncLog(syncId, startDate, endDate, pageRequest));
    }

    @ApiOperation(value = "hr基础数据同步外部日志详情")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/logs/{logId}"})
    public ResponseEntity<?> logDetail(@PathVariable Long organizationId, @PathVariable("logId") @Encrypt Long logId) {
        return hrSyncService.logDetail(logId);
    }

    @ApiOperation(value = "第三方组织信息同步到平台")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/local")
    public ResponseEntity<Void> syncToLocal(@PathVariable Long organizationId, @RequestBody @Valid @Encrypt HrSync hrSyncDto){
        hrSyncService.syncToLocal(organizationId, hrSyncDto);
        return Results.success();

    }
}
