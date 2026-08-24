package org.hzero.iam.domain.service.authdata;

import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;

/**
 * 权限数据参数对象转换器
 *
 * @author bo.he02@hand-china.com 2020/05/27 9:09
 */
public interface AuthDataVoConverter<P> {
    /**
     * 通过参数对象转换成权限数据参数对象
     *
     * @param paramObject 参数对象
     * @return 权限数据参数对象
     */
    AuthDataCondition convert(P paramObject);
}
