package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.cache.ProcessCacheValue;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.app.service.NoticeService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Notice;
import org.hzero.message.domain.repository.NoticeRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 公告基础信息 管理 API
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
@Api(tags = MessageSwaggerApiConfig.NOTICE)
@RestController("noticeController.v1")
@RequestMapping("/v1")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeRepository noticeRepository;

    @ApiOperation(value = "公告/通知信息列表 或 用户公告列表")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/{organizationId}/notices")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ProcessCacheValue
    @CustomPageRequest
    public ResponseEntity<Page<NoticeDTO>> listNotice(@PathVariable Long organizationId, NoticeDTO noticeDTO,
                                                      @ApiIgnore @SortDefault(value = Notice.FIELD_NOTICE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        noticeDTO.setTenantId(organizationId);
        return Results.success(noticeService.pageNotice(pageRequest, noticeDTO));
    }

    @ApiOperation(value = "公告/通知信息明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{organizationId}/notices/{noticeId}")
    public ResponseEntity<NoticeDTO> detail(@PathVariable Long organizationId, @Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeRepository.detailNotice(organizationId, noticeId));
    }

    @ApiOperation(value = "用户公告列表，非分页模式")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/{organizationId}/notices/user/preview")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ProcessCacheValue
    @CustomPageRequest
    public ResponseEntity<List<NoticeDTO>> listSimpleNotice(@PathVariable Long organizationId, @Encrypt NoticeDTO noticeDTO) {
        noticeDTO.setTenantId(organizationId);
        noticeDTO.setUserId(DetailsHelper.getUserDetails().getUserId());
        return Results.success(noticeService.listUserAnnouncement(noticeDTO));
    }

    @ApiOperation(value = "顶部悬浮公告")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/notices/top")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ProcessCacheValue
    @CustomPageRequest
    public ResponseEntity<NoticeDTO> topNotice(NoticeDTO noticeDTO) {
        noticeDTO.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        noticeDTO.setUserId(DetailsHelper.getUserDetails().getUserId());
        return Results.success(noticeService.topAnnouncement(noticeDTO));
    }

    @ApiOperation(value = "用户公告明细")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/{organizationId}/notices/user/{noticeId}")
    public ResponseEntity<NoticeDTO> userDetail(@PathVariable Long organizationId, @Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeRepository.detailAnnouncement(organizationId, noticeId));
    }

    @ApiOperation(value = "公告基础信息明细列表（查询所有明细信息）")

    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/notices/page-details")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<NoticeDTO>> pageDetail(@PathVariable Long organizationId, NoticeDTO noticeDTO,
                                                      @ApiIgnore @SortDefault(value = Notice.FIELD_NOTICE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        noticeDTO.setTenantId(organizationId);
        return Results.success(noticeRepository.selectNoticeWithDetails(noticeDTO, pageRequest));
    }

    @ApiOperation(value = "创建公告/通知")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/notices")
    public ResponseEntity<NoticeDTO> create(@PathVariable Long organizationId, @RequestBody NoticeDTO notice) {
        notice.setTenantId(organizationId);
        return Results.success(noticeService.createNotice(notice));
    }

    @ApiOperation(value = "修改公告/通知")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/notices")
    public ResponseEntity<NoticeDTO> update(@PathVariable Long organizationId, @Encrypt @RequestBody NoticeDTO notice) {
        notice.setTenantId(organizationId);
        SecurityTokenHelper.validTokenIgnoreInsert(notice);
        return Results.success(noticeService.updateNotice(notice));
    }

    @ApiOperation(value = "删除公告/通知")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/notices")
    public ResponseEntity<Notice> deleteNotice(@PathVariable Long organizationId, @Encrypt @RequestBody Notice notice) {
        SecurityTokenHelper.validTokenIgnoreInsert(notice);
        return Results.success(noticeService.deleteNotice(organizationId, notice.getNoticeId()));
    }

    @ApiOperation(value = "发布公告/通知")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/notices/{noticeId}/publish")
    public ResponseEntity<Notice> publicNotice(@PathVariable Long organizationId, @Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeService.publicNotice(organizationId, noticeId));
    }

    @ApiOperation(value = "撤销已发布的公告/通知")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/notices/{noticeId}/revoke")
    public ResponseEntity<Notice> revokeNotice(@PathVariable Long organizationId, @Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeService.revokeNotice(organizationId, noticeId));
    }

    @ApiOperation(value = "查询公告标题")
    @Permission(permissionWithin = true)
    @PostMapping("/{organizationId}/notices/{category}/page")
    @CustomPageRequest
    public ResponseEntity<Page<NoticeDTO>> pageNoticeTitle(@PathVariable Long organizationId, @RequestParam(required = false) String title,
                                                           @PathVariable String category, @RequestBody PageRequest pageRequest) {
        return Results.success(noticeService.pageNoticeTitle(StringUtils.upperCase(category), title, organizationId, pageRequest));
    }

    @ApiOperation(value = "id查询公告内容")
    @Permission(permissionWithin = true)
    @GetMapping("/notices/{noticeId}/detail")
    public ResponseEntity<NoticeDTO> getNoticeDetailsById(@Encrypt @PathVariable("noticeId") Long noticeId) {
        return Results.success(noticeRepository.selectNoticeBody(null, noticeId));
    }
}
