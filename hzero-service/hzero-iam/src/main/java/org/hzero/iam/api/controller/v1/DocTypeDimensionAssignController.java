package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.iam.app.service.DocTypeDimensionAssignService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;

/**
 * 单据类型定义维度分配
 *
 * @author qingsheng.chen@hand-china.com 2019-07-02 14:23
 */
@Api(tags = SwaggerApiConfig.DOC_TYPE)
@RestController("docTypeAssignDimensionController.v1")
@RequestMapping("/v1/{organizationId}/doc-type/dimension")
public class DocTypeDimensionAssignController {
    private final DocTypeDimensionAssignService docTypeDimensionAssignService;

    @Autowired
    public DocTypeDimensionAssignController(DocTypeDimensionAssignService docTypeDimensionAssignService) {
        this.docTypeDimensionAssignService = docTypeDimensionAssignService;
    }

    @ApiOperation(value = "查询角色单据类型定义维度分配列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path"),
            @ApiImplicitParam(name = "roleId", value = "用户Id", paramType = "path")
    })
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<DocTypeDimension>> listRoleAssign(@PathVariable long organizationId,
                                                                 @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable long roleId) {
        return Results.success(docTypeDimensionAssignService.listRoleAssign(roleId, organizationId));
    }

    @ApiOperation(value = "查询用户单据类型定义维度分配列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "用户Id", paramType = "path")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DocTypeDimension>> listUserAssign(@PathVariable long organizationId,
                                                                 @Encrypt @ApiParam(value = "用户ID", required = true) @PathVariable long userId) {
        return Results.success(docTypeDimensionAssignService.listUserAssign(userId, organizationId));
    }
}
