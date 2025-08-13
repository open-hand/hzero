package org.hzero.message.domain.repository;

import org.hzero.message.domain.entity.NoticePublished;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 公告发布记录服务接口
 *
 * @author minghui.qiu@hand-china.com
 * @date 2019-06-10 16:18:09
 */
public interface NoticePublishedRepository extends BaseRepository<NoticePublished> {

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

