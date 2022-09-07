package org.hzero.boot.oauth.user;

import io.choerodon.core.oauth.CustomUserDetails;
import org.springframework.core.PriorityOrdered;

/**
 * 用户后置处理
 *
 * @author 废柴 2020/11/6 10:59
 */
public interface UserPostProcessor extends PriorityOrdered, Comparable<UserPostProcessor> {

    /**
     * @return 执行的顺序，越小优先级越高
     */
    @Override
    default int getOrder() {
        return 0;
    }

    /**
     * 在 Wrapper 之前执行
     *
     * @param customUserDetails 用户信息
     */
    default void beforeWrapper(CustomUserDetails customUserDetails) {
    }

    /**
     * 在 Wrapper 之后执行
     *
     * @param customUserDetails 用户信息
     */
    default void afterWrapper(CustomUserDetails customUserDetails) {
    }

    @Override
    default int compareTo(UserPostProcessor o) {
        return this.getOrder() - o.getOrder();
    }
}
