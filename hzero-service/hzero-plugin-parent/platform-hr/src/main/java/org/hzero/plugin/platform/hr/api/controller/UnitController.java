package org.hzero.plugin.platform.hr.api.controller;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.api.dto.Receiver;
import org.hzero.plugin.platform.hr.api.dto.UnitDTO;
import org.hzero.plugin.platform.hr.api.dto.UnitUserDTO;
import org.hzero.plugin.platform.hr.app.service.UnitService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.plugin.platform.hr.domain.repository.UnitRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

/**
 * 部门 管理 API
 *
 * @author gaokuo.dai@hand-china.com 2018-06-21 17:05:02
 */
@Api(tags = EnablePlatformHrPlugin.UNIT)
@RestController("unitController.v1")
@RequestMapping("/v1")
public class UnitController extends BaseController {
    private UnitRepository unitRepository;
    private UnitService unitService;

    @Autowired
    public UnitController(UnitRepository unitRepository, UnitService unitService) {
        this.unitRepository = unitRepository;
        this.unitService = unitService;
    }

    @ApiOperation(value = "根据组织ID查询组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/{unitId}")
    public ResponseEntity<Unit> listCompanyUnitByOrgId(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "组织ID") @PathVariable @Encrypt Long unitId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(unitId, BaseConstants.ErrorCode.DATA_INVALID);
        Unit queryParam = new Unit();
        queryParam.setUnitId(unitId);
        queryParam.setTenantId(tenantId);
        return Results.success(unitRepository.selectOne(queryParam));
    }

    @ApiOperation(value = "查询公司组织信息列表（非树形）")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/company-list")
    public ResponseEntity<Page<Unit>> listCompanyUnits(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "组织ID") @RequestParam(required = false) @Encrypt Long unitId,
            @ApiParam(required = true, value = "组织编码") @RequestParam(required = false) String unitCode,
            @ApiParam(required = true, value = "组织名称") @RequestParam(required = false) String unitName,
            PageRequest pageRequest) {
        return Results.success(unitService.listCompanyUnits(tenantId, unitId, unitCode, unitName, pageRequest));
    }

    @ApiOperation(value = "查询部门组织信息列表（非树形）")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/department-list")
    public ResponseEntity<Page<Unit>> listDepartmentUnits(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "部门所属公司Id") @RequestParam @Encrypt Long unitCompanyId,
            @ApiParam(required = true, value = "组织ID") @RequestParam @Encrypt Long unitId,
            @ApiParam(required = true, value = "部门编码") @RequestParam(required = false) String unitCode,
            @ApiParam(required = true, value = "部门名称") @RequestParam(required = false) String unitName,
            PageRequest pageRequest) {
        return Results.success(unitService.listDepartmentUnits(tenantId, unitCompanyId, unitId, unitCode, unitName, pageRequest));
    }

    @ApiOperation(value = "查询公司级组织列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", paramType = "path", value = "租户ID", required = true),
            @ApiImplicitParam(name = "unitCode", paramType = "query", value = "部门代码"),
            @ApiImplicitParam(name = "unitName", paramType = "query", value = "部门名称"),
            @ApiImplicitParam(name = "enabledFlag", paramType = "query", value = "是否启用")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/company")
    public ResponseEntity<List<UnitDTO>> listCompanyUnit(@Encrypt Unit queryParam,
                                                         @PathVariable("organizationId") Long tenantId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        queryParam = ObjectUtils.defaultIfNull(queryParam, new Unit());
        queryParam.setTenantId(tenantId);
        return Results.success(unitService.selectCompany(queryParam));
    }

    @ApiOperation(value = "树形查询所有公司级组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/company/tree")
    public ResponseEntity<List<UnitDTO>> treeCompanyUnit(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(unitService.treeCompany(tenantId));
    }

    @ApiOperation(value = "树形懒加载查询公司级组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/company/lazy-tree")
    public ResponseEntity<List<UnitDTO>> lazyTreeCompanyUnit(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @RequestParam(value = "unitId", required = false) @Encrypt Long unitId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(unitService.lazyTreeCompany(tenantId, unitId));
    }

    @ApiOperation(value = "分页查询组织信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", paramType = "path", value = "租户ID", required = true),
            @ApiImplicitParam(name = "unitCode", paramType = "query", value = "组织代码"),
            @ApiImplicitParam(name = "unitName", paramType = "query", value = "组织名称"),
            @ApiImplicitParam(name = "unitTypeCode", paramType = "query", value = "组织类型")})
    @GetMapping("/{organizationId}/units/list")
    public ResponseEntity<Page<UnitDTO>> pageCompanyUnits(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @Encrypt Unit unit, PageRequest pageRequest) {
        unit.setTenantId(tenantId);
        return Results.success(unitService.pageCompanyUnits(unit, pageRequest));
    }

    @ApiOperation(value = "查询部门级组织列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", paramType = "path", value = "租户ID", required = true),
            @ApiImplicitParam(name = "unitCode", paramType = "query", value = "部门代码"),
            @ApiImplicitParam(name = "unitName", paramType = "query", value = "部门名称"),
            @ApiImplicitParam(name = "unitCompanyId", paramType = "query", value = "部门公司Id", required = true),
            @ApiImplicitParam(name = "enabledFlag", paramType = "query", value = "是否启用")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/department")
    public ResponseEntity<List<UnitDTO>> listDepartmentUnit(@PathVariable("organizationId") Long tenantId,
            @Encrypt Unit queryParam) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        queryParam = ObjectUtils.defaultIfNull(queryParam, new Unit());
        queryParam.setTenantId(tenantId);
        Assert.notNull(queryParam.getUnitCompanyId(), BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(unitService.selectDepartment(queryParam));
    }

    @ApiOperation(value = "树形查询所有部门级组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/department/tree")
    public ResponseEntity<List<UnitDTO>> treeDepartmentUnit(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "组织架构公司ID") @RequestParam @Encrypt Long unitCompanyId,
            @ApiParam("是否启用") @RequestParam(required = false) Integer enabledFlag) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(unitCompanyId, BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(unitService.treeDepartment(tenantId, unitCompanyId, enabledFlag));
    }

    @ApiOperation(value = "拉取最近更新过的记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/units/recent")
    public ResponseEntity<List<Unit>> listRecentUnit(@ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
                                                     @ApiParam("过去多久内(单位：ms，默认5min)") @RequestParam(required = false,
                                                             defaultValue = "300000") long before) {
        return Results.success(unitService.listRecentUnit(tenantId, before));
    }

    @ApiOperation(value = "批量创建部门")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/units")
    public ResponseEntity<List<Unit>> batchCreate(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "待插入的数据") @RequestBody @Encrypt List<Unit> units) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.isTrue(CollectionUtils.isNotEmpty(units), BaseConstants.ErrorCode.DATA_INVALID);
        this.validList(units);
        units.forEach(unit -> {
            unit.setTenantId(tenantId);
            unit.generateQuickIndexAndPinyin();
        });
        return Results.success(unitService.batchInsertSelective(units));
    }

    @ApiOperation(value = "修改组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/units")
    public ResponseEntity<Unit> update(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "待修改的数据") @RequestBody @Encrypt Unit unit) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        this.validObject(unit);
        unit.setTenantId(tenantId);
        unit.generateQuickIndexAndPinyin();
        unitService.updateByPrimaryKey(unit);
        return Results.success(unit);
    }

    @ApiOperation(value = "禁用组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/units/disable")
    public ResponseEntity<Long> disableUnit(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "组织信息,包含组织ID和版本号") @RequestBody @Encrypt Unit unit) {
        Assert.notNull(unit, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(unit.getUnitId(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(unit.getObjectVersionNumber(), BaseConstants.ErrorCode.DATA_INVALID);
        unitService.disableUnit(unit.getUnitId(), tenantId, unit.getObjectVersionNumber());
        return Results.success(unit.getObjectVersionNumber() + 1);
    }

    @ApiOperation(value = "启用组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/units/enable")
    public ResponseEntity<Long> enableUnit(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(required = true, value = "组织信息,包含组织ID和版本号") @RequestBody @Encrypt Unit unit) {
        Assert.notNull(unit, BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(unit.getUnitId(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(unit.getObjectVersionNumber(), BaseConstants.ErrorCode.DATA_INVALID);
        this.unitService.enableUnit(unit.getUnitId(), tenantId, unit.getObjectVersionNumber());
        return Results.success(unit.getObjectVersionNumber() + 1);
    }


    @ApiOperation(value = "批量查询所有部门")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/department/all")
    public ResponseEntity<List<UnitDTO>> listAllDepartment(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @ApiParam(value = "是否有效") @RequestParam(name = "enabledFlag",
                    required = false) Integer enabledFlag) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        List<UnitDTO> list = this.unitService.selectAllDepartment(tenantId, enabledFlag);
        return Results.success(list);
    }


    @ApiOperation(value = "批量查询部门", notes = "parentUnitIds和unitIds两个查询条件是or关系，换而言之就是查询的是其并集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/department/batch")
    public ResponseEntity<?> listDepartment(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
            @RequestParam(value = "parentUnitIds", required = false) @Encrypt List<Long> parentIds,
            @RequestParam(value = "unitIds", required = false) @Encrypt List<Long> unitIds) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        List<UnitDTO> list = this.unitService.selectDepartment(tenantId, parentIds, unitIds);
        return Results.success(list);
    }

    @ApiOperation(value = "按类型查询部门")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/type-department")
    public ResponseEntity<List<UnitDTO>> selectByTypeCodes(
            @RequestParam(value = "typeCodes", required = false) String[] typeCodes,
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId) {
        List<UnitDTO> list = unitService.selectByTypeCodes(typeCodes, tenantId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation("批量查询部门及其子部门下的用户id信息-用于消息公告管理界面")
    @Permission(permissionWithin = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "includeChildDepartment", value = "是否包含子部门，默认包含", paramType = "query",
                    dataType = "boolean"),
            @ApiImplicitParam(name = "typeCode", value = "返回参数的种类,默认返回userId及其所属租户，参数种类维护在ReceiverTypeCode枚举中",
                    paramType = "query")})
    @PostMapping("/units/user-ids")
    public ResponseEntity<Set<Receiver>> listDepartmentUsers(@RequestBody List<UnitUserDTO> units,
                                                             @RequestParam(required = false, defaultValue = "true") boolean includeChildDepartment,
                                                             @RequestParam(required = false) List<String> typeCode) {
        return Results.success(unitRepository.listDepartmentUsers(units, includeChildDepartment, typeCode));
    }

    @ApiOperation("查询部门以及子部门下用户对应的第三方平台部门下的员工")
    @Permission(permissionWithin = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "thirdPlatformType", value = "第三方平台类型",
                    paramType = "query")})
    @PostMapping("/units/open/user-ids")
    public ResponseEntity<Set<Receiver>> listHrDepartmentUsers(@RequestBody List<UnitUserDTO> units,
                                                               @RequestParam(required = false, defaultValue = "true") boolean includeChildDepartment,
                                                               @RequestParam String thirdPlatformType) {
        return Results.success(unitService.listOpenDepartmentUsers(units, includeChildDepartment, thirdPlatformType));
    }

    @ApiOperation(value = "通过部门Id树形查询部门级组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "unitIds", value = "部门Id", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "是否启用", paramType = "query")
    })
    @GetMapping("/{organizationId}/units/department/tree/by-condition")
    public ResponseEntity<List<UnitDTO>> treeDepartmentUnitByCondition(@PathVariable("organizationId") Long tenantId,
                                                                       @RequestParam(required = false) @Encrypt List<Long> unitIds, @RequestParam(required = false) Integer enabledFlag) {
        return Results.success(unitRepository.treeDepartmentByCondition(tenantId, unitIds, enabledFlag));
    }

    @ApiOperation(value = "通过部门Id查询部门级组织列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true),
            @ApiImplicitParam(name = "unitIds", value = "部门Id", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "是否启用", paramType = "query")
    })
    @GetMapping("/{organizationId}/units/department/list/by-condition")
    public ResponseEntity<List<UnitDTO>> listDepartmentUnitByCondition(@PathVariable("organizationId") Long tenantId,
                                                                       @RequestParam(required = false) @Encrypt List<Long> unitIds, @RequestParam(required = false) Integer enabledFlag) {
        return Results.success(unitRepository.listDepartmentByCondition(tenantId, unitIds, enabledFlag));
    }

    @ApiOperation(value = "新版组织架构树形查询公司级组织")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", paramType = "path", value = "租户ID", required = true),
            @ApiImplicitParam(name = "keyWord", paramType = "query", value = "部门代码/部门名称")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/plus/company")
    public ResponseEntity<List<UnitDTO>> listPlusCompanyUnit(@RequestParam(required = false) String keyWord,
                                                             @PathVariable("organizationId") Long tenantId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(unitService.selectPlusCompany(keyWord, tenantId));
    }

    @ApiOperation(value = "新版组织架构树形查询部门级组织")
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", paramType = "path", value = "租户ID", required = true),
            @ApiImplicitParam(name = "unitCompanyId", paramType = "query", value = "所属公司"),
            @ApiImplicitParam(name = "keyWord", paramType = "query", value = "部门代码/部门名称")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/plus/department")
    public ResponseEntity<List<UnitDTO>> listPlusDepartmentUnit(@RequestParam(required = false) String keyWord,
                                                                @RequestParam(required = false) @Encrypt(ignoreValue = "0") Long unitCompanyId,
                                                                @PathVariable("organizationId") Long tenantId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(unitService.selectPlusDepartment(keyWord, unitCompanyId, tenantId));
    }

    @ApiOperation("批量查询部门及其子部门下的用户信息-用于新版组织架构界面")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/plus/user/{unitId}")
    @CustomPageRequest
    public ResponseEntity<Page<EmployeeDTO>> pageUnitUsers(@ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
                                                           @ApiParam(required = true, value = "组织ID") @PathVariable("unitId") @Encrypt(ignoreValue = "0") Long unitId,
                                                           @RequestParam(required = false) @ApiParam(value = "员工姓名/工号") String keyWord,
                                                           @RequestParam(required = false) @ApiParam(value = "员工状态") String status,
                                                           @RequestParam(required = false) Integer primaryPositionFlag,
                                                           @ApiIgnore @SortDefault(value = Employee.FIELD_EMPLOYEE_NUM, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(unitService.pageUnitUsers(tenantId, unitId, keyWord, status, primaryPositionFlag, pageRequest));
    }

    @ApiOperation("批量查询组织下的部门信息-用于新版组织架构公司界面")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/units/plus/department/{unitId}")
    @CustomPageRequest
    public ResponseEntity<Page<UnitDTO>> pageUnitDepartment(@ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
                                                            @ApiParam(required = true, value = "组织ID") @PathVariable("unitId") @Encrypt(ignoreValue = "0") Long unitId,
                                                            @RequestParam(required = false) @ApiParam(value = "部门编码/部门名称") String keyWord,
                                                            @RequestParam(required = false) Integer enabledFlag,
                                                            @ApiIgnore @SortDefault(value = Employee.FIELD_EMPLOYEE_NUM, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(unitService.pageUnitDept(tenantId, unitId, keyWord, enabledFlag, pageRequest));
    }
}
