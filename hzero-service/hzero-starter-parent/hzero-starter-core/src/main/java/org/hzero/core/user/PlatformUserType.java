package org.hzero.core.user;

/**
 * 平台用户
 *
 * @author bojiangzhou 2019/10/28
 */
public final class PlatformUserType extends UserType {

    private static final String USER_TYPE = "P";

    @Override
    public String value() {
        return USER_TYPE;
    }
}
