package org.hzero.boot.oauth.strategy.password;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.repository.BasePasswordHistoryRepository;
import org.hzero.boot.oauth.policy.PasswordPolicyMap;
import org.hzero.boot.oauth.policy.PasswordPolicyType;
import org.hzero.boot.oauth.strategy.PasswordStrategy;

/**
 * @author wuguokai
 */
@Component
public class NotRecentStrategy implements PasswordStrategy {
    private static final String ERROR_MESSAGE = "hoth.warn.password.policy.notRecent";
    public static final String TYPE = PasswordPolicyType.NOT_RECENT.getValue();

    private final PasswordEncoder passwordEncoder;
    private final BasePasswordHistoryRepository basePasswordHistoryRepository;

    public NotRecentStrategy(PasswordEncoder passwordEncoder,
                             BasePasswordHistoryRepository basePasswordHistoryRepository) {
        this.passwordEncoder = passwordEncoder;
        this.basePasswordHistoryRepository = basePasswordHistoryRepository;
    }

    @Override
    public Object validate(PasswordPolicyMap policyMap, BaseUser user, String password) {
        Integer recentPasswordCount = (Integer) policyMap.getPasswordConfig().get(TYPE);
        if (recentPasswordCount > 0 && user.getId() != null) {
            List<String> passwordHistoryList = basePasswordHistoryRepository.selectUserHistoryPassword(user.getId());
            int count = 0;
            for (String recentPassword : passwordHistoryList) {
                if (passwordEncoder.matches(password, recentPassword)) {
                    throw new CommonException(ERROR_MESSAGE, recentPasswordCount);
                }
                if (++count >= recentPasswordCount) {
                    break;
                }
            }
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
