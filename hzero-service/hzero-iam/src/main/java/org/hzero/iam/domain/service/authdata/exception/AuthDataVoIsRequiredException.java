package org.hzero.iam.domain.service.authdata.exception;

import io.choerodon.core.exception.CommonException;

/**
 * 权限类型对象是必须的
 *
 * @author bo.he02@hand-china.com 2020/05/26 11:17
 */
public class AuthDataVoIsRequiredException extends CommonException {

    public AuthDataVoIsRequiredException() {
        super("hiam.error.auth_data.auth_data_vo.required");
    }
}
