package org.hzero.message.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.hzero.message.domain.entity.Unit;

import java.util.List;

public interface NoticeUnitMapper extends BaseMapper<Unit> {

	List<Unit> listUnits(long receiverTypeId,String unitName,String unitCode);
	
	List<Unit> listAllUnits(Long tenantId,String unitName,String unitCode);
}
