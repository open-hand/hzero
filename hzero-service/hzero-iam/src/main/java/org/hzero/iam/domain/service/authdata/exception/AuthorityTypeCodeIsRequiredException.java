package org.hzero.iam.domain.service.authdata.exception;

import io.choerodon.core.exception.CommonException;

/**
 * 权限类型码是必须的
 *
 * @author bo.he02@hand-china.com 2020/05/26 11:15
 */
public class AuthorityTypeCodeIsRequiredException extends CommonException {

    public AuthorityTypeCodeIsRequiredException() {
        super("hiam.error.auth_data.authority_type_code.required");
    }
}
