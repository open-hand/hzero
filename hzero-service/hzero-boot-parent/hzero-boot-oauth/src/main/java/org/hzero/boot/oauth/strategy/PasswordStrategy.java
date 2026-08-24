package org.hzero.boot.oauth.strategy;

import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.policy.PasswordPolicyMap;

/**
 * @author wuguokai
 */
public interface PasswordStrategy {

    <T> T validate(PasswordPolicyMap policyMap, BaseUser user, String password);

    String getType();

    Object parseConfig(Object value);
}
