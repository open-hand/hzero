package org.hzero.iam.domain.service.user;

import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.domain.entity.User;

import io.choerodon.core.exception.CommonException;

/**
 * 内部调用创建用户服务
 *
 * @author bojiangzhou 2019/04/22
 */
public class UserCreateInternalService extends UserCreateService {

    @Override
    public User createUser(User user) {
        LOGGER.info("create user internal...");
        return super.createUser(user);
    }

    @Override
    protected void checkValidity(User user) {
        // 内部创建需自行传入组织ID
        if (user.getOrganizationId() == null) {
            throw new CommonException("hiam.warn.user.parameterNotBeNull", "organizationId");
        }
        super.checkValidity(user);
    }

    @Override
    protected void checkMemberRole(User user) {
        // not check.
    }

    @Override
    protected void checkLoginName(User user) {
        String loginName = user.getLoginName();
        if (StringUtils.isBlank(loginName)) {
            return;
        }
        if (userRepository.existsByLoginName(user.getLoginName())) {
            throw new CommonException("hiam.warn.user.loginNameExists");
        }
    }
}
