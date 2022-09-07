package org.hzero.iam.infra.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.repository.SecGrpDclRepository;
import org.hzero.iam.infra.mapper.SecGrpDclMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;

/**
 * 安全组数据权限 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Component
public class SecGrpDclRepositoryImpl extends BaseRepositoryImpl<SecGrpDcl> implements SecGrpDclRepository {
    @Autowired
    private SecGrpDclMapper secGrpDclMapper;

    @Override
    public List<CompanyOuInvorgNodeDTO> listSecGrpCompanyDcl(Long secGrpId, Integer selfDim, SecGrpDclQueryDTO queryDTO) {
        queryDTO.setSecGrpId(secGrpId);
        queryDTO.setSelfDim(selfDim);
        return secGrpDclMapper.selectSecGrpCompanyDcl(queryDTO);
    }

    @Override
    public List<CompanyOuInvorgNodeDTO> listSecGrpAssignedCompanyDcl(Long secGrpId, SecGrpDclQueryDTO queryDTO) {
        return secGrpDclMapper.selectSecGrpAssignedCompanyDcl(queryDTO);
    }

    @Override
    public List<CompanyOuInvorgNodeDTO> listRoleSecGrpCompanyDcl(Long roleId, Long secGrpId, SecGrpDclQueryDTO queryDTO) {
        queryDTO.setSecGrpId(secGrpId);
        return secGrpDclMapper.selectRoleSecGrpCompanyDcl(queryDTO);
    }



    @Override
    public List<SecGrpDcl> selectBySecGrpId(Long secGrpId) {
        if (secGrpId == null) {
            return new ArrayList<>();
        }
        SecGrpDcl secGrpDcl = new SecGrpDcl();
        secGrpDcl.setSecGrpId(secGrpId);
        return secGrpDclMapper.select(secGrpDcl);
    }

    @Override
    public SecGrpDcl queryOne(Long secGrpId, String authorityTypeCode) {
        SecGrpDcl query = new SecGrpDcl().setSecGrpId(secGrpId).setAuthorityTypeCode(authorityTypeCode);
        return this.selectOneOptional(query,
                new Criteria()
                        .select(
                                SecGrpDcl.FIELD_SEC_GRP_DCL_ID,
                                SecGrpDcl.FIELD_SEC_GRP_ID,
                                SecGrpDcl.FIELD_TENANT_ID,
                                SecGrpDcl.FIELD_AUTHORITY_TYPE_CODE,
                                SecGrpDcl.FIELD_INCLUDE_ALL_FLAG
                        )
                        .where(
                                SecGrpDcl.FIELD_SEC_GRP_ID,
                                SecGrpDcl.FIELD_AUTHORITY_TYPE_CODE
                        )
        );
    }
}
