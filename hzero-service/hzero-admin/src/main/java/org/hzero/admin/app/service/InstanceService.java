package org.hzero.admin.app.service;

import org.hzero.admin.api.dto.InstanceDTO;
import org.hzero.admin.api.dto.InstanceDetailDTO;
import org.hzero.admin.api.dto.condition.InstanceQueryDTO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 服务实例服务接口
 *
 * @author modify by bo.he02@hand-china.com on 2020/05/12
 * @author flyleft
 */
public interface InstanceService {
    /**
     * 按条件查询满足条件的实例对象
     *
     * @param service          服务名称
     * @param instanceQueryDTO 查询条件对象
     * @param pageRequest      分页对象
     * @return 查询到的实例数据
     */
    Page<InstanceDTO> listByOptions(String service, InstanceQueryDTO instanceQueryDTO, PageRequest pageRequest);

    /**
     * 查询实例的详细信息
     *
     * @param instanceId 实例ID
     * @return 查询到的实例详细信息
     */
    InstanceDetailDTO query(String instanceId);
}