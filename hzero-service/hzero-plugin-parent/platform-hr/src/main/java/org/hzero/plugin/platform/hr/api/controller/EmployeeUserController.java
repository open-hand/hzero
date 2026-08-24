package org.hzero.plugin.platform.hr.api.controller;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.plugin.platform.hr.api.dto.EmployeeUserDTO;
import org.hzero.plugin.platform.hr.app.service.EmployeeUserService;
import org.hzero.plugin.platform.hr.config.EnablePlatformHrPlugin;
import org.hzero.plugin.platform.hr.domain.entity.EmployeeUser;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeUserRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;

/**
 * 员工用户关系 管理 API
 *
 * @author like.zhang@hand-china.com 2018-09-17 11:20:55
 */
@Api(tags = {EnablePlatformHrPlugin.EMPLOYEE_USER})
@RestController("employeeUserController.v1")
@RequestMapping("/v1/{organizationId}/employee-users")
public class EmployeeUserController extends BaseController {
    private EmployeeUserRepository employeeUserRepository;
    private EmployeeUserService employeeUserService;

    @Autowired
    public EmployeeUserController(EmployeeUserRepository employeeUserRepository, EmployeeUserService employeeUserService) {
        this.employeeUserRepository = employeeUserRepository;
        this.employeeUserService = employeeUserService;
    }

    @ApiOperation(value = "员工用户关系列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "employeeId", value = "员工id", paramType = "query", required = true)
    })
    public ResponseEntity<List<EmployeeUserDTO>> pageEmployeeUser(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                  @Encrypt Long employeeId) {
        return Results.success(employeeUserRepository.listByEmployeeId(organizationId, employeeId));
    }

    @ApiOperation("查询用户在某个租户下的员工信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/employee")
    public ResponseEntity<EmployeeUserDTO> getEmployee(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                       @ApiParam("是否启用") @RequestParam(required = false) Integer enabledFlag,
                                                       @ApiParam(hidden = true) @RequestParam(required = false) @Encrypt Long userId) {
        return Results.success(employeeUserService.getEmployee(userId == null ? DetailsHelper.getUserDetails().getUserId() : userId, organizationId, enabledFlag));
    }

    @ApiOperation("批量查询查询用户在某个租户下的员工信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/employee/batch")
    public ResponseEntity<List<EmployeeUserDTO>> listEmployee(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                              @RequestParam @ApiParam(value = "用户ID列表", required = true) @Encrypt List<Long> userIdList) {
        return Results.success(employeeUserService.listEmployee(userIdList, organizationId));
    }


    @ApiOperation(value = "批量新增员工用户关系")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<EmployeeUser>> batchInsertEmployees(@RequestBody @Encrypt List<EmployeeUser> employeeUsers) {
        validList(employeeUsers);
        return Results.success(employeeUserService.batchInsertEmployees(employeeUsers));
    }

    @ApiOperation(value = "批量删除员工用户关系")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity batchRemoveEmployeeUsers(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                   @RequestBody @Encrypt List<EmployeeUser> employeeUsers) {
        employeeUserService.batchRemoveEmployeeUsers(organizationId, employeeUsers);
        return Results.success();
    }

    @ApiOperation("通过租户id和员工编码查询用户在某个租户下的员工信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true)
    })
    @GetMapping("/employee-num")
    public ResponseEntity<List<EmployeeUserDTO>> getEmpUsersByEmpNum(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                     @RequestParam("employeeNums") List<String> employeeNums) {
        return Results.success(employeeUserRepository.listByEmployeeNum(organizationId, employeeNums));
    }

    @ApiOperation("通过用户id获取员工在第三方平台的id")
    @Permission(permissionWithin = true)
    @GetMapping("/open-userid")
    public ResponseEntity<List<String>> getOpenUserId(@RequestParam("ids") List<Long> ids, @RequestParam("thirdPlatformType") String thirdPlatformType) {
        return Results.success(employeeUserService.getOpenUserIdByIds(ids, thirdPlatformType));
    }
}
