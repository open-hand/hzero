package org.hzero.plugin.platform.hr.api.controller;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.app.service.EmployeeAssignService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeAssign;
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
 * 员工岗位分配表 管理 API
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:44
 */
@Api(tags = EnablePlatformHrPlugin.EMPLOYEE_ASSIGN)
@RestController("employeeAssignController.v1")
@RequestMapping("/v1/{organizationId}/employee-assigns")
public class EmployeeAssignController extends BaseController {
    private EmployeeAssignService employeeAssignService;

    @Autowired
    public EmployeeAssignController(EmployeeAssignService employeeAssignService) {
        this.employeeAssignService = employeeAssignService;
    }

    @ApiOperation("查询当前员工分配的岗位")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping
    public ResponseEntity<List<EmployeeAssignDTO>> listEmployeeAssign(
                    @PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                    @RequestParam @ApiParam(value = "员工ID", required = true) @Encrypt long employeeId) {
        return Results.success(employeeAssignService.listEmployeeAssign(organizationId, employeeId));
    }

    @ApiOperation(value = "批量更新员工岗位分配信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity updateEmployeeAssign(@PathVariable Long organizationId, @RequestParam @Encrypt Long employeeId,
                    @RequestBody @Encrypt List<EmployeeAssign> assignList) {
        this.validList(assignList);
        employeeAssignService.updateEmployeeAssign(organizationId, employeeId, assignList);
        return Results.success();
    }


    @ApiOperation(value = "批量新增该岗位下的员工")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/position")
    public ResponseEntity<List<Long>> batchCreateEmployee(@PathVariable Long organizationId, @RequestParam @Encrypt Long unitId,
                    @RequestParam @Encrypt Long positionId, @RequestBody @Encrypt List<Long> employeeIdList) {
        employeeAssignService.batchCreateEmployee(organizationId, unitId, positionId, employeeIdList);
        return Results.success(employeeIdList);
    }

    @ApiOperation(value = "批量删除该岗位下的员工")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/position")
    public ResponseEntity<List<Long>> batchDeleteEmployee(@PathVariable Long organizationId, @RequestParam @Encrypt Long unitId,
                    @RequestParam @Encrypt Long positionId, @RequestBody @Encrypt List<Long> employeeIdList) {
        employeeAssignService.batchDeleteEmployee(organizationId, unitId, positionId, employeeIdList);
        return Results.success(employeeIdList);
    }


    @ApiOperation(value = "不在该岗位下的员工")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/not-in-position")
    @CustomPageRequest
    public ResponseEntity<Page<Employee>> employeesNotInPosition(@PathVariable Long organizationId,
                    @RequestParam(required = false) String employeeNum, @RequestParam(required = false) String name,
                    @RequestParam @Encrypt Long unitId, @RequestParam @Encrypt Long positionId,
                    @ApiIgnore @SortDefault(value = "employeeId",
                                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(employeeAssignService.employeeNotInPosition(organizationId, employeeNum, name, unitId,
                        positionId, pageRequest));
    }

    @ApiOperation(value = "在该岗位下的员工")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/in-position")
    @CustomPageRequest
    public ResponseEntity<Page<Employee>> employeestInPosition(@PathVariable Long organizationId,
                    @RequestParam(required = false) String employeeNum, @RequestParam(required = false) String name,
                    @RequestParam @Encrypt Long unitId, @RequestParam @Encrypt Long positionId,
                    @ApiIgnore @SortDefault(value = "employeeId",
                                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(employeeAssignService.employeeInPosition(organizationId, employeeNum, name, unitId,
                        positionId, pageRequest));
    }


    @ApiOperation(value = "查询当前员工的岗位")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/employee-assign")
    public ResponseEntity<List<EmployeeAssignDTO>> getEmployeeAssign(
                    @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
                    @RequestParam(required = false) @ApiParam(value = "主岗位标示") Integer primaryPositionFlag,
                    @RequestParam(required = false) @ApiParam(value = "用户") @Encrypt Long userId,
                    @RequestParam(required = false) @ApiParam(value = "语言") String language) {
        return Results.success(
                        employeeAssignService.listEmployeeAssign(tenantId, primaryPositionFlag, userId, language));
    }

}
