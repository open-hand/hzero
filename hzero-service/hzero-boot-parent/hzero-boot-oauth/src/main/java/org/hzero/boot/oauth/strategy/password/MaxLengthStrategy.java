package org.hzero.boot.oauth.strategy.password;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.policy.PasswordPolicyMap;
import org.hzero.boot.oauth.policy.PasswordPolicyType;
import org.hzero.boot.oauth.strategy.PasswordStrategy;

/**
 * @author wuguokai
 */
@Component
public class MaxLengthStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "hoth.warn.password.policy.maxLength";
    private static final String TYPE = PasswordPolicyType.MAX_LENGTH.getValue();

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUser user, String password) {
        Integer max = (Integer) policyMap.getPasswordConfig().get(TYPE);
        if (max != null && max != 0 && password.length() > max) {
            throw new CommonException(ERROR_MESSAGE, max);
        }
        return null;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public Object parseConfig(Object value) {
        return null;
    }
}
