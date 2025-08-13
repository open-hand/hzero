package org.hzero.iam.infra.repository.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.SecGrpRevoke;
import org.hzero.iam.domain.repository.SecGrpRevokeRepository;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityRevokeType;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.SecGrpRevokeMapper;
import org.hzero.iam.infra.util.BatchSqlHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 安全组权限回收 资源库实现
 *
 * @author xingxing.wu@hand-china.com  2019-10-31 14:00:03
 */
@Component
public class SecGrpRevokeRepositoryImpl extends BaseRepositoryImpl<SecGrpRevoke> implements SecGrpRevokeRepository {
    @Autowired
    private SecGrpRevokeMapper secGrpRevokeMapper;

    @Override
    public List<SecGrpRevoke> selectShieldedAuthority(Long secGrpId, Long shieldRoleId, SecGrpAuthorityType secGrpAuthorityType) {
        return secGrpRevokeMapper.selectShieldedAuthority(secGrpId, shieldRoleId, secGrpAuthorityType.value());
    }

    @Override
    public void batchAdd(List<SecGrpRevoke> revokes) {
        if (CollectionUtils.isEmpty(revokes)) {
            return;
        }
        UserUtils.setDataUser(revokes);

        BatchSqlHelper.batchExecute(revokes, 7,
                (dataList) -> secGrpRevokeMapper.batchInsertBySql(dataList),
                "BatchInsertSecGrpRevoke");
    }

    @Override
    public void batchRemove(Long roleId, Long secGrpId, Set<Long> authorityIds,
                            SecGrpAuthorityRevokeType revokeType, SecGrpAuthorityType authorityType) {
        if (CollectionUtils.isEmpty(authorityIds) || revokeType == null || authorityType == null) {
            return;
        }
        secGrpRevokeMapper.batchDeleteBySql(roleId, secGrpId, authorityIds, revokeType.value(), authorityType.value());
    }

}
