package org.hzero.message.app.service;

import org.hzero.message.api.dto.ReceiverTypeDTO;
import org.hzero.message.domain.entity.ReceiverType;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 接收者类型应用服务
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
public interface ReceiverTypeService {

    /**
     * 获取ReceiverTypeDTO分页对象
     *
     * @param pageRequest  分页对象
     * @param receiverType receiverType
     * @return eceiverTypeDTO分页对象
     */
    Page<ReceiverTypeDTO> pageAndSort(PageRequest pageRequest, ReceiverType receiverType);

    /**
     * 获取接收者类型
     *
     * @param tenantId 租户id
     * @param typeCode 接收者类型编码
     * @return 接收者类型
     */
    ReceiverType getReceiverType(Long tenantId, String typeCode);
}
