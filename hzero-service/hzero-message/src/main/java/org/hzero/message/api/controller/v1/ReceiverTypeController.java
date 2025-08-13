package org.hzero.message.api.controller.v1;

import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.ReceiverTypeDTO;
import org.hzero.message.app.service.ReceiverTypeLineService;
import org.hzero.message.app.service.ReceiverTypeService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.ReceiverType;
import org.hzero.message.domain.entity.ReceiverTypeLine;
import org.hzero.message.domain.entity.Unit;
import org.hzero.message.domain.entity.UserGroup;
import org.hzero.message.domain.repository.ReceiverTypeRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 接收者类型 管理 API
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
@Api(tags = MessageSwaggerApiConfig.RECEIVER_TYPE)
@RestController("receiverTypeController.v1")
@RequestMapping("/v1/{organizationId}/receiver-types")
public class ReceiverTypeController extends BaseController {


    private ReceiverTypeRepository receiverTypeRepository;
    private ReceiverTypeService receiverTypeService;
    private ReceiverTypeLineService receiverTypeLineService;

    @Autowired
    public ReceiverTypeController(ReceiverTypeRepository receiverTypeRepository,
                                  ReceiverTypeService receiverTypeService,
                                  ReceiverTypeLineService receiverTypeLineService) {
        this.receiverTypeRepository = receiverTypeRepository;
        this.receiverTypeService = receiverTypeService;
        this.receiverTypeLineService = receiverTypeLineService;
    }

    @ApiOperation(value = "接收者类型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path"),
            @ApiImplicitParam(name = "receiverType", value = "接收者类型", paramType = "ReceiverType")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ReceiverTypeDTO>> list(@PathVariable("organizationId") Long organizationId, ReceiverType receiverType,
                                                      @ApiIgnore @SortDefault(value = ReceiverType.FIELD_RECEIVER_TYPE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        receiverType.setTenantId(organizationId);
        return Results.success(receiverTypeService.pageAndSort(pageRequest, receiverType));
    }

    @ApiOperation(value = "接收者类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户id", paramType = "path"),
            @ApiImplicitParam(name = "typeCode", value = "类型编码", paramType = "path")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/detail")
    public ResponseEntity<ReceiverType> detail(@PathVariable("organizationId") Long organizationId, @RequestParam("typeCode") String typeCode) {
        return Results.success(receiverTypeService.getReceiverType(organizationId, typeCode));
    }


    @ApiOperation(value = "创建接收者类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @PostMapping
    public ResponseEntity<ReceiverType> create(@PathVariable("organizationId") Long organizationId, @RequestBody ReceiverType receiverType) {
        receiverType.setTenantId(organizationId);
        validObject(receiverType);
        receiverType.validCodeRepeat(receiverTypeRepository);
        receiverTypeRepository.insertSelective(receiverType);
        return Results.success(receiverType);
    }

    @ApiOperation(value = "修改接收者类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @PutMapping
    public ResponseEntity<ReceiverType> update(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody ReceiverType receiverType) {
        SecurityTokenHelper.validToken(receiverType);
        receiverType.setTenantId(organizationId);
        validObject(receiverType);
        receiverTypeRepository.updateOptional(receiverType, ReceiverType.FIELD_TYPE_NAME, ReceiverType.FIELD_ROUTE_NAME, ReceiverType.FIELD_API_URL, ReceiverType.FIELD_ENABLED_FLAG, ReceiverType.FIELD_TYPE_MODE);
        return Results.success(receiverType);
    }

    /**
     * 接收者类型行列表
     */
    @ApiOperation(value = "接收者类型行列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/lines/{receiverTypeId}")
    public ResponseEntity<?> listReceiverTypeLine(@PathVariable("organizationId") Long organizationId,
                                                  @Encrypt @PathVariable("receiverTypeId") Long receiverTypeId,
                                                  @ApiIgnore @SortDefault(value = ReceiverTypeLine.FIELD_RECEIVER_USER_GROUP_ID,
                                                          direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<ReceiverTypeLine> list = receiverTypeLineService.listReceiverTypeLine(pageRequest, receiverTypeId);
        return Results.success(list);
    }

    /**
     * 创建接收者类型行
     */
    @ApiOperation(value = "创建接收者类型行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/lines/{receiverTypeId}")
    public ResponseEntity<?> createLines(@PathVariable("organizationId") Long organizationId,
                                         @Encrypt @PathVariable("receiverTypeId") Long receiverTypeId,
                                         @Encrypt @RequestBody List<ReceiverTypeLine> receiverLineList) {
        return Results.success(receiverTypeLineService.createReceiverTypeLine(receiverTypeId, receiverLineList));
    }

    /**
     * 删除接收组关联用户组
     */
    @ApiOperation(value = "删除接收者类型用户组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/lines")
    public ResponseEntity<?> removeLines(@PathVariable("organizationId") Long organizationId,
                                         @Encrypt @RequestBody(required = true) List<ReceiverTypeLine> receiverLineList) {
        SecurityTokenHelper.validToken(receiverLineList);
        receiverTypeLineService.deleteUserGroup(receiverLineList);
        return Results.success();
    }

    /**
     * 用户组列表
     */
    @ApiOperation(value = "用户组列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/lines/user-groups/{receiverTypeId}")
    public ResponseEntity<?> listUserGroups(@PathVariable("organizationId") Long organizationId,
                                            @RequestParam(required = false) @ApiParam(value = "用户组名称") String groupName,
                                            @RequestParam(required = false) @ApiParam(value = "用户组编码") String groupCode,
                                            @Encrypt @PathVariable("receiverTypeId") Long receiverTypeId,
                                            @ApiIgnore @SortDefault(value = UserGroup.FIELD_USER_GROUP_ID,
                                                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<UserGroup> list = receiverTypeLineService.listUserGroups(pageRequest, receiverTypeId, groupName, groupCode);
        return Results.success(list);
    }

    /**
     * 组织列表
     */
    @ApiOperation(value = "组织列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/lines/units/{receiverTypeId}")
    public ResponseEntity<?> listUnits(@PathVariable("organizationId") Long organizationId,
                                       @RequestParam(required = false) @ApiParam(value = "组织名称") String unitName,
                                       @RequestParam(required = false) @ApiParam(value = "组织编码") String unitCode,
                                       @Encrypt @PathVariable("receiverTypeId") Long receiverTypeId,
                                       @ApiIgnore @SortDefault(value = Unit.FIELD_UNIT_ID,
                                               direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<Unit> list = receiverTypeLineService.listUnits(pageRequest, receiverTypeId, unitName, unitCode);
        return Results.success(list);
    }

}
