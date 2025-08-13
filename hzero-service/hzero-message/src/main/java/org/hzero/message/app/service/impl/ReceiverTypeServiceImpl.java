package org.hzero.message.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.api.dto.ReceiverTypeDTO;
import org.hzero.message.app.service.ReceiverTypeService;
import org.hzero.message.domain.entity.ReceiverType;
import org.hzero.message.domain.repository.ReceiverTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 接收者类型应用服务默认实现
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
@Service
public class ReceiverTypeServiceImpl implements ReceiverTypeService {

    private final ReceiverTypeRepository receiverTypeRepository;

    @Autowired
    public ReceiverTypeServiceImpl(ReceiverTypeRepository receiverTypeRepository) {
        this.receiverTypeRepository = receiverTypeRepository;

    }

    @Override
    public Page<ReceiverTypeDTO> pageAndSort(PageRequest pageRequest, ReceiverType receiverType) {
        return receiverTypeRepository.selectReceiverType(pageRequest, receiverType);
    }

    @Override
    public ReceiverType getReceiverType(Long tenantId, String typeCode) {
        ReceiverType receiverType = receiverTypeRepository.select(new ReceiverType().setTenantId(tenantId).setTypeCode(typeCode)).
                stream().findFirst().orElse(null);
        if (receiverType != null || Objects.equals(BaseConstants.DEFAULT_TENANT_ID, tenantId)) {
            return receiverType;
        }
        return receiverTypeRepository.select(new ReceiverType().setTenantId(BaseConstants.DEFAULT_TENANT_ID).setTypeCode(typeCode)).
                stream().findFirst().orElse(null);
    }
}
