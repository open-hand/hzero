package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.AutoProcessResultDTO;
import org.hzero.iam.api.dto.TenantAdminRoleAndDataPrivAutoAssignmentDTO;
import org.hzero.iam.app.service.MemberRoleExternalService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.validator.ValidList;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 *
 * @author mingwei.liu@hand-china.com 2018/8/11
 */
@Api(tags = SwaggerApiConfig.MEMBER_ROLE_EXTERNAL)
@RestController("memberRoleExternalController.v1")
@RequestMapping(value = "/hzero/v1/member-roles-external")
public class MemberRoleExternalController {
    @Autowired
    MemberRoleExternalService memberRoleExternalService;

    @ApiOperation(value = "批量自动分配租户管理员角色及数据权限")
    @Permission(permissionWithin = true)
    @PostMapping("/auto-assign")
    public ResponseEntity<List<AutoProcessResultDTO>> batchAutoAssignTenantAdminRoleAndDataPriv(
            @RequestBody ValidList<TenantAdminRoleAndDataPrivAutoAssignmentDTO> tenantAdminRoleAndDataPrivAutoAssignmentDTOList) {
        return Results.success(
                memberRoleExternalService.batchAutoAssignTenantAdminRoleAndDataPriv(tenantAdminRoleAndDataPrivAutoAssignmentDTOList));
    }
}
