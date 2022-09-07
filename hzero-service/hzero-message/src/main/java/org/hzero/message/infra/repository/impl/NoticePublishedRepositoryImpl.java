package org.hzero.message.infra.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.message.domain.entity.NoticePublished;
import org.hzero.message.domain.repository.NoticePublishedRepository;
import org.hzero.message.infra.mapper.NoticePublishedMapper;

/**
 * 公告发布记录服务实现类
 *
 * @author minghui.qiu@hand-china.com
 * @date 2019-06-10 16:18:09
 */
@Service
public class NoticePublishedRepositoryImpl extends  BaseRepositoryImpl<NoticePublished> implements NoticePublishedRepository {

	@Autowired
    private NoticePublishedMapper noticePublishedMapper;
	
	@Override
	public Page<NoticePublished> listNoticePublished(Long tenantId, PageRequest pageRequest, Long noticeId) {
		return PageHelper.doPageAndSort(pageRequest, () -> noticePublishedMapper.listNoticePublished(tenantId, noticeId));
	}

}
