package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.common.Criteria;
import org.hzero.platform.app.service.HpfmGroupService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Group;
import org.hzero.platform.domain.repository.HpfmGroupRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
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
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 集团信息 管理 API
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@Api(tags = PlatformSwaggerApiConfig.GROUP)
@RestController("groupController.v1")
@RequestMapping("/v1/{organizationId}/groups")
public class GroupController extends BaseController {

    @Autowired
    private HpfmGroupRepository groupRepository;
    @Autowired
    private HpfmGroupService groupService;

    @ApiOperation(value = "集团信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<Group>> list(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                            Group group,
                                            @ApiIgnore @SortDefault(value = "groupId", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        group = ObjectUtils.defaultIfNull(group, new Group());
        group.setTenantId(tenantId);
        Page<Group> list = this.groupRepository.pageAndSort(pageRequest, group);
        return Results.success(list);
    }

    @ApiOperation("查询当前租户下的集团")
    @GetMapping("/self")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<Group>> listSelf(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId) {
        Group group = new Group();
        group.setTenantId(tenantId);
        return Results.success(this.groupRepository.selectOptional(group, new Criteria().unSelect(Group.FIELD_CREATED_BY, Group.FIELD_CREATION_DATE,
                Group.FIELD_LAST_UPDATED_BY, Group.FIELD_LAST_UPDATE_DATE)));
    }

    @ApiOperation(value = "集团信息明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> detail(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                        @ApiParam(value = "集团ID", required = true) @PathVariable @Encrypt Long groupId) {
        Group group = this.groupRepository.selectByPrimaryKey(groupId);
        return Results.success(group);
    }

    @ApiOperation(value = "创建/修改集团信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Group> insertOrUpdate(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                                @RequestBody @Encrypt Group group) {
        Assert.notNull(group, BaseConstants.ErrorCode.DATA_INVALID);
        group.setTenantId(tenantId);
        group = this.groupService.insertOrUpdate(group);
        return Results.success(group);
    }

    @ApiOperation(value = "批量创建/修改集团信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch")
    public ResponseEntity<List<Group>> batchInsertOrUpdate(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                                           @RequestBody @Encrypt List<Group> groups) {
        Assert.notNull(groups, BaseConstants.ErrorCode.DATA_INVALID);
        for (Group group : groups) {
            group.setTenantId(tenantId);
        }
        groups = this.groupService.batchInsertOrUpdate(groups);
        return Results.success(groups);
    }

    @ApiIgnore
    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "删除集团信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{groupId}")
    public ResponseEntity remove(@PathVariable @Encrypt Long groupId) {
        this.groupRepository.deleteByPrimaryKey(groupId);
        return Results.success();
    }

}
