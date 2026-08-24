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
 * 数据源权限数据提供器
 *
 * @author bo.he02@hand-china.com 2020/05/26 17:08
 */
@Component
public class DataSourceAuthDataProvider extends AbstractAuthDataProvider {
    @Autowired
    public DataSourceAuthDataProvider(AuthDataRepository authDataRepository) {
        super(authDataRepository);
    }

    @Override
    public String getAuthorityTypeCode() {
        return Constants.AUTHORITY_TYPE_CODE.DATA_SOURCE;
    }

    @Override
    public List<AuthDataVo> findAuthData(@Nonnull String authorityTypeCode, @Nonnull AuthDataCondition authDataCondition) {
        return this.singleAuthDataVo(this.getAuthDataRepository().queryDataSourceInfo(authDataCondition));
    }
}
