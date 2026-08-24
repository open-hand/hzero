package org.hzero.iam.domain.service.pwdpolicy;

import org.hzero.core.observer.Observer;
import org.hzero.iam.domain.entity.PasswordPolicy;

/**
 * 密码策略更新观察者
 *
 * @author bojiangzhou 2020/07/13
 */
public interface PasswordPolicyObserver extends Observer<PasswordPolicy> {
}
