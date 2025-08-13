package org.hzero.iam.app.service.impl;

import io.choerodon.mybatis.domain.AuditDomain;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.app.service.SecGrpAclFieldService;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.service.secgrp.authority.impl.SecGrpFieldAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hzero.iam.infra.constant.Constants.SecGrpAssignTypeCode.SELF;

/**
 * 安全组字段权限应用服务默认实现
 *
 * @author bojiangzhou 2020/02/18
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@Service
public class SecGrpAclFieldServiceImpl implements SecGrpAclFieldService {
    @Autowired
    private SecGrpFieldAuthorityService fieldAuthorityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveSecGrpField(Long tenantId, Long secGrpId, List<SecGrpAclField> secGrpFields) {
        if (CollectionUtils.isEmpty(secGrpFields)) {
            return;
        }

        //安装操作类型进行分组，通过_status字段进行限定
        Map<AuditDomain.RecordStatus, List<SecGrpAclField>> statusListMap = secGrpFields.stream().collect(Collectors.groupingBy(AuditDomain::get_status));
        List<SecGrpAclField> insertList = statusListMap.get(AuditDomain.RecordStatus.create);
        List<SecGrpAclField> updateList = statusListMap.get(AuditDomain.RecordStatus.update);
        List<SecGrpAclField> deleteList = statusListMap.get(AuditDomain.RecordStatus.delete);

        if (CollectionUtils.isNotEmpty(insertList)) {
            insertList.forEach(item -> {
                item.setTenantId(tenantId);
                item.setSecGrpId(secGrpId);
                item.setAssignTypeCode(SELF);
            });
            fieldAuthorityService.addSecGrpAuthority(secGrpId, insertList);
        }

        if (CollectionUtils.isNotEmpty(updateList)) {
            updateList.forEach(item -> {
                item.setTenantId(tenantId);
                item.setSecGrpId(secGrpId);
            });
            fieldAuthorityService.updateSecGrpAuthority(secGrpId, updateList);
        }

        if (CollectionUtils.isNotEmpty(deleteList)) {
            fieldAuthorityService.removeSecGrpAuthority(secGrpId, deleteList);
        }
    }

}
