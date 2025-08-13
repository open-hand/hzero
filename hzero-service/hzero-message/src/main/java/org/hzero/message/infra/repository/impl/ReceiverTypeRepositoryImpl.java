package org.hzero.message.infra.repository.impl;

import org.hzero.message.domain.entity.ReceiverTypeLine;
import org.hzero.message.infra.mapper.ReceiverTypeLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.message.api.dto.ReceiverTypeDTO;
import org.hzero.message.domain.entity.ReceiverType;
import org.hzero.message.domain.repository.ReceiverTypeRepository;
import org.hzero.message.infra.mapper.ReceiverTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 接收者类型 资源库实现
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
@Component
public class ReceiverTypeRepositoryImpl extends BaseRepositoryImpl<ReceiverType> implements ReceiverTypeRepository {
    private ReceiverTypeMapper receiverTypeMapper;
    private ReceiverTypeLineMapper receiverTypeLineMapper;

    @Autowired
    public ReceiverTypeRepositoryImpl(ReceiverTypeMapper receiverTypeMapper, ReceiverTypeLineMapper receiverTypeLineMapper) {
        this.receiverTypeMapper = receiverTypeMapper;
        this.receiverTypeLineMapper = receiverTypeLineMapper;
    }

    @Override
    public Page<ReceiverTypeDTO> selectReceiverType(PageRequest pageRequest, ReceiverType receiverType) {

        return PageHelper.doPageAndSort(pageRequest, () -> receiverTypeMapper.selectReceiverType(receiverType));
    }

    @Override
    public List<ReceiverTypeLine> listReceiveTypeLine(Long receiverTypeId) {
        return receiverTypeLineMapper.listReceiveTypeLine(receiverTypeId);
    }
}
