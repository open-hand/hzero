package org.hzero.plugin.platform.hr.api.controller;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.app.service.EmployeeService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 员工表 管理 API
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:43
 */
@Api(tags = EnablePlatformHrPlugin.EMPLOYEE)
@RestController("employeeController.v1")
@RequestMapping("/v1")
public class EmployeeController extends BaseController {
    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @ApiOperation(value = "获取员工信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/employees")
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<Employee>> list(@PathVariable Long organizationId,
                                               @RequestParam(required = false) String employeeNum,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(required = false) Integer enabledFlag,
                                               @RequestParam(required = false) @Encrypt Long userId,
                                               @ApiIgnore @SortDefault(value = "employeeNum", direction = Sort.Direction.ASC) PageRequest pageRequest) {
        return Results.success(employeeService.listEmployee(organizationId, employeeNum, name, enabledFlag, pageRequest, userId));
    }

    @ApiOperation(value = "根据员工编码获取员工信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/employees/employee-num")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Employee> query(@PathVariable Long organizationId, @RequestParam String employeeNum) {
        return Results.success(employeeService.queryEmployee(organizationId, employeeNum));
    }

    @ApiOperation(value = "根据员工ID获取员工信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/employees/id")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Employee> queryEmployee(@PathVariable Long organizationId,
                                                  @RequestParam @Encrypt Long employeeId,
                                                  @RequestParam(required = false) Integer enabledFlag) {
        return Results.success(employeeService.queryEmployee(organizationId, employeeId, enabledFlag));
    }

    @ApiOperation(value = "根据员工ID获取员工信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/employees/ids")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<Employee>> listEmployee(@PathVariable Long organizationId, @RequestParam @Encrypt List<Long> employeeIds) {
        return Results.success(employeeService.listEmployeeByIds(organizationId, employeeIds));
    }

    @ApiOperation(value = "拉取最近更新过的记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/employees/recent")
    public ResponseEntity<List<Employee>> listRecentEmployee(@ApiParam("租户ID") @RequestParam(required = false) Long tenantId,
                                                             @ApiParam("过去多久内(单位：ms，默认5min)") @RequestParam(required = false, defaultValue = "300000") long before) {
        return Results.success(employeeService.listRecentEmployee(tenantId, before));
    }

    @ApiOperation(value = "批量创建或更新员工信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/employees")
    public ResponseEntity<List<Employee>> createOrUpdateEmployee(@PathVariable Long organizationId,
                                                                 @RequestBody @Encrypt List<Employee> employeeList) {
        this.validList(employeeList);
        return Results.success(employeeService.createOrUpdateEmployee(organizationId, employeeList));

    }
    
    @ApiOperation("新版组织架构-新增员工详情")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/plus/employees")
    public ResponseEntity<EmployeeDTO> createEmployee(@ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                                                      @RequestBody @Encrypt EmployeeDTO employeeDTO) {
        return Results.success(employeeService.insertEmployeeDetail(organizationId, employeeDTO));
    }
    
    @ApiOperation("新版组织架构-修改员工详情")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping({"/{organizationId}/plus/employees"})
    public ResponseEntity<EmployeeDTO> updateEmployee(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                      @RequestBody @Encrypt EmployeeDTO employeeDTO) {
        return Results.success(employeeService.updateEmployeeDetail(organizationId,employeeDTO));
    }
    
    @ApiOperation("新版组织架构-查看员工详情")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/{organizationId}/plus/employees/{employeeId}"})
    public ResponseEntity<EmployeeDTO> employeeDetail(@ApiParam(value = "租户ID", required = true) @PathVariable(name = "organizationId") Long organizationId,
                                                      @ApiParam(value = "员工ID", required = true) @PathVariable(name = "employeeId") @Encrypt(ignoreValue = "0") Long employeeId) {

        return Results.success(employeeService.queryEmployeeDetail(organizationId, employeeId));
    }

    @ApiOperation(value = "更新员工信息和主岗信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/employees/employee-position")
    public ResponseEntity<Employee> updateEmployeeAndAssign(@PathVariable Long organizationId,
                                                            @RequestParam(required = false) @Encrypt Long positionId,
                                                            @RequestBody @Encrypt Employee employee) {
        return Results.success(employeeService.updateEmployeeAndAssign(organizationId, positionId, employee));
    }

    @ApiOperation(value = "批量查询所有有效员工")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/{organizationId}/employees/employee/all")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<Employee>> listAllEnableEmployee(
            @ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        List<Employee> list = employeeService.selectEmployee(tenantId);
        return Results.success(list);
    }

    @ApiOperation("查询指定角色关联的员工列表 -- 用于工作流审批规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/role-list")
    @CustomPageRequest
    public ResponseEntity<List<String>> queryEmployees(@ApiParam(required = true, value = "租户ID") @PathVariable("organizationId") Long tenantId,
                                                       @ApiParam(required = true, value = "租户ID") @RequestParam Long roleId) {
        return Results.success(employeeService.listEmployeeByRoleCodeId(tenantId, roleId));
    }
}
