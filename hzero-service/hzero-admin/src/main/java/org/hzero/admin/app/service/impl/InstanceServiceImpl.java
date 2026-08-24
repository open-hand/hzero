package org.hzero.admin.app.service.impl;

import org.hzero.admin.api.dto.InstanceDTO;
import org.hzero.admin.api.dto.InstanceDetailDTO;
import org.hzero.admin.api.dto.condition.InstanceQueryDTO;
import org.hzero.admin.app.service.InstanceService;
import org.hzero.admin.domain.repository.InstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 服务实例服务接口实现
 *
 * @author modify by bo.he02@hand-china.com on 2020/05/12
 * @author flyleft
 */
@Service
public class InstanceServiceImpl implements InstanceService {
    /**
     * 服务实例资源库对象
     */
    private final InstanceRepository instanceRepository;

    @Autowired
    public InstanceServiceImpl(InstanceRepository instanceRepository) {
        this.instanceRepository = instanceRepository;
    }

    @Override
    public Page<InstanceDTO> listByOptions(String service, InstanceQueryDTO instanceQueryDTO, PageRequest pageRequest) {
        return this.instanceRepository.listByOptions(service, instanceQueryDTO, pageRequest);
    }

    @Override
    public InstanceDetailDTO query(String instanceId) {
        return this.instanceRepository.query(instanceId);
    }
}
