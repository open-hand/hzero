package org.hzero.iam.domain.service.authdata.exception;

import io.choerodon.core.exception.CommonException;

/**
 * 没有可使用的权限数据提供器处理当前请求
 *
 * @author bo.he02@hand-china.com 2020/05/26 11:22
 */
public class NotProviderProcessException extends CommonException {

    public NotProviderProcessException(String authorityTypeCode, String authData) {
        super("hiam.error.auth_data.not_provider_process", authorityTypeCode, authData);
    }
}
