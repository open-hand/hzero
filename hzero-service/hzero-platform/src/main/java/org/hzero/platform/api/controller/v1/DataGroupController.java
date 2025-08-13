package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.app.service.DataGroupService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DataGroup;
import org.hzero.platform.domain.repository.DataGroupRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据组定义 管理 API
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Api(tags = PlatformSwaggerApiConfig.DATA_GROUP)
@RestController("dataGroupController.v1")
@RequestMapping("/v1/{organizationId}/data-groups")
public class DataGroupController extends BaseController {

    @Autowired
    private DataGroupRepository dataGroupRepository;
    @Autowired
    private DataGroupService dataGroupService;

    @ApiOperation(value = "数据组定义列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<DataGroup>> list(
                    @PathVariable("organizationId") Long tenantId,
                    DataGroup dataGroup,
                    @ApiIgnore @SortDefault(value = DataGroup.FIELD_GROUP_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        dataGroup.setTenantId(tenantId);
        Page<DataGroup> list = dataGroupService.pageDataGroupByCondition(pageRequest, dataGroup);
        return Results.success(list);
    }

    @ApiOperation(value = "数据组详情查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dataGroupId}")
    public ResponseEntity<DataGroup> detail(@PathVariable("organizationId") Long tenantId,
                    @PathVariable("dataGroupId") @Encrypt Long dataGroupId) {
        DataGroup queryDataGroup = new DataGroup();
        queryDataGroup.setGroupId(dataGroupId);
        DataGroup dataGroup = dataGroupService.detailDataGroup(queryDataGroup);
        return Results.success(dataGroup);
    }

    @ApiOperation(value = "更新数据组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DataGroup> update(@PathVariable("organizationId") Long tenantId,
                    @RequestBody @Encrypt DataGroup dataGroup) {
        dataGroup.setTenantId(tenantId);
        validObject(dataGroup);
        dataGroupRepository.updateOptional(dataGroup, DataGroup.FIELD_GROUP_NAME, DataGroup.FIELD_TENANT_ID,
                        DataGroup.FIELD_REMARK, DataGroup.FIELD_ENABLED_FLAG);
        return Results.success(dataGroup);
    }

    @ApiOperation(value = "创建数据组定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DataGroup> create(@PathVariable("organizationId") Long tenantId,
                    @RequestBody DataGroup dataGroup) {
        dataGroup.setTenantId(tenantId);
        validObject(dataGroup);
        dataGroupService.createDataGroup(dataGroup);
        return Results.success(dataGroup);
    }

    @ApiOperation(value = "删除数据组定义")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable("organizationId") Long tenantId, @RequestBody @Encrypt DataGroup dataGroup) {
        dataGroupService.deleteDataGroup(dataGroup);
        return Results.success();
    }

}
