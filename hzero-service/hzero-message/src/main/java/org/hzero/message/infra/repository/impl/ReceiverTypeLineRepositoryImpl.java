package org.hzero.message.infra.repository.impl;

import org.hzero.message.domain.entity.ReceiverTypeLine;
import org.hzero.message.domain.entity.Unit;
import org.hzero.message.domain.entity.UserGroup;
import org.hzero.message.domain.repository.ReceiverTypeLineRepository;
import org.hzero.message.infra.mapper.NoticeUnitMapper;
import org.hzero.message.infra.mapper.NoticeUserGroupMapper;
import org.hzero.message.infra.mapper.ReceiverTypeLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 接收者类型行服务实现类
 *
 * @author minghui.qiu@hand-china.com
 * @date 2019-06-12 09:03:01
 */
@Service
public class ReceiverTypeLineRepositoryImpl extends  BaseRepositoryImpl<ReceiverTypeLine> implements ReceiverTypeLineRepository {

	@Autowired
	private ReceiverTypeLineMapper receiverTypeLineMapper;
	
	@Autowired
	private NoticeUserGroupMapper userGroupMapper;
	
	@Autowired
	private NoticeUnitMapper unitMapper;
	
	@Override
	public Page<ReceiverTypeLine> listReceiveTypeLine(PageRequest pageRequest, Long receiverTypeId) {
		return PageHelper.doPageAndSort(pageRequest, () -> receiverTypeLineMapper.listReceiveTypeLine(receiverTypeId));
	}

	@Override
	public Page<UserGroup> listUserGroups(PageRequest pageRequest,long receiverTypeId,String groupName,String groupCode) {
		return PageHelper.doPageAndSort(pageRequest, () ->  userGroupMapper.listUserGroups(receiverTypeId,groupName,groupCode));
	}

	@Override
	public Page<Unit> listUnits(PageRequest pageRequest,long receiverTypeId,String unitName,String unitCode) {
		return PageHelper.doPageAndSort(pageRequest, () -> unitMapper.listUnits(receiverTypeId,unitName,unitCode));
	}

    @Override
    public List<ReceiverTypeLine> listOldReceiveTypeLine(Long receiverTypeId) {
        return receiverTypeLineMapper.listReceiveTypeLine(receiverTypeId);
    }
}
