package org.hzero.message.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.message.domain.entity.ReceiverTypeLine;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.message.api.dto.ReceiverTypeDTO;
import org.hzero.message.domain.entity.ReceiverType;

import java.util.List;

/**
 * 接收者类型资源库
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
public interface ReceiverTypeRepository extends BaseRepository<ReceiverType> {
    /**
     * @param pageRequest
     * @param receiverType
     * @return
     */
    Page<ReceiverTypeDTO> selectReceiverType(PageRequest pageRequest, ReceiverType receiverType);

    /**
     * 获取接收者行
     *
     * @param receiverTypeId 接收者类型ID
     * @return 接收者行
     */
    List<ReceiverTypeLine> listReceiveTypeLine(Long receiverTypeId);
}
