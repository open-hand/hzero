package org.hzero.iam.domain.service.authdata;

import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;

import java.util.List;

/**
 * 权限数据管理器
 *
 * @author bo.he02@hand-china.com 2020/05/26 10:21
 */
public interface AuthDataManager {
    /**
     * 初始化权限数据管理器
     *
     * @param authDataProviders 权限数据提供器实现
     */
    void init(List<AuthDataProvider> authDataProviders);

    /**
     * 查询权限数据
     *
     * @param authorityTypeCode 权限类型码
     * @param authDataCondition 权限数据对象
     * @return 查询的数据ID结果
     */
    List<AuthDataVo> findAuthData(String authorityTypeCode, AuthDataCondition authDataCondition);
}
