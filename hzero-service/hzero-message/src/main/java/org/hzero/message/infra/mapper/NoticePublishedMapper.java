package org.hzero.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.NoticePublished;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 公告发布记录Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-06-10 16:18:09
 */
public interface NoticePublishedMapper extends BaseMapper<NoticePublished> {

    /**
     * 查询发布记录列表
     *
     * @param tenantId 租户ID
     * @param noticeId 公告/通知ID
     * @return 公告/通知
     */
    List<NoticePublished> listNoticePublished(@Param("tenantId") Long tenantId,
											  @Param("noticeId") Long noticeId);
}

