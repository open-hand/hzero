package org.hzero.iam.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.SecGrpDclDimDTO;
import org.hzero.iam.app.service.SecGrpDclDimService;
import org.hzero.iam.domain.entity.SecGrpDclDim;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.domain.service.secgrp.authority.impl.SecGrpDimAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全组数据权限维度应用服务默认实现
 *
 * @author bojiangzhou 2020/02/18
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Service
public class SecGrpDclDimServiceImpl implements SecGrpDclDimService {

    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private SecGrpDimAuthorityService dimAuthorityService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSecGrpDim(Long tenantId, Long secGrpId, List<SecGrpDclDimDTO> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return;
        }

        // 删除的维度范围数据
        List<SecGrpDclDim> deleteList = new ArrayList<>(dtos.size());
        // 更新的维度范围数据
        List<SecGrpDclDim> updateList = new ArrayList<>(dtos.size());

        // 对维度范围进行分类
        for (SecGrpDclDimDTO dto : dtos) {
            dto.setTenantId(tenantId);
            dto.setSecGrpId(secGrpId);
            SecGrpDclDim dim = SecGrpDclDim.buildDim(dto);
            // 是否取消勾选
            if (BaseConstants.Flag.NO.equals(dto.getSecGrpDclDimCheckedFlag())) {
                deleteList.add(dim);
            } else {
                updateList.add(dim);
            }
        }

        if (CollectionUtils.isNotEmpty(deleteList)) {
            //处理删除的权限维度范围
            this.dimAuthorityService.removeSecGrpAuthority(secGrpId, deleteList);
        }

        if (CollectionUtils.isNotEmpty(updateList)) {
            // 处理更新的权限维度范围
            this.dimAuthorityService.updateSecGrpAuthority(secGrpId, updateList);
        }
    }
}
