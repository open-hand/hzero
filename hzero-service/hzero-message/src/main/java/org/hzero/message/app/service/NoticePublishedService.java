package org.hzero.message.app.service;

import java.util.List;

import org.hzero.message.domain.entity.NoticePublished;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author minghui.qiu@hand-china.com
 */
public interface NoticePublishedService {

    /**
     * 重新发布公告/通知
     *
     * @param publishedIds   发布记录ID
     * @param noticeId       公告/通知ID
     * @param organizationId 租户ID
     * @return 发布记录
     */
    NoticePublished publicNotice(List<Long> publishedIds, Long noticeId, Long organizationId);

    /**
     * 查询发布记录列表
     *
     * @param tenantId    租户ID
     * @param pageRequest 分页
     * @param noticeId    公告/通知ID
     * @return 公告/通知
     */
    Page<NoticePublished> listNoticePublished(Long tenantId, PageRequest pageRequest, Long noticeId);
}
