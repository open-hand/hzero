package org.hzero.message.infra.mapper;

import org.hzero.message.domain.entity.ReceiverTypeLine;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 接收者类型用户组Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-06-12 09:03:01
 */
public interface ReceiverTypeLineMapper extends BaseMapper<ReceiverTypeLine> {

	/**
	 * 获取接收组行列表
	 *
	 * @param receiverTypeId 接收组ID
	 * @return 接收组行
	 */
	List<ReceiverTypeLine> listReceiveTypeLine(Long receiverTypeId);
}

