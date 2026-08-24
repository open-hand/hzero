package org.hzero.plugin.platform.hr.api.controller;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.PositionDTO;
import org.hzero.plugin.platform.hr.app.service.PositionService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.repository.PositionRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 组织架构岗位管理接口
 * </p>
 *
 * @author qingsheng.chen 2018/6/20 星期三 13:51
 */
@Api(tags = EnablePlatformHrPlugin.POSITION)
@RestController("positionController.v1")
@RequestMapping("/v1")
public class PositionController extends BaseController {
    private PositionService positionService;
    private PositionRepository positionRepository;

    @Autowired
    public PositionController(PositionService positionService, PositionRepository positionRepository) {
        this.positionService = positionService;
        this.positionRepository = positionRepository;
    }

    @ApiOperation("查询岗位")
    @GetMapping("/{organizationId}/companies/units/positions/{positionId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Position> queryPosition(
                    @PathVariable @ApiParam(value = "岗位ID", required = true) @Encrypt long positionId) {
        return ResponseEntity.ok(positionService.queryPosition(positionId));
    }

    @ApiOperation("查询所有岗位树")
    @GetMapping("/{organizationId}/companies/units/{unitId}/positions")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<PositionDTO>> listPosition(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @PathVariable @ApiParam(value = "部门ID", required = true) @Encrypt long unitId,
                    @RequestParam(required = false) @ApiParam(value = "岗位编码") String positionCode,
                    @RequestParam(required = false) @ApiParam(value = "岗位名称") String positionName) {
        return ResponseEntity.ok(positionService.treePosition(organizationId, unitId, positionCode, positionName));
    }

    @ApiOperation(value = "拉取最近更新过的记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/companies/recent")
    public ResponseEntity<List<Position>> listRecentPosition(
                    @ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
                    @ApiParam("过去多久内(单位：ms，默认5min)") @RequestParam(required = false,
                                    defaultValue = "300000") long before) {
        return Results.success(positionService.listRecentPosition(tenantId, before));
    }

    @ApiOperation("新增岗位")
    @PostMapping("/{organizationId}/companies/{unitCompanyId}/units/{unitId}/position")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Position> createPosition(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @PathVariable @ApiParam(value = "公司ID", required = true) @Encrypt long unitCompanyId,
                    @PathVariable @ApiParam(value = "部门ID", required = true) @Encrypt long unitId,
                    @RequestBody @Encrypt Position position) {
        this.validObject(position);
        return ResponseEntity.status(HttpStatus.CREATED).body(positionService.createPosition(
                        position.setTenantId(organizationId).setUnitCompanyId(unitCompanyId).setUnitId(unitId)));
    }

    @ApiOperation("批量新增岗位")
    @PostMapping("/{organizationId}/companies/{unitCompanyId}/units/{unitId}/positions")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<Position>> batchCreatePosition(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @PathVariable @ApiParam(value = "公司ID", required = true) @Encrypt long unitCompanyId,
                    @PathVariable @ApiParam(value = "部门ID", required = true) @Encrypt long unitId,
                    @RequestBody @Encrypt List<Position> positionList) {
        this.validList(positionList);
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(positionService.batchCreatePosition(organizationId, unitCompanyId, unitId, positionList));
    }

    @ApiOperation("更新岗位")
    @PutMapping("/{organizationId}/companies/{unitCompanyId}/units/{unitId}/positions/{positionId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Position> updatePosition(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @PathVariable @ApiParam(value = "公司ID", required = true) @Encrypt long unitCompanyId,
                    @PathVariable @ApiParam(value = "部门ID", required = true) @Encrypt long unitId,
                    @PathVariable @ApiParam(value = "岗位ID", required = true) @Encrypt long positionId,
                    @RequestBody @Encrypt Position position) {
        SecurityTokenHelper.validToken(position);
        this.validObject(position);
        return ResponseEntity.ok(positionService.updatePosition(position.setPositionId(positionId)
                        .setUnitCompanyId(unitCompanyId).setTenantId(organizationId).setUnitId(unitId)));
    }

    @ApiOperation("批量更新岗位")
    @PutMapping("/{organizationId}/companies/{unitCompanyId}/units/{unitId}/positions")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<Position>> batchUpdatePosition(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @PathVariable @ApiParam(value = "公司ID", required = true) @Encrypt long unitCompanyId,
                    @PathVariable @ApiParam(value = "部门ID", required = true) @Encrypt long unitId,
                    @RequestBody @Encrypt List<Position> positionList) {
        SecurityTokenHelper.validToken(positionList);
        this.validList(positionList);
        return ResponseEntity
                        .ok(positionService.batchUpdatePosition(organizationId, unitCompanyId, unitId, positionList));
    }

