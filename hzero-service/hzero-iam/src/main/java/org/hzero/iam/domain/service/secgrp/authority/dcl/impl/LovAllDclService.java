package org.hzero.iam.domain.service.secgrp.authority.dcl.impl;

import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.service.secgrp.authority.dcl.AbstractLovDclService;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 安全组数据权限服务 —— 值集：全部
 *
 * @author bo.he02@hand-china.com 2020/04/03 14:00
 */
@Component
public class LovAllDclService extends AbstractLovDclService {
    /**
     * 设置维度编码：因为数据来源类型为值集的维度处理的是多种不同的数据来源，要区分这些来源，就需要使用维度码
     */
    private final ThreadLocal<String> dimensionCode = new ThreadLocal<>();

    @Override
    public boolean support(@Nonnull String authorityTypeCode, @Nullable DocTypeDimension docTypeDimension) {
        if (Objects.isNull(docTypeDimension)) {
            throw new CommonException("Doc Type Dimension Not Exists, Dimension Code Is [{0}]", authorityTypeCode);
        }

        // 判断来源是否为值集
        boolean support = this.getValueSourceType().equals(docTypeDimension.getValueSourceType());
        if (support) {
            this.dimensionCode.set(authorityTypeCode);
        }

        return support;
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignableAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest) {
        // 是否是自己创建的dim
        boolean selfDim = this.secGrpDclDimRepository.isSelfManagementDim(queryDTO.getSecGrpId(),
                queryDTO.getRoleId(), Constants.DocLocalAuthorityTypeCode.PURAGENT);
        if (selfDim || this.isSuperAdmin(queryDTO.getRoleId())) {
            // todo 值集来源为lov类型数据控制需实现，自己创建的dim和管理员可以查询全量数据
            return null;
        } else {
            // todo 能查询到的数据受到父级的控制
            return null;
        }
    }

    @Override
    public String getAuthorityTypeCode() {
        return this.dimensionCode.get();
    }

    @Override
    public void clear() {
        // 清理线程变量
        this.dimensionCode.remove();
    }
}
