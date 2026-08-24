package org.hzero.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.NoticeReceiver;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 公告接收记录Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-06-10 16:18:09
 */
public interface NoticeReceiverMapper extends BaseMapper<NoticeReceiver> {

    List<NoticeReceiver> listReceiveRecordPage(@Param("publishedIds") List<Long> publishedIds);

    List<NoticeReceiver> listReceiveRecord(@Param("publishedId") Long publishedId);
}