    @ApiOperation("启用岗位")
    @PostMapping("/{organizationId}/companies/{unitCompanyId}/units/{unitId}/positions/enable/{positionId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<Position>> enablePosition(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @PathVariable @ApiParam(value = "公司ID", required = true) @Encrypt long unitCompanyId,
                    @PathVariable @ApiParam(value = "部门ID", required = true) @Encrypt long unitId,
                    @PathVariable @ApiParam(value = "岗位ID", required = true) @Encrypt long positionId,
                    @RequestBody @Encrypt Position position) {
        SecurityTokenHelper.validToken(position.setTenantId(organizationId).setUnitCompanyId(unitCompanyId).setUnitId(unitId).setPositionId(positionId));
        Assert.notNull(position.getObjectVersionNumber(), BaseConstants.ErrorCode.OPTIMISTIC_LOCK);
        return ResponseEntity.ok(positionService.enablePosition(position));
    }

    @ApiOperation("禁用岗位")
    @PostMapping("/{organizationId}/companies/{unitCompanyId}/units/{unitId}/positions/disable/{positionId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<Position>> disablePosition(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @PathVariable @ApiParam(value = "公司ID", required = true) @Encrypt long unitCompanyId,
                    @PathVariable @ApiParam(value = "部门ID", required = true) @Encrypt long unitId,
                    @PathVariable @ApiParam(value = "岗位ID", required = true) @Encrypt long positionId,
                    @RequestBody @Encrypt Position position) {
        SecurityTokenHelper.validToken(position.setTenantId(organizationId).setUnitCompanyId(unitCompanyId).setUnitId(unitId).setPositionId(positionId));
        Assert.notNull(position.getObjectVersionNumber(), BaseConstants.ErrorCode.OPTIMISTIC_LOCK);
        return ResponseEntity.ok(positionService.disablePosition(position));
    }

    @ApiOperation("通过岗位编码查询岗位")
    @GetMapping("/{organizationId}/companies/units/positions/position-code")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Position> queryPositionByCode(@PathVariable("organizationId") Long organizationId,
                    @RequestParam String positionCode) {
        return ResponseEntity.ok(positionRepository.queryPositionByCode(positionCode, organizationId));
    }

    @ApiOperation("全量查询租户下的岗位信息")
    @GetMapping("/{organizationId}/companies/tenant/positions/all")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<Position>> queryAllPositions(@PathVariable("organizationId") Long tenantId) {
        return ResponseEntity.ok(positionRepository.queryAllPositionsByTenant(tenantId));
    }
    
    @ApiOperation("新版组织架构-全量查询组织下的岗位信息")
    @GetMapping("/{organizationId}/positions/{unitId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    public ResponseEntity<Page<Position>> queryPositionsByUnit(@PathVariable("organizationId") Long tenantId,
    														   @PathVariable("unitId") @Encrypt(ignoreValue = "0") Long unitId,
    														   @RequestParam(required = false)  @ApiParam(value = "组织编码/组织名称")  String keyWord,
    														   @ApiIgnore @SortDefault(value = Position.FIELD_POSITION_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return ResponseEntity.ok(positionRepository.pagePositionByUnit(pageRequest, tenantId, unitId,keyWord));
    }
    
    @ApiOperation(value = "新版组织架构-树形查询岗位")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", paramType = "path", value = "租户ID", required = true),
    				@ApiImplicitParam(name = "unitCompanyId", paramType = "query", value = "岗位所属公司"),
    				@ApiImplicitParam(name = "unitId", paramType = "query", value = "岗位所属部门"),
    				@ApiImplicitParam(name = "keyWord", paramType = "query", value = "岗位代码/岗位名称")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/positions")
    public ResponseEntity<List<PositionDTO>> listPlusPositionTree(@RequestParam(required = false) String keyWord,
    				@RequestParam(required = false) @Encrypt(ignoreValue = "0") Long unitCompanyId,
    				@RequestParam(required = false) @Encrypt(ignoreValue = "0") Long unitId,
                    @PathVariable("organizationId") Long tenantId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(positionService.selectPlusPositionTree(tenantId,unitCompanyId, unitId,keyWord));
    }

    @ApiOperation("新版组织架构-批量查询岗位下的用户信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/positions/user/{positionId}")
    @CustomPageRequest
    public ResponseEntity<Page<EmployeeDTO>> pageUnitUsers(@ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
    													   @ApiParam(required = true, value = "岗位ID") @PathVariable("positionId") @Encrypt(ignoreValue = "0") Long positionId,
    													   @RequestParam(required = false)  String keyWord,
    													   @RequestParam(required = false)  String status,
    													   @RequestParam(required = false) Integer primaryPositionFlag,
    													   @ApiIgnore @SortDefault(value = Employee.FIELD_EMPLOYEE_NUM, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(positionService.pagePositionUsers(tenantId, positionId, keyWord, status, primaryPositionFlag, pageRequest));
    }
}
