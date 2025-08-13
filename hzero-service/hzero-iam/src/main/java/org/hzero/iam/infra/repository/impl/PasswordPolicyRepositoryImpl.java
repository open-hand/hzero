package org.hzero.iam.infra.repository.impl;

import org.apache.commons.lang.BooleanUtils;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.repository.PasswordPolicyRepository;
import org.hzero.iam.domain.vo.PasswordPolicyVO;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PasswordPolicyRepositoryImpl extends BaseRepositoryImpl<PasswordPolicy> implements PasswordPolicyRepository {

    private final BasePasswordPolicyRepository basePasswordPolicyRepository;

    public PasswordPolicyRepositoryImpl(BasePasswordPolicyRepository basePasswordPolicyRepository) {
        this.basePasswordPolicyRepository = basePasswordPolicyRepository;
    }

    @Override
    public PasswordPolicy selectTenantPasswordPolicy(Long tenantId) {
        PasswordPolicy params = new PasswordPolicy();
        params.setOrganizationId(tenantId);
        return selectOne(params);
    }

    @Override
    public void initCachePasswordPolicy() {
        List<PasswordPolicy> passwordPolicyList = this.selectAll();
        List<BasePasswordPolicy> list = passwordPolicyList.stream().map(pp -> {
            BasePasswordPolicy bpp = new BasePasswordPolicy();
            BeanUtils.copyProperties(pp, bpp);
            return bpp;
        }).collect(Collectors.toList());
        basePasswordPolicyRepository.batchSavePasswordPolicy(list);
    }

    @Override
    public void cachePasswordPolicy(PasswordPolicy passwordPolicy) {
        BasePasswordPolicy basePasswordPolicy = new BasePasswordPolicy();
        BeanUtils.copyProperties(passwordPolicy, basePasswordPolicy);
        basePasswordPolicyRepository.savePasswordPolicy(basePasswordPolicy);
    }

    @Override
    public PasswordPolicyVO queryPasswordPolicy(Long tenantId) {
        PasswordPolicy params = new PasswordPolicy();
        PasswordPolicyVO passwordPolicyVO = new PasswordPolicyVO();
        params.setOrganizationId(tenantId);
        params = selectOne(params);
        // 组装passwordPolicyVO对象
        if (params != null && params.getEnablePassword()) {
            // 默认值置为空
            passwordPolicyVO.setDigitsCount(Optional.ofNullable(params.getDigitsCount()).orElse(0) > 0
                    ? params.getDigitsCount() : null);
            passwordPolicyVO.setLowercaseCount(Optional.ofNullable(params.getLowercaseCount()).orElse(0) > 0
                    ? params.getLowercaseCount() : null);
            passwordPolicyVO.setUppercaseCount(Optional.ofNullable(params.getUppercaseCount()).orElse(0) > 0
                    ? params.getUppercaseCount() : null);
            passwordPolicyVO.setMaxLength(Optional.ofNullable(params.getMaxLength()).orElse(0) > 0
                    ? params.getMaxLength() : null);
            passwordPolicyVO.setMinLength(Optional.ofNullable(params.getMinLength()).orElse(0) > 0
                    ? params.getMinLength() : null);
            passwordPolicyVO.setSpecialCharCount(Optional.ofNullable(params.getSpecialCharCount()).orElse(0) > 0
                    ? params.getSpecialCharCount() : null);
            passwordPolicyVO.setNotUsername(params.getNotUsername());
            passwordPolicyVO.setLoginAgain(BooleanUtils.isTrue(params.getLoginAgain()));
            passwordPolicyVO.setForceCodeVerify(BooleanUtils.isTrue(params.getForceCodeVerify()));
            passwordPolicyVO.setEnableThreeRole(params.getEnableThreeRole());

            passwordPolicyVO.setEnableRoleInherit(BooleanUtils.isTrue(params.getEnableRoleInherit()));
            passwordPolicyVO.setEnableRoleAllocate(BooleanUtils.isTrue(params.getEnableRoleAllocate()));
            passwordPolicyVO.setEnableRolePermission(BooleanUtils.isTrue(params.getEnableRolePermission()));
        }
        return passwordPolicyVO;
    }
}
