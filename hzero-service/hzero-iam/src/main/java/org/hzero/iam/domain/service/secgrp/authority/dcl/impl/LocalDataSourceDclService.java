package org.hzero.iam.domain.service.secgrp.authority.dcl.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.iam.domain.service.secgrp.authority.dcl.AbstractLocalDclService;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * 安全组数据权限服务 —— 本地编码：数据源
 *
 * @author bojiangzhou 2020/03/04
 */
@Component
public class LocalDataSourceDclService extends AbstractLocalDclService {
    @Override
    public SecGrpDclDTO querySecGrpDclAssignableAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        // 是否是自己创建的dim
        boolean selfDim = this.secGrpDclDimRepository.isSelfManagementDim(queryDTO.getSecGrpId(),
                queryDTO.getRoleId(), Constants.DocLocalAuthorityTypeCode.DATA_SOURCE);
        if (selfDim || this.isSuperAdmin(queryDTO.getRoleId())) {
            SecGrpDclDTO result = new SecGrpDclDTO();
            Page<SecGrpDclLine> page = PageHelper.doPageAndSort(pageRequest, () -> secGrpDclLineMapper.selectGlobalAssignableDatasource(queryDTO));
            result.setSecGrpDclLineList(page);
            return result;
        }
        return super.querySecGrpDclAssignableAuthority(queryDTO, pageRequest);
    }

    @Override
    protected String getAuthorityTypeCode() {
        return Constants.DocLocalAuthorityTypeCode.DATA_SOURCE;
    }
}
