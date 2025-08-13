package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.NoticePublishedService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.NoticePublished;
import org.hzero.message.domain.repository.NoticePublishedRepository;
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
import io.choerodon.swagger.annotation.Permission;

/**
 * 公告发布记录 管理 API
 *
 * @author minghui.qiu@hand-china.com
 */
@Api(tags = MessageSwaggerApiConfig.NOTICE_PUBLISH_SITE)
@RestController("noticePublishedSiteController.v1")
@RequestMapping("/v1/notices/published-records")
public class NoticePublishedSiteController extends BaseController {

    @Autowired
    private NoticePublishedRepository noticePublishedRepository;
    @Autowired
    private NoticePublishedService noticePublishedService;

    @ApiOperation(value = "公告/通知发布记录列表")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{noticeId}")
    public ResponseEntity<Page<NoticePublished>> list(@Encrypt @PathVariable Long noticeId,
                                                      @ApiIgnore @SortDefault(value = NoticePublished.FIELD_PUBLISHED_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(noticePublishedService.listNoticePublished(null, pageRequest, noticeId));
    }

    @ApiOperation(value = "公告发布记录明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{publishedId}/details")
    public ResponseEntity<NoticePublished> detail(@Encrypt @PathVariable Long publishedId) {
        return Results.success(noticePublishedRepository.selectByPrimaryKey(publishedId));
    }

    @ApiOperation(value = "重新发布公告/通知")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{noticeId}/publish")
    public ResponseEntity<NoticePublished> create(@Encrypt @PathVariable Long noticeId,
                                                  @Encrypt @RequestBody(required = false) @ApiParam("发布记录ID列表") List<Long> publishedIds) {
        return Results.success(noticePublishedService.publicNotice(publishedIds, noticeId, null));
    }

    @ApiOperation(value = "修改公告发布记录")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<NoticePublished> update(@Encrypt @RequestBody NoticePublished noticePublished) {
        noticePublishedRepository.updateByPrimaryKeySelective(noticePublished);
        return Results.success(noticePublished);
    }

    @ApiOperation(value = "删除公告发布记录")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{publishedId}")
    public ResponseEntity<Void> remove(@Encrypt @PathVariable Long publishedId) {
        noticePublishedRepository.deleteByPrimaryKey(publishedId);
        return Results.success();
    }
}
