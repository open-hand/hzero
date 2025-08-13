package org.hzero.boot.oauth.strategy.login;

import org.springframework.stereotype.Component;

import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.service.PasswordErrorTimesService;
import org.hzero.boot.oauth.policy.PasswordPolicyMap;
import org.hzero.boot.oauth.policy.PasswordPolicyType;
import org.hzero.boot.oauth.strategy.PasswordStrategy;

/**
 * @author wuguokai
 */
@Component
public class MaxErrorTimeStrategy implements PasswordStrategy {
    private static final String TYPE = PasswordPolicyType.MAX_ERROR_TIME.getValue();
    private static final String ENABLE_LOCK_TYPE = PasswordPolicyType.ENABLE_LOCK.getValue();

    private PasswordErrorTimesService passwordErrorTimesService;

    public MaxErrorTimeStrategy(PasswordErrorTimesService passwordErrorTimesService) {
        this.passwordErrorTimesService = passwordErrorTimesService;
    }

    @Override
    public Boolean validate(PasswordPolicyMap policyMap, BaseUser user, String password) {
        Integer max = (Integer) policyMap.getLoginConfig().get(TYPE);
        Boolean enableLock = (Boolean) policyMap.getLoginConfig().get(ENABLE_LOCK_TYPE);
        if (enableLock && user.getId() != null) {
            long errorTimes = passwordErrorTimesService.getErrorTimes(user.getId());
            return errorTimes < max;
        }
        return true;
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
