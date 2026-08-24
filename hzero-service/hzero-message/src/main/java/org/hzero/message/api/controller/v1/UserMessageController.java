package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserMsgParamDTO;
import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 用户消息管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Api(tags = MessageSwaggerApiConfig.USER_MESSAGE)
@RestController("userMessageController.v1")
@RequestMapping("/v1/{organizationId}/messages/user")
public class UserMessageController extends BaseController {

    private static final String UNREAD_MESSAGE_COUNT = "unreadMessageCount";

    private UserMessageService userMessageService;

    @Autowired
    public UserMessageController(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    @ApiOperation("查询当前用户未读消息数量")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countUnreadMessage(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId) {
        Map<String, Long> resultMap = new HashMap<>(4);
        resultMap.put(UNREAD_MESSAGE_COUNT, userMessageService.countUnreadMessage(organizationId, DetailsHelper.getUserDetails().getUserId()));
        return Results.success(resultMap);
    }

    @ApiOperation("查询当前用户的未读消息列表，默认5条")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/preview")
    public ResponseEntity<List<SimpleMessageDTO>> listSimpleMessage(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                                    @RequestParam(required = false) @ApiParam("预览消息数量，不传取默认数量") Integer previewMessageCount,
                                                                    @RequestParam(defaultValue = "false") @ApiParam("是否返回消息内容") boolean withContent) {
        return Results.success(userMessageService.listSimpleMessage(organizationId, DetailsHelper.getUserDetails().getUserId(), previewMessageCount, withContent));
    }

    @ApiOperation("查询当前用户的消息列表")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<UserMessageDTO>> listMessage(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                            UserMsgParamDTO userMsgParamDTO,
                                                            @ApiIgnore @SortDefault(value = AuditDomain.FIELD_CREATION_DATE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        userMsgParamDTO.setTenantId(organizationId).setUserId(DetailsHelper.getUserDetails().getUserId()).setMessageTypeCode(HmsgConstant.MessageType.WEB);
        return Results.success(userMessageService.listMessage(userMsgParamDTO, pageRequest));
    }

    @ApiOperation("查询消息详情，并将消息标记为已读")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/{userMessageId}")
    public ResponseEntity<Message> getMessage(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                              @Encrypt @PathVariable @ApiParam(value = "用户消息ID", required = true) long userMessageId) {
        return Results.success(userMessageService.getMessage(organizationId, DetailsHelper.getUserDetails().getUserId(), userMessageId));
    }

    @ApiOperation("标记消息已读")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping("/read-flag")
    public ResponseEntity<Void> updateMessageRead(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                  @RequestParam(required = false) @ApiParam("全部已读，如果不为1，则指定消息已读不能为空") Integer readAll,
                                                  @Encrypt @RequestParam(required = false) @ApiParam("指定消息已读，如果readAll为1") List<Long> userMessageIdList) {
        if (BaseConstants.Flag.YES.equals(readAll)) {
            userMessageService.readMessage(organizationId, DetailsHelper.getUserDetails().getUserId());
        } else if (!CollectionUtils.isEmpty(userMessageIdList)) {
            userMessageService.readMessage(organizationId, DetailsHelper.getUserDetails().getUserId(), userMessageIdList);
        }
        return Results.success();
    }

    @ApiOperation("铃铛消息清除")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/clear")
    public ResponseEntity<Void> clearMessage(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId) {
        userMessageService.clearMessage(organizationId, DetailsHelper.getUserDetails().getUserId());
        return Results.success();
    }

    @ApiOperation("删除消息")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @DeleteMapping
    public ResponseEntity<Void> deleteMessage(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                              @Encrypt @RequestParam @ApiParam(value = "指定消息删除", required = true) List<Long> userMessageIdList) {
        // 将要删除的消息标记已读
        userMessageService.readMessage(organizationId, DetailsHelper.getUserDetails().getUserId(), userMessageIdList);
        userMessageService.deleteMessage(organizationId, DetailsHelper.getUserDetails().getUserId(), userMessageIdList);
        return Results.success();
    }
}
