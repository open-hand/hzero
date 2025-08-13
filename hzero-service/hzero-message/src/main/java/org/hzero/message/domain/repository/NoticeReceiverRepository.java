package org.hzero.message.domain.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.NoticeReceiver;
import org.hzero.message.domain.entity.Unit;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 公告接收记录服务接口
 *
 * @author minghui.qiu@hand-china.com
 * @date 2019-06-10 16:18:09
 */
public interface NoticeReceiverRepository extends BaseRepository<NoticeReceiver> {

    Page<NoticeReceiver> listReceiveRecordPage(PageRequest pageRequest, List<Long> publishedIds);

    Page<NoticeReceiver> listReceiveRecord(PageRequest pageRequest, Long publishedId);

    Page<Unit> listAllUnits(PageRequest pageRequest, Long tenantId, String unitName, String unitCode);

    /**
     * 获取消息接收者
     *
     * @param publishedIds 公告发布记录ID集合
     * @return 消息接收者
     */
    List<NoticeReceiver> listReceiveRecordPage(@Param("publishedIds") List<Long> publishedIds);
}

