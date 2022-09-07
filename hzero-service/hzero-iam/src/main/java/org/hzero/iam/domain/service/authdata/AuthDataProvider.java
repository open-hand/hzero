package org.hzero.iam.domain.service.authdata;

import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;
import org.springframework.core.Ordered;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 权限管理提供器
 *
 * @author bo.he02@hand-china.com 2020/05/26 10:21
 */
public interface AuthDataProvider extends Ordered {
    /**
     * 获取权限类型码
     *
     * @return 权限类型码
     */
    String getAuthorityTypeCode();

    /**
     * 查询权限数据
     *
     * @param authorityTypeCode 权限类型码
     * @param authDataCondition 权限数据对象
     * @return 查询的数据ID结果
     */
    List<AuthDataVo> findAuthData(@Nonnull String authorityTypeCode, @Nonnull AuthDataCondition authDataCondition);
}
