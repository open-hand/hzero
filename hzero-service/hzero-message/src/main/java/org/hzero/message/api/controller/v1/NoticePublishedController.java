package org.hzero.message.api.controller.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.ResponseUtils;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.UnitUserDTO;
import org.hzero.message.app.service.NoticePublishedService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.NoticePublished;
import org.hzero.message.domain.entity.UserGroupAssign;
import org.hzero.message.domain.repository.NoticePublishedRepository;
import org.hzero.message.infra.feign.IamRemoteService;
import org.hzero.message.infra.feign.UnitService;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 公告发布记录 管理 API
 *
 * @author minghui.qiu@hand-china.com
 * @date 2019-06-10 16:18:09
 */
@Api(tags = MessageSwaggerApiConfig.NOTICE_PUBLISH)
@RestController("noticePublishedController.v1")
@RequestMapping("/v1/{organizationId}/notices/published-records")
public class NoticePublishedController extends BaseController {

    @Autowired
    private NoticePublishedRepository noticePublishedRepository;
    @Autowired
    private NoticePublishedService noticePublishedService;
    @Autowired
    private IamRemoteService iamRemoteService;
    @Autowired
    private UnitService unitService;

    @ApiOperation(value = "公告/通知发布记录列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{noticeId}")
    @CustomPageRequest
    public ResponseEntity<Page<NoticePublished>> list(@Encrypt @PathVariable Long noticeId,
                                                      @PathVariable Long organizationId,
                                                      @ApiIgnore @SortDefault(value = NoticePublished.FIELD_PUBLISHED_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(noticePublishedService.listNoticePublished(organizationId, pageRequest, noticeId));
    }

    @ApiOperation(value = "公告发布记录明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{publishedId}/details")
    public ResponseEntity<NoticePublished> detail(@Encrypt @PathVariable Long publishedId, @PathVariable Long organizationId) {
        return Results.success(noticePublishedRepository.selectOne(new NoticePublished().setTenantId(organizationId).setPublishedId(publishedId)));
    }

    @ApiOperation(value = "重新发布公告/通知")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{noticeId}/publish")
    public ResponseEntity<NoticePublished> create(@Encrypt @PathVariable Long noticeId, @PathVariable Long organizationId,
                                                  @Encrypt @RequestBody @ApiParam("发布记录ID列表") List<Long> publishedIds) {
        return Results.success(noticePublishedService.publicNotice(publishedIds, noticeId, organizationId));
    }

    @ApiOperation(value = "修改公告发布记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<NoticePublished> update(@Encrypt @RequestBody NoticePublished noticePublished, @PathVariable Long organizationId) {
        noticePublished.setTenantId(organizationId);
        SecurityTokenHelper.validToken(noticePublished);
        noticePublishedRepository.updateByPrimaryKeySelective(noticePublished);
        return Results.success(noticePublished);
    }

    @ApiOperation(value = "删除公告发布记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{publishedId}")
    public ResponseEntity<Void> remove(@Encrypt @RequestBody NoticePublished noticePublished, @PathVariable Long organizationId) {
        SecurityTokenHelper.validToken(noticePublished);
        noticePublishedRepository.deleteByPrimaryKey(noticePublished);
        return Results.success();
    }

    /**
     * "获取用户组下的用户Id及用户所属租户Id-用于消息公告管理界面"
     *
     * @param userGroupAssigns 用户组列表
     * @return Set<Receiver>
     */
    @ApiOperation(value = "用户组下用户")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/user-groups/user-ids")
    public ResponseEntity<Set<Receiver>> listUserGroupAssignUsers(@PathVariable Long organizationId,
                                                                  @RequestBody List<UserGroupAssign> userGroupAssigns) {
        return Results.success(ResponseUtils.getResponse(iamRemoteService.listUserGroupAssignUsers(userGroupAssigns), new TypeReference<Set<Receiver>>() {
        }));
    }

    /**
     * "获取组织下的用户Id及用户所属租户Id-用于消息公告管理界面"
     *
     * @param units 组织列表
     * @return Set<Receiver>
     */
    @ApiOperation(value = "组织下用户")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/units/user-ids")
    public ResponseEntity<Set<Receiver>> listUnitUsers(@PathVariable Long organizationId, @RequestBody List<UnitUserDTO> units) {
        return Results.success(ResponseUtils.getResponse(unitService.listUnitUsers(units), new TypeReference<Set<Receiver>>() {
        }));
    }
}
