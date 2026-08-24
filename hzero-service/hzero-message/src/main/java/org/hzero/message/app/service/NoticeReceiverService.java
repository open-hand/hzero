package org.hzero.message.app.service;

import java.util.List;

import org.hzero.message.domain.entity.NoticeReceiver;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author minghui.qiu@hand-china.com
 */
public interface NoticeReceiverService {

    /**
     * 公告/通知 接收记录
     *
     * @param tenantId     租户ID
     * @param pageRequest  分页
     * @param publishedIds 发布记录
     * @param noticeId     公告/通知 ID
     * @return 接收记录
     */
    Page<NoticeReceiver> listReceiveRecordPage(Long tenantId, PageRequest pageRequest, List<Long> publishedIds, Long noticeId);

    /**
     * 创建 公告/通知 接收记录
     *
     * @param noticeId          公告/通知 ID
     * @param tenantId          租户Id
     * @param noticeReceiveList 接收人列表
     * @return 接收记录
     */
    List<NoticeReceiver> createNoticeReceiver(long noticeId, long tenantId, List<NoticeReceiver> noticeReceiveList);
}
