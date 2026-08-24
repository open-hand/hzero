package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.iam.app.service.HrUnitService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.HrUnit;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 *
 * @author mingwei.liu@hand-china.com 2018/7/15
 */
@Api(tags = SwaggerApiConfig.HR_UNIT)
@RestController("hrUnitController.v1")
@RequestMapping("/hzero/v1")
public class HrUnitController {

    @Autowired
    private HrUnitService hrUnitService;

    @Permission(permissionLogin = true)
    @ApiOperation("查询用户在某租户下所属的组织树")
    @GetMapping("/{organizationId}/hrunits/tree")
    public ResponseEntity<List<HrUnit>> listWholeHrUnitTreeOfTenant(@PathVariable(name = "organizationId") Long organizationId,
                                                                    @Encrypt @RequestParam(name = "userId", required = false) Long userId) {
        return Results.success(hrUnitService.listWholeHrUnitTreeOfTenant(organizationId, userId));
    }

    /**
     * 查询角色于组织层分配用户的可用组织树 - LOV使用
     */
    @Permission(permissionLogin = true)
    @ApiOperation("查询角色于组织层分配用户的可用组织树")
    @GetMapping("/hrunits/assignable-org-tree")
    public ResponseEntity<List<HrUnit>> listAssignableHrUnitTreeByRoleId(@Encrypt @RequestParam(name = "roleId") Long roleId,
            @RequestParam(name = "unitCode",required = false)String unitCode,
            @RequestParam(name = "unitName",required = false)String unitName) {
        return Results.success(hrUnitService.listAssignableHrUnitTreeByRoleId(roleId,unitCode,unitName));
    }
}
