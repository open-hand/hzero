package org.hzero.admin.infra.exception;

import io.choerodon.core.exception.CommonException;

/**
 * @author XCXCXCXCX
 * @date 2020/6/19 12:04 下午
 */
public class ServiceSkipInitializationException extends CommonException {

    public ServiceSkipInitializationException(String service) {
        super("The service[" + service + "] should not perform initialization!");
    }

    public ServiceSkipInitializationException(String service, Throwable cause) {
        super("The service[" + service + "] should not perform initialization!", cause);
    }
}
