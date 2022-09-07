package org.hzero.message.infra.mapper;

import java.util.List;

import org.hzero.message.api.dto.ReceiverTypeDTO;
import org.hzero.message.domain.entity.ReceiverType;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 接收者类型Mapper
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
public interface ReceiverTypeMapper extends BaseMapper<ReceiverType> {
    /**
     *
     * @param receiverType
     * @return
     */
    List<ReceiverTypeDTO> selectReceiverType(ReceiverType receiverType);
}
