package org.hzero.plugin.platform.hr.api.controller;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.*;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.plugin.platform.hr.api.dto.EmployeeDTO;
import org.hzero.plugin.platform.hr.app.service.HrService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.plugin.platform.hr.domain.entity.Employee;
import org.hzero.plugin.platform.hr.domain.entity.Position;
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

/**
 * <p>
 * 工作流组织架构接口API
 * </p>
 *
 * @author qingsheng.chen 2018/8/13 星期一 14:45
 */
@Api(tags = EnablePlatformHrPlugin.WORKFLOW_HR)
@RestController("hrController.v1")
@RequestMapping("v1/{organizationId}/hr/")
public class HrController extends BaseController {
    private HrService hrService;

    @Autowired
    public HrController(HrService hrService) {
        this.hrService = hrService;
    }

    @ApiOperation("通过员工编号查找上级领导,用于审批规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("employee/superior-num")
    public ResponseEntity<List<String>> listEmployeeSuperiorNum(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                @RequestParam(name = "employeeCode") @ApiParam(value = "员工编号", required = true) String employeeNum) {
        return Results.success(hrService.listEmployeeSuperiorNum(organizationId, employeeNum));
    }

    @ApiOperation("通过岗位编码查询员工，用于审批规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("position/employee-num")
    public ResponseEntity<List<String>> listPositionEmployeeNum(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                @RequestParam @ApiParam(value = "岗位编码", required = true) String positionCode) {
        return Results.success(hrService.listPositionEmployeeNum(organizationId, positionCode));
    }

    @ApiOperation("通过员工编号查找部门领导，用于审批规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("department/superior-num")
    public ResponseEntity<List<String>> listDepartmentSuperiorNum(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                  @RequestParam(name = "employeeCode") @ApiParam(value = "员工编号", required = true) String employeeNum) {
        return Results.success(hrService.listDepartmentSuperiorNum(organizationId, employeeNum));
    }

    @ApiOperation("通过员工编号查找上级部门领导，用于审批规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("superior-department/superior-num")
    public ResponseEntity<List<String>> listSuperiorDepartmentSuperiorNum(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                          @RequestParam(name = "employeeCode") @ApiParam(value = "员工编号", required = true) String employeeNum) {
        return Results.success(hrService.listSuperiorDepartmentSuperiorNum(organizationId, employeeNum));
    }


    @ApiOperation("通过员工编号查找岗位列表(原描述：通过员工编号获取用户的权限控制分配条件，用于权限控制)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("employee/position-code")
    public ResponseEntity<Map<String, Object>> mapEmployeePositionCode(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                       @RequestParam(name = "employeeCode") @ApiParam(value = "员工编号", required = true) String employeeNum) {
        return Results.success(hrService.mapEmployeePositionCode(organizationId, employeeNum));
    }

    @ApiOperation("通过员工编码查询员工，用于流程内部调用")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("employee/details")
    public ResponseEntity<EmployeeDTO> queryEmployeeDetail(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                           @RequestParam(name = "employeeCode") @ApiParam(value = "员工编号", required = true) String employeeNum) {
        return Results.success(hrService.queryEmployeeDetail(organizationId, employeeNum));
    }

    @ApiOperation("通过员工编码查询员工详细信息，用于流程内部调用")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("employee/all-details")
    public ResponseEntity<EmployeeDTO> queryEmployeeAllDetail(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                              @RequestParam(name = "employeeCode") @ApiParam(value = "员工编号", required = true) String employeeNum) {
        return Results.success(hrService.queryEmployeeAllDetail(organizationId, employeeNum));
    }

    @ApiOperation("通过员工编码查询岗位，用于流程内部调用")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("employee/position")
    public ResponseEntity<List<Position>> listEmployeePosition(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                               @RequestParam(name = "employeeCode") @ApiParam(value = "员工编号", required = true) String employeeNum) {
        return Results.success(hrService.listEmployeePosition(organizationId, employeeNum));
    }

    @ApiOperation("通过岗位编码查询岗位，用于流程内部调用")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("position/details")
    public ResponseEntity<Position> queryPositionDetails(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                         @RequestParam @ApiParam(value = "岗位编码", required = true) String positionCode) {
        return Results.success(hrService.queryPositionDetails(organizationId, positionCode));
    }

    @ApiOperation("通过多个员工编码批量查询员工信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true)
    })
    @PostMapping("employee/details/by-nums")
    public ResponseEntity<List<EmployeeDTO>> queryEmployeesByEmployeeNum(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                         @RequestParam("employeeNums") List<String> employeeNums) {
        return Results.success(hrService.queryEmployeesByEmployeeNum(organizationId, employeeNums));
    }

    @ApiOperation("查询用户列表，用于指定人页面查询API")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("employee")
    @CustomPageRequest
    public ResponseEntity<Page<EmployeeDTO>> pageEmployee(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                          @ApiIgnore @SortDefault(value = Employee.FIELD_EMPLOYEE_NUM, direction = Sort.Direction.ASC) PageRequest pageRequest,
                                                          @RequestBody @ApiParam(value = "员工查询条件", required = true) EmployeeDTO employee) {
        return Results.success(hrService.pageEmployee(organizationId, pageRequest, employee));
    }

    @ApiOperation("条件查询员工列表。用于转交页面")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("employee/list-details")
    @CustomPageRequest
    public ResponseEntity<Page<EmployeeDTO>> pageEmployeeAllDetail(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                   @ApiIgnore @SortDefault(value = Employee.FIELD_EMPLOYEE_NUM, direction = Sort.Direction.ASC) PageRequest pageRequest,
                                                                   @RequestBody @ApiParam(value = "员工查询条件", required = true) EmployeeDTO employee) {
        return Results.success(hrService.pageEmployeeAllDetail(organizationId, pageRequest, employee));
    }

    @ApiOperation("查询岗位列表，用于指定岗位页面查询API")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("position")
    @CustomPageRequest
    public ResponseEntity<Page<Position>> pagePosition(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                       @ApiIgnore @SortDefault(value = Position.FIELD_ORDER_SEQ) PageRequest pageRequest,
                                                       @RequestBody @ApiParam(value = "岗位查询条件", required = true) Position position) {
        return Results.success(hrService.pagePosition(organizationId, pageRequest, position));
    }

    @ApiOperation("查询某个部门下的员工")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/department/employee")
    @CustomPageRequest
    public ResponseEntity<Page<EmployeeDTO>> pageDepartmentEmployee(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                    @RequestParam(required = false) @ApiParam("部门ID,与部门编码不可同时为空") Long departmentId,
                                                                    @RequestParam(required = false) @ApiParam("部门编码,与部门ID不可同时为空") String departmentCode,
                                                                    @RequestParam(required = false, defaultValue = "false") @ApiParam("是否包含子部门，默认不包含") boolean includeChildDepartment,
                                                                    @ApiIgnore @SortDefault(value = Employee.FIELD_EMPLOYEE_NUM, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Assert.isTrue(departmentId != null || departmentCode != null, BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(hrService.pageDepartmentEmployee(organizationId, departmentId, departmentCode, includeChildDepartment, pageRequest));
    }
}
