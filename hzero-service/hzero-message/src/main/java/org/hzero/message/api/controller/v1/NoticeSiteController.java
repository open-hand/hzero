package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = MessageSwaggerApiConfig.NOTICE_SITE)
@RestController("noticeSiteController.v1")
@RequestMapping("/v1/notices")
public class NoticeSiteController {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeRepository noticeRepository;

    @ApiOperation(value = "公告/通知列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ProcessCacheValue
    @CustomPageRequest
    public ResponseEntity<Page<NoticeDTO>> list(NoticeDTO noticeDTO,
                                                @ApiIgnore @SortDefault(value = Notice.FIELD_NOTICE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        // 平台级仅能查询自身的公告信息
        noticeDTO.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        return Results.success(noticeRepository.pageNotice(pageRequest, noticeDTO));
    }

    @ApiOperation(value = "公告/通知信息明细")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeDTO> detail(@Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeRepository.detailNotice(DetailsHelper.getUserDetails().getTenantId(), noticeId));
    }

    @ApiOperation(value = "用户公告明细")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @GetMapping("/user/{noticeId}")
    public ResponseEntity<NoticeDTO> userDetail(@Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeRepository.detailAnnouncement(DetailsHelper.getUserDetails().getTenantId(), noticeId));
    }

    @ApiOperation(value = "公告基础信息明细列表（查询所有明细信息）")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/page-details")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<NoticeDTO>> pageDetail(NoticeDTO noticeDTO, @ApiIgnore @SortDefault(value = Notice.FIELD_NOTICE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        noticeDTO.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        return Results.success(noticeRepository.selectNoticeWithDetails(noticeDTO, pageRequest));
    }

    @ApiOperation(value = "创建公告/通知")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<NoticeDTO> create(@RequestBody NoticeDTO notice) {
        notice.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        return Results.success(noticeService.createNotice(notice));
    }

    @ApiOperation(value = "修改公告/通知")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<NoticeDTO> update(@Encrypt @RequestBody NoticeDTO notice) {
        notice.setTenantId(DetailsHelper.getUserDetails().getTenantId());
        SecurityTokenHelper.validTokenIgnoreInsert(notice);
        return Results.success(noticeService.updateNotice(notice));
    }

    @ApiOperation(value = "删除公告/通知")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Notice> deleteNotice(@Encrypt @RequestBody Notice notice) {
        SecurityTokenHelper.validTokenIgnoreInsert(notice);
        return Results.success(noticeService.deleteNotice(DetailsHelper.getUserDetails().getTenantId(), notice.getNoticeId()));
    }

    @ApiOperation(value = "发布公告/通知")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{noticeId}/publish")
    public ResponseEntity<Notice> publicNotice(@Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeService.publicNotice(DetailsHelper.getUserDetails().getTenantId(), noticeId));
    }

    @ApiOperation(value = "撤销已发布的公告/通知")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{noticeId}/revoke")
    public ResponseEntity<Notice> revokeNotice(@Encrypt @PathVariable Long noticeId) {
        return Results.success(noticeService.revokeNotice(DetailsHelper.getUserDetails().getTenantId(), noticeId));
    }
}
