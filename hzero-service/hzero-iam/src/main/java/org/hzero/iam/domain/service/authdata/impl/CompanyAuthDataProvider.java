package org.hzero.iam.domain.service.authdata.impl;

import org.hzero.iam.domain.repository.AuthDataRepository;
import org.hzero.iam.domain.service.authdata.AbstractAuthDataProvider;
import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 公司权限数据提供器实现
 *
 * @author bo.he02@hand-china.com 2020/05/26 11:30
 */
@Component
public class CompanyAuthDataProvider extends AbstractAuthDataProvider {

    @Autowired
    public CompanyAuthDataProvider(AuthDataRepository authDataRepository) {
        super(authDataRepository);
    }

    @Override
    public String getAuthorityTypeCode() {
        return Constants.AUTHORITY_TYPE_CODE.COMPANY;
    }

    @Override
    public List<AuthDataVo> findAuthData(@Nonnull String authorityTypeCode, @Nonnull AuthDataCondition authDataCondition) {
        return this.singleAuthDataVo(this.getAuthDataRepository().queryComDataSourceInfo(authDataCondition));
    }
}
